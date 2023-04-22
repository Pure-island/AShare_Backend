package com.backend.search.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "paper")
@TableName(autoResultMap = true)
public class Paper {

	@TableId
	private String _id;
	private String pid;
	private String title;
	private List<author> authors;
	private venue venue;
	private Integer year;
	private List<String> keywords;
	private Integer n_citation;
	private String pageStart;
	private String pageEnd;
	private String lang;
	private String volume;
	private String issue;
	private String issn;
	private String doi;
	private List<String> url;
	private String abstracts;
}