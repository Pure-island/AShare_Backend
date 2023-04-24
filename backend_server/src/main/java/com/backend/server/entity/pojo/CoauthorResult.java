package com.backend.server.entity.pojo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CoauthorResult {
    List<CoauthorNode>nodes;
    List<CoauthorLink>links;
    public CoauthorResult()
    {
        nodes=new ArrayList<>();
        links=new ArrayList<>();
    }

}
