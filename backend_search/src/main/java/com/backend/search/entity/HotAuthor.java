package com.backend.search.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
//@Document(collection = "c_h_author")
public class HotAuthor {

	@Id
	private String _id;

//	@TableField(value = "index")
	private String aid;
	private String index;
	private String name;
	private Integer h_index;
	private Integer n_pubs;
	private Integer n_citation;
	private Integer userId;
}
