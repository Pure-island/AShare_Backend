package com.backend.server.entity;

import com.backend.server.entity.authorSon.pub;
import com.backend.server.entity.authorSon.tag;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "coauthor")
public class Coauthor {
    @Id
    private String _id;

    private String aid_1;
    private String aid_2;
    private Integer collaborations;
}
