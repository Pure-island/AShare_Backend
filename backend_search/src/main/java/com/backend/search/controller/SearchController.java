package com.backend.search.controller;

import com.backend.search.entity.HotAuthor;
import com.backend.search.entity.Paper;
import com.backend.search.entity.pojo.Result;
import com.backend.search.entity.pojo.StatusCode;
import com.backend.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("search")
public class SearchController {

	@Autowired
	SearchService searchService;

	/**
	 * 按 id (pid) 查询文章
	 *
	 * @param paperId
	 * @return
	 */
	@GetMapping("/id/{paperId}")
	public Result getPaperById(@PathVariable String paperId) {
		return Result.create(StatusCode.OK, "查询成功", searchService.findPaperByPid_(paperId));
	}

	/**
	 *
	 * @return
	 */
	@GetMapping("/hotPaper")
	public Result getHotPaper() {
		List<Paper> p = searchService.findHotPaper();
		return Result.create(StatusCode.OK, "查询成功", p);
	}

	/**
	 *
	 * @return
	 */
	@GetMapping("/hotAuthorByC")
	public Result getHotAuthorByC() {
		List<HotAuthor> a = searchService.findHotAuthorByC();
		return Result.create(StatusCode.OK, "查询成功", a);
	}

	/**
	 *
	 * @return
	 */
	@GetMapping("/hotAuthorByH")
	public Result getHotAuthorByH() {
		List<HotAuthor> a = searchService.findHotAuthorByH();
		return Result.create(StatusCode.OK, "查询成功", a);
	}

	/**
	 *
	 * @param text
	 * @param startYear
	 * @param endYear
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@PostMapping("/keyword")
	public Result findPaperBykeyword(String text,  Integer startYear,
			 Integer endYear,  Integer pageNum,  Integer pageSize) {
		Page<Paper> page = searchService.findPaperByKeywords(text, startYear, endYear, pageNum, pageSize);
		return Result.create(StatusCode.OK, "查询成功", page);
	}
}
