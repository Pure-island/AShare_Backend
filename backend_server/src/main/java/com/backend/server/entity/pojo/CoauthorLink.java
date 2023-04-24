package com.backend.server.entity.pojo;

import lombok.Data;

@Data
public class CoauthorLink {
    public String source;
    public String target;
    public Integer label;
    public CoauthorLink()
    {

    }
}
