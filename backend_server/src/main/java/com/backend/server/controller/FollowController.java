package com.backend.server.controller;

import com.backend.server.entity.Author;
import com.backend.server.entity.User;
import com.backend.server.entity.pojo.Result;
import com.backend.server.entity.pojo.StatusCode;
import com.backend.server.service.FollowService;
import com.backend.server.service.UserService;
import com.backend.server.service.NoticeService;
import com.backend.server.utils.FormatUtil;
import com.backend.server.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("follow")
public class FollowController {
    @Autowired
    private FollowService followService;
    @Autowired
    private FormatUtil formatUtil;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private UserService userService;
    @Autowired
    private NoticeService noticeService;
    /**
     * 查询关注列表
     */
    @GetMapping("/my_following")
    public Result getFollowList() {
        List<Author> result = followService.getFollowList();
        return Result.create(StatusCode.OK, "查询成功", result);
    }

    /**
     * 关注用户
     */
    @PostMapping("/follow_scholar")
    public Result newFollow(String person_id) {
        if (!formatUtil.checkStringNull(person_id)) {
            return Result.create(StatusCode.ERROR, "person_id为空");
        }
        Integer followerId = jwtTokenUtil.getUserIdFromRequest(request);
        try {
            if(followService.isFollowed(followerId,person_id))
                return Result.create(StatusCode.OK,"已关注该学者");
            User result = userService.getUserByAid(person_id);


            if(result != null){
                if(Objects.equals(followerId, result.getId())){
                    return Result.create(StatusCode.ERROR,"不能关注自己！");
                }
                followService.addFollowing(followerId,person_id);
                System.out.println(followerId);
                System.out.println(result.getId());
                String notifierName = userService.getUserById(followerId).getUserName();
                String receiverName = result.getUserName();
                noticeService.sendMessage("我已经关注了你，开始聊天吧！", result.getId(), followerId, notifierName, receiverName, 1);
            }
            return Result.create(StatusCode.OK, "关注成功", result);
        } catch (RuntimeException e) {
            return Result.create(StatusCode.ERROR, e.getMessage());
        }
    }

    /**
     * 移除关注
     */
    @PostMapping("/remove_scholar")
    public Result removeFollow(String person_id) {
        if (!formatUtil.checkStringNull(person_id)) {
            return Result.create(StatusCode.ERROR, "person_id为空");
        }
        try{
            followService.removeFollower(person_id);
            return Result.create(StatusCode.OK, "移除成功");
        }catch (RuntimeException e) {
            return Result.create(StatusCode.ERROR, e.getMessage());
        }
    }

    // ===============================================

    /**
     * 查询用户粉丝数
     */
    @GetMapping("/followerNum")
    public Result followingNum(String person_id){
        if (!formatUtil.checkStringNull(person_id)) {
            return Result.create(StatusCode.ERROR, "person_id为空");
        }
        int num = followService.getFollowerCountById(person_id);
        return Result.create(StatusCode.OK, "查询成功", num);
    }

    /**
     * 查询用户关注数
     */
    @GetMapping("/followingNum")
    public Result followerNum(){
//        Integer userId = userService.getUserByName(userName).getId();
//        int num = followService.getFollowerCountById(userId);
        return Result.create(StatusCode.OK, "查询成功", -1);
    }

    /**
     * 是否关注此id用户
     */
    @GetMapping("/isFollow")
    public Result isFollow(String person_id){
        if (!formatUtil.checkStringNull(person_id)) {
            return Result.create(StatusCode.ERROR, "person_id为空");
        }
        try{
            Integer userId = jwtTokenUtil.getUserIdFromRequest(request);
            boolean flag = followService.isFollowed(userId,person_id);
            return Result.create(StatusCode.OK, "查询成功", flag);
        }catch (RuntimeException e){
            return Result.create(StatusCode.ERROR, "用户未登录");
        }

    }
}
