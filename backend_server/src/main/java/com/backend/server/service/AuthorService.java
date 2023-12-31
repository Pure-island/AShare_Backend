package com.backend.server.service;

import com.backend.server.entity.pojo.Change;
import com.backend.server.entity.pojo.MessageList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class AuthorService {
    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * 查看作者信息
     * @param aid
     * @return
     */
    public Map queryAuthor(String aid) {

        Query query = new Query(Criteria.where("index").is(aid));
        Map authors = mongoTemplate.findOne(query, Map.class, "author");
        authors.put("aid",authors.get("index"));
        //System.out.println(authors);
        return authors;
    }

    /**
     * 绑定门户
     * @param userId
     * @param username
     * @param authorId
     */
    public void bindUser(Integer userId, String username, String authorId) {
        Query query = new Query(Criteria.where("index").is(authorId));
        //Update update = new Update().set("user_id", userId).set("username", username);
        Update update = new Update().set("user_id", userId);
        mongoTemplate.updateFirst(query, update, "author");
    }


    /**
     * 更新门户信息
     * @param author
     */
    public void updateById(Change author) {
        System.out.println("updateById:");
        System.out.println("orgination:"+author.getOrgination().toString());
        System.out.println("name:"+author.getExpertName().toString());
        Query query = new Query(Criteria.where("index").is(author.getAid()));
//        Update update = new Update().set("name", author.getExpertName()).set("phone", author.getPhoneNumber()).set("email", author.getEmail())
//                                    .set("affiliations", author.getOrgination()).set("address", author.getAddress()).set("homepage", author.getHomepage())
//                                    .set("sex", author.getSex());
        Update update = new Update().set("name", author.getExpertName()).set("affiliations",author.getOrgination());

        mongoTemplate.updateFirst(query, update,"author");
    }

    /**
     * 搜索门户
     * @param name
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Page<Map> findAuthorByName(String name, Integer pageNum, Integer pageSize) {
        String str =   name.replaceAll("\\.", "\\\\.") ;
        Pattern pattern = Pattern.compile(str , Pattern.CASE_INSENSITIVE);
//        System.out.println(str);
        Query query = new Query(Criteria.where("name").regex(pattern));
//        Query query = new Query(Criteria.where("name").regex(str));
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        List<Map> list = mongoTemplate.find(query.with(pageable), Map.class, "author");
        for (Map map : list) {
            String portalId = map.get("_id").toString();
            map.put("_id", portalId);
            String aid=map.get("index").toString();
            map.put("aid",aid);
        }
        long count = (pageNum + 1) * pageSize;
        if (list.size() == pageSize) count += pageSize;
        else if (list.size() == 0) count -= pageSize;
        return new PageImpl<Map>(list, pageable, count);
    }

    /**
     * 解除门户绑定
     * @param aid
     */
    public void unBindById(String aid) {
        Query query = new Query(Criteria.where("index").is(aid));
        Update update = new Update().unset("user_id");
        mongoTemplate.updateFirst(query, update, "author");
    }


    public void getNameByUserId(List<MessageList> personList) {
        for (MessageList messageList : personList) {
            if (messageList.getAid() != null && !messageList.getAid().equals("")) {
                Query query = new Query(Criteria.where("index").is(messageList.getAid()));
                Map author = mongoTemplate.findOne(query, Map.class, "author");
                messageList.setAuthorName((String) author.get("name"));
            }
        }
    }
}
