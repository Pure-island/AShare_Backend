package com.backend.server.service;

import com.backend.server.entity.pojo.ReferPaperLink;
import com.backend.server.entity.pojo.ReferPaperNode;
import com.backend.server.entity.pojo.ReferPaperResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PaperService {
    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Map> queryPaperByAuthorId(String authorId) {
        Query query = new Query(Criteria.where("aid").is(authorId));
        List<Map> aidpids = mongoTemplate.find(query, Map.class, "aidpid");
        List<Map> papers=new ArrayList<>();
        for (Map aidpid:aidpids
             ) {
            String aid=aidpid.get("aid").toString();
            String pid=aidpid.get("pid").toString();
            Query paperQuery=new Query(Criteria.where("pid").is(pid));
            Map paper=mongoTemplate.findOne(paperQuery,Map.class,"paper");
            if(paper==null)continue;
            papers.add(paper);
        }
        //System.out.println(papers.size());
        return papers;
    }
    public String GetPaperNameByPid(String pid)
    {
        Query query = new Query(Criteria.where("pid").is(pid));
        Map paper = mongoTemplate.findOne(query, Map.class, "paper");
        if(paper==null)
            return "";
        return paper.get("title").toString();
    }

    public ReferPaperResult queryRelatePaperByPid(String pid) {
        Query query = new Query(Criteria.where("pid").is(pid));
        Map paper = mongoTemplate.findOne(query, Map.class, "paper");

        ReferPaperResult referPaperResult=new ReferPaperResult();
        ReferPaperNode firstNode=new ReferPaperNode();
        firstNode.setPid(pid);
        System.out.println("pid:"+pid);
        firstNode.setName(GetPaperNameByPid(pid));
        referPaperResult.getNodes().add(firstNode);
        if(paper.get("references")==null)
            return referPaperResult;
        for(Object o:(List<?>)paper.get("references"))
        {
            String tarPid=o.toString();
            String tarName=GetPaperNameByPid(tarPid);
            ReferPaperNode tarNode=new ReferPaperNode();
            tarNode.setName(tarName);
            tarNode.setPid(tarPid);
            referPaperResult.getNodes().add(tarNode);

            ReferPaperLink link1=new ReferPaperLink();
            link1.setSource(pid);
            link1.setTarget(tarPid);
            referPaperResult.getLinks().add(link1);

            ReferPaperLink link2=new ReferPaperLink();
            link2.setSource(tarPid);
            link2.setTarget(pid);
            referPaperResult.getLinks().add(link2);
        }
        return referPaperResult;
    }
}
