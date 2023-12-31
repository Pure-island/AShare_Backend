package com.backend.server.controller;

import com.backend.server.entity.pojo.CoauthorResult;
import com.backend.server.entity.pojo.PortalReturn;
import com.backend.server.entity.pojo.ReferPaperResult;
import com.backend.server.entity.pojo.Result;
import com.backend.server.service.CoauthorService;
import com.backend.server.service.PaperService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Api(tags="关联检索")
@RequestMapping("/relation")
@RestController
public class RelationController {
    @Autowired
    public CoauthorService coauthorService;

    @Autowired
    public PaperService paperService;


    @ApiOperation(value="查看关联学者接口")
    @PostMapping("/getAuthor")
    public Result getAuthor(String aid)
    {
        CoauthorResult cr=coauthorService.queryByAid(aid);
        return Result.create(200,"success",cr);
    }

    @ApiOperation(value="查看关联论文接口")
    @PostMapping("/getRelatePaper")
    public Result getRelatePaper(String pid)
    {
        System.out.println("getRelatePaper:"+pid);
        ReferPaperResult referPaperResult=paperService.queryRelatePaperByPid(pid);

        return Result.create(200,"success",referPaperResult);
    }
}
