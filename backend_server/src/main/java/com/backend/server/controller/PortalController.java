package com.backend.server.controller;

import com.backend.server.entity.User;
import com.backend.server.entity.pojo.Change;
import com.backend.server.entity.pojo.PortalReturn;
import com.backend.server.entity.pojo.Result;
import com.backend.server.entity.pojo.StatusCode;
import com.backend.server.mapper.UserMapper;
import com.backend.server.service.AuthorService;
import com.backend.server.service.PaperService;
import com.backend.server.service.PortalService;
import com.backend.server.service.UserService;
import com.backend.server.utils.FormatUtil;
import com.backend.server.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequestMapping("/portal")
@RestController
public class PortalController {
    @Autowired
    private AuthorService authorService;

    @Autowired
    private PaperService paperService;

    @Autowired
    private PortalService portalService;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/test")
    public Result test() {
        return Result.create(200, "success");
    }

    /**
     * 查看作者信息
     * @param aid
     * @return
     */
    @PostMapping("/personal_center/academic_homepage/view")
    public Result checkPortal(String aid) {
        Map authors = authorService.queryAuthor(aid);
        List<Map> papers = paperService.queryPaperByAuthorId(aid);
        List<Map> paperReturn = new ArrayList<>();
        for (int i = 0; i < 5 && i < papers.size(); ++i) {
            Map paper = papers.get(i);
            paperReturn.add(paper);
        }
        PortalReturn portalReturn = new PortalReturn(authors, paperReturn);
        return Result.create(200, "返回成功", portalReturn);
    }


    /**
     * 查看门户
     * @param aid
     * @return
     */
    @PostMapping("/profile/view")
    public Result viewPortal(String aid) {
        System.out.println("11111aid:"+aid);
        Map authors = authorService.queryAuthor(aid);
        System.out.println(authors.get("index").toString());
        List<Map> papers = paperService.queryPaperByAuthorId(aid);
        System.out.println("111111papers.size:"+papers.size());
        PortalReturn portalReturn = new PortalReturn(authors, papers);
        return Result.create(200, "success", portalReturn);
    }
    
    /**
     * 根据名字完全匹配作者
     * @param key_word
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/personal_center/academic_homepage/search/{key_word}/{pageNum}/{pageSize}")
    public Result searchAuthorByName(@PathVariable String key_word, @PathVariable Integer pageNum, @PathVariable Integer pageSize) {
    	return Result.create(200, "查询成功", authorService.findAuthorByName(key_word, pageNum, pageSize));
    }
    
    /**
     * 修改门户信息
     * @param author
     * @return
     */
    @PostMapping("/profile/modify")
    public Result modifyPortal(@RequestBody Change author) {
        authorService.updateById(author);
        return Result.create(200, "success");
    }


    /**
     * 校验码验证
     * @param userId
     * @param aid
     * @param email
     * @param code
     * @return
     */

    @PostMapping("/personal_center/academic_homepage/check")
    public Result certificationPortal(Integer userId, String aid, String email, String code) {
        System.out.println("check code");
        System.out.println("userId"+userId.toString());
        System.out.println("aid"+aid);
        System.out.println("email"+email);
        System.out.println("code:"+code);
        //查看验证码是否正确
        boolean isTrue = portalService.checkMailCode(email, code);
        if (!isTrue) return Result.create(StatusCode.CODE_ERROR, "验证码错误");
        //成功关联用户与门户
        String username = userService.getUserById(userId).getName();
        authorService.bindUser(userId, username, aid);
        userService.bindPortal(userId, aid);
        System.out.println("bind success");
        return Result.create(200, "success");
    }

    /**
     * 发送邮箱验证
     * @param mail
     * @return
     */
    @PostMapping("/personal_center/academic_homepage/bind")
    public Result sendmail(String mail){
        if (!FormatUtil.checkMail(mail)) return Result.create(StatusCode.INFORMATION_ERROR, "邮箱格式错误");
        //String redisMailCode = redisTemplate.opsForValue().get("MAIL_" + mail);
        //if(redisMailCode!=null){
         //   return Result.create(200,"请稍后再发送");
        //}else{
            System.out.println("controller:发送邮件");
            portalService.sendMail(mail);
            System.out.println("controller:发送邮件over");
            return Result.create(200,"发送成功");
       // }
    }

    /**
     * 发送用户已绑定邮箱验证
     * @return
     */
    @GetMapping("/personal_center/academic_homepage/sendUsermail")
    public Result sendUsermail(){
        User user = userMapper.selectById(jwtTokenUtil.getUserIdFromRequest(request));
        String mail = user.getMail();
        if (!FormatUtil.checkMail(mail)) return Result.create(StatusCode.INFORMATION_ERROR, "用户未绑定邮箱！");
        //String redisMailCode = redisTemplate.opsForValue().get("MAIL_" + mail);
        //if(redisMailCode!=null){
        //   return Result.create(200,"请稍后再发送");
        //}else{
        System.out.println("controller:发送邮件");
        portalService.sendMail(mail);
        System.out.println("controller:发送邮件over");
        return Result.create(200,"发送成功");
        // }
    }


    @PostMapping("/personal_center/academic_homepage/unbind")
    public Result unBindPortal(String aid) {
        authorService.unBindById(aid);
        userService.unBindById(aid);
        return Result.create(200,"解绑成功");
    }

    /**
     * 创建索引
     */
    @PostMapping("/cridx")
    public Result cri() {
//        mongoTemplate.indexOps(Paper.class).ensureIndex(new Index().on("pid", Sort.Direction.ASC));
        return Result.create(200,"成功");
    }
}
