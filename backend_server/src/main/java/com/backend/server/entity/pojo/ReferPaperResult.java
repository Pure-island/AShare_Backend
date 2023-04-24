package com.backend.server.entity.pojo;

import lombok.Data;

import java.sql.Ref;
import java.util.ArrayList;
import java.util.List;

@Data
public class ReferPaperResult {
    public List<ReferPaperNode> nodes;
    public List<ReferPaperLink>links;
    public ReferPaperResult()
    {
        nodes=new ArrayList<>();
        links=new ArrayList<>();
    }
}
