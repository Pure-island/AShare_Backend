package com.backend.server.service;

import com.backend.server.entity.Portal;
import com.backend.server.mapper.PortalMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class PortalService {
    @Autowired
    private PortalMapper portalMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public Map<String,String>codes=new HashMap<>();

    public Portal selectById(Integer portalId) {
        Portal portal = portalMapper.selectById(portalId);
        return portal;
    }

    public int updateById(Portal portal) {
        int result = portalMapper.updateById(portal);
        return result;
    }

    /*
    public boolean checkInformation(Certification certification) {
        Portal portal = portalMapper.selectById(certification.getPortalId());
        if (portal.getExpertName().equals(certification.getExpertName())) {
            return true;
        }
        return false;
    }*/



    /**
     * 判断验证码
     * @param mail 邮箱
     * @param code 验证码
     * @return 验证码正误
     */
    public boolean checkMailCode(String mail, String code) {
        //String mailCode = redisTemplate.opsForValue().get("MAIL_" + mail);
        String mailCode=codes.get(mail);
        System.out.println("真验证码："+mailCode);
        System.out.println("输入验证码:" + code);
        System.out.println("邮箱:" + mail);
        System.out.println(code.equals(mailCode));
        return code.equals(mailCode);
    }
    @Autowired
    JavaMailSenderImpl javaMailSender;

    /**
     * 发送邮件验证码
     * @param mail 邮箱
     */
    public void sendMail(String mail) {
        System.out.println("mail = " + mail);
        Random _random = new Random();
        int random = _random.nextInt(899999) + 100001;
        Map<String, String> map = new HashMap<>();
        String code = Integer.toString(random);
        map.put("mail", mail);
        map.put("code", code);
        SimpleMailMessage mailMessage=new SimpleMailMessage();
        mailMessage.setTo(mail);
        mailMessage.setSubject("Ashare");
        mailMessage.setText("验证码:"+code);
        mailMessage.setFrom("1723072376@qq.com");
        System.out.println("测试邮件是否能发送");
        javaMailSender.send(mailMessage);
        System.out.println("测试邮件发送over");
        if(codes.containsKey(mail))
        {
            codes.replace(mail,code);
        }
        else codes.put(mail,code);
        //保存发送记录
       // redisTemplate.opsForValue()
       //         .set("MAIL_" + mail, code, 1, TimeUnit.MINUTES);
       // rabbitTemplate.convertAndSend("MAIL", map);

    }
}
