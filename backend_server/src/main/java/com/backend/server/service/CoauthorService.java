package com.backend.server.service;

import com.backend.server.entity.pojo.CoauthorLink;
import com.backend.server.entity.pojo.CoauthorNode;
import com.backend.server.entity.pojo.CoauthorResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CoauthorService {
    @Autowired
    private MongoTemplate mongoTemplate;

    public CoauthorResult queryByAid(String aid)
    {
        Query query = new Query(Criteria.where("aid_1").is(aid));
        List<Map> authors = mongoTemplate.find(query,Map.class, "coauthor");
        Query query1 = new Query(Criteria.where("aid_2").is(aid));
        List<Map> authors1 = mongoTemplate.find(query,Map.class, "coauthor");
        CoauthorResult coauthorResult=new CoauthorResult();
        for (Map a:authors
             ) {
            String source=a.get("aid_1").toString();
            String target=a.get("aid_2").toString();

            Integer label=Integer.parseInt(a.get("collaborations").toString());
            CoauthorNode cn=new CoauthorNode();
            cn.setId(source);
            Query query2 = new Query(Criteria.where("index").is(source));
            Map author = mongoTemplate.findOne(query2, Map.class, "author");
            String tempName="";
            if(author==null)
            cn.setName("佚名");
            else
            {
                tempName=author.get("name").toString();
                cn.setName(tempName);
            }
            System.out.println("aid1:"+source+" name:"+tempName);
            boolean isNew=true;
            for (CoauthorNode oldNode:coauthorResult.getNodes()
                 ) {
                if(oldNode.getId().equals(source))
                {
                    isNew=false;
                    break;
                }
            }
            if(isNew)
            coauthorResult.getNodes().add(cn);

            CoauthorNode cn1=new CoauthorNode();
            cn1.setId(target);
            Query query3 = new Query(Criteria.where("index").is(target));
            Map author1 = mongoTemplate.findOne(query3, Map.class, "author");

            String tempName1="";
            if(author1==null)
                cn1.setName("佚名");
            else
            {
                tempName1=author1.get("name").toString();
                cn1.setName(tempName1);
            }
            System.out.println("aid1:"+target+" name:"+tempName1);
            boolean isNew1=true;
            for (CoauthorNode oldNode:coauthorResult.getNodes()
            ) {
                if(oldNode.getId().equals(target))
                {
                    isNew1=false;
                    break;
                }
            }
            if(isNew1)
                coauthorResult.getNodes().add(cn1);

            CoauthorLink cl1=new CoauthorLink();
            cl1.setSource(source);
            cl1.setTarget(target);
            cl1.setLabel(label);
            boolean isLinkNew=true;
            for (CoauthorLink oldLink:coauthorResult.getLinks()
                 ) {
                String aid1=oldLink.getSource();
                String aid2=oldLink.getTarget();
                if(source.equals(aid1)&&aid2.equals(target))
                {
                    isLinkNew=false;
                    break;
                }
            }
            if(isLinkNew)
            coauthorResult.getLinks().add(cl1);

            CoauthorLink cl2=new CoauthorLink();
            cl2.setSource(target);
            cl2.setTarget(source);
            cl2.setLabel(label);
            boolean isLinkNew1=true;
            for (CoauthorLink oldLink:coauthorResult.getLinks()
            ) {
                String aid1=oldLink.getSource();
                String aid2=oldLink.getTarget();
                if(target.equals(aid1)&&aid2.equals(source))
                {
                    isLinkNew1=false;
                    break;
                }
            }
            if(isLinkNew1)
            coauthorResult.getLinks().add(cl2);
        }
        /*
        for (Map a:authors1
        ) {
            String source=a.get("aid_1").toString();
            String target=a.get("aid_2").toString();
            Integer label=Integer.parseInt(a.get("collaborations").toString());

            CoauthorNode cn=new CoauthorNode();
            cn.setId(a.get("aid_1").toString());
            cn.setName("test");
            coauthorResult.getNodes().add(cn);

            CoauthorLink cl1=new CoauthorLink();
            cl1.setSource(source);
            cl1.setTarget(target);
            cl1.setLabel(label);
            coauthorResult.getLinks().add(cl1);

            CoauthorLink cl2=new CoauthorLink();
            cl2.setSource(source);
            cl2.setTarget(target);
            cl2.setLabel(label);
            coauthorResult.getLinks().add(cl2);
        }*/

        //System.out.println(authors);
        return coauthorResult;
    }
}
