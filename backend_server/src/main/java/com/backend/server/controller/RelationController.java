package com.backend.server.controller;

import com.backend.server.entity.pojo.CoauthorResult;
import com.backend.server.entity.pojo.PortalReturn;
import com.backend.server.entity.pojo.ReferPaperResult;
import com.backend.server.entity.pojo.Result;
import com.backend.server.service.CoauthorService;
import com.backend.server.service.PaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RequestMapping("/relation")
@RestController
public class RelationController {
    @Autowired
    public CoauthorService coauthorService;

    @Autowired
    public PaperService paperService;


    @PostMapping("/getAuthor")
    public Result getAuthor(String aid)
    {
        CoauthorResult cr=coauthorService.queryByAid(aid);
        return Result.create(200,"success",cr);
    }

    @PostMapping("/getRelatePaper")
    public Result getRelatePaper(String pid)
    {
        System.out.println("getRelatePaper:"+pid);
        ReferPaperResult referPaperResult=paperService.queryRelatePaperByPid(pid);

        return Result.create(200,"success",referPaperResult);
    }
}
