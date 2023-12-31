package com.backend.server.controller;

import com.backend.server.entity.User;
import com.backend.server.entity.pojo.Result;
import com.backend.server.entity.pojo.StatusCode;
import com.backend.server.mapper.UserMapper;
import com.backend.server.service.PortalService;
import com.backend.server.service.UserService;
import com.backend.server.utils.FormatUtil;
import com.backend.server.utils.JwtTokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Random;

@Api(tags = {"用户"})
@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private UserService userService;
    @Autowired
    private FormatUtil formatUtil;
    @Autowired
    private PortalService portalService;



    /**
     * 登录返回token
     */
    @ApiOperation("登录")
    @PostMapping("/login")
    public Result login(String username, String password) {
        if (!formatUtil.checkStringNull(username, password)) {
            return Result.create(StatusCode.ERROR, "参数错误");
        }
        try {
            Map<String, Object> map = userService.login(username, password);
            if(map==null) return Result.create(StatusCode.LOGIN_ERROR, "登录失败，用户名或密码错误");
            return Result.create(StatusCode.OK, "登录成功", map);
        } catch (RuntimeException ue) {
            return Result.create(StatusCode.LOGIN_ERROR, "登录失败，用户名或密码错误");
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result register(String username,String email, String password, String code) {
        boolean isTrue = portalService.checkMailCode(email, code);
        if (!isTrue) return Result.create(StatusCode.CODE_ERROR, "验证码错误");
        if (!formatUtil.checkStringNull(username,password,email)) {
            return Result.create(StatusCode.ERROR, "注册失败，字段不完整");
        }
        try {
            Map<String, Object> map = userService.register(username,email,password);
            return Result.create(StatusCode.OK, "注册成功",map);
        } catch (RuntimeException e) {
            return Result.create(StatusCode.ERROR, "注册失败，" + e.getMessage());
        }
    }

    /**
     * 获取个人信息
     */
    @GetMapping("/my_info")
    public Result getCurUser(){
        User user = userMapper.selectById(jwtTokenUtil.getUserIdFromRequest(request));
        return Result.create(StatusCode.OK, "获取成功",user);
    }

    /**
     * 修改个人信息
     */
    @PostMapping("/change_info")
    public Result updateUser(String nickname,String family_name,String name,String gender, String occupation, String institution){
        try {
            userService.updateInfo(nickname,family_name,name,gender,occupation,institution);
            return Result.create(StatusCode.OK, "更新成功");
        } catch (RuntimeException e) {
            return Result.create(StatusCode.ERROR, "更新失败，" + e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @PostMapping("/change_password")
    public Result updatePassword(String old_pwd, String new_pwd) {
        try {
            if(userService.updatePassword(old_pwd,new_pwd))
                return Result.create(StatusCode.OK, "修改密码成功");
            else return Result.create(StatusCode.ERROR, "旧密码错误");
        } catch (RuntimeException e) {
            return Result.create(StatusCode.ERROR, e.getMessage());
        }
    }

    /**
     * Token校检
     */
    @ResponseBody
    @RequestMapping(value = { "/checkToken" }, method = RequestMethod.POST)
    public int checkToken(@RequestParam(value = "authToken") String authToken) {
        if (JwtTokenUtil.isNotBlank(authToken)) {
            try {
                boolean flag = jwtTokenUtil.isTokenExpired(authToken);
                if (flag) return 201;// 过期
                else return 200; // 认证通过
            } catch (Exception e) {
                e.printStackTrace();
            }
        } return 202; //为空
    }

    /*=============================================================*/

    @GetMapping("/getUser/{userId}")
    public Result getUserByName(@PathVariable Integer userId){
        User user = userService.getUserById(userId);
        return Result.create(StatusCode.OK, "获取成功",user);
    }


    /**
     * 加密密码
     */
//    @GetMapping("/uppwd")
//    public Result uppwd(){
//        for(User u: userMapper.selectList(null)){
//            u.setPassword(UserService.getMd5Password(u.getPassword()));
//            userMapper.updateById(u);
//        }
//        return Result.create(StatusCode.OK,"成功");
//    }

    @PostMapping("/getId")
    public Result getUserIdByName(String userName){
        Integer uid = userService.getUserByName(userName).getId();
        return Result.create(StatusCode.OK,"成功",uid);
    }

    /**
     * 上传头像
     */
    @RequestMapping(value = "/uploadAvatar", method = RequestMethod.POST)
    @ResponseBody
    public Result uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            User user = userMapper.selectById(jwtTokenUtil.getUserIdFromRequest(request));
            String path="/home/zero/avatar/";
            String format = formatUtil.getFileFormat(file.getOriginalFilename());
            File file1 = new File(path + user.getUserName() + format);
            if(!file1.getParentFile().exists()){
                file1.getParentFile().mkdirs();
            }
            file.transferTo(file1);
            userService.updateAvatar("http://127.0.0.1/home/zero/avatar/"+user.getUserName()+format);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return Result.create(200, "上传头像成功");
    }

    @PostMapping("/retrievePassword")
    public Result retrievePassword(String email, String code) {
//        System.out.println("check code");
//        System.out.println("userId"+userId.toString());
//        System.out.println("aid"+aid);
//        System.out.println("email"+email);
//        System.out.println("code:"+code);
        //查看验证码是否正确，复用了门户服务中的发送验证码功能
//        System.out.println("验证码："+code);
//        System.out.println("邮箱："+ email);
        boolean isTrue = portalService.checkMailCode(email, code);
        if (!isTrue) return Result.create(StatusCode.CODE_ERROR, "验证码错误");
        //发送密码
        User user = userService.getUserByMail(email);
        if(user == null)
            return Result.create(404, "邮箱未被注册");
        Random _random = new Random();
        int random = _random.nextInt(899999) + 100001;
        String pwd = String.valueOf(random);
        user.setPassword(UserService.getMd5Password(pwd));
        userMapper.updateById(user);
        userService.sendMail(email,pwd);
        return Result.create(200, "success");
    }

}


