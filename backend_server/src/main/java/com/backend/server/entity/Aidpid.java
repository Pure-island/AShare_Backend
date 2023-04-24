package com.backend.server.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "aidpid")
public class Aidpid {
    @Id
    private String _id;

    private String aid;
    private String pid;
}
