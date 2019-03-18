package com.taotao.portal.controller;

import javax.servlet.ServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.taotao.portal.pojo.SearchResult;
import com.taotao.portal.service.SearchService;

@Controller
public class SearchController {

	@Autowired
	private SearchService searchService;
	
	@RequestMapping("/search")
	public String search(@RequestParam("q") String queryString,
			@RequestParam(defaultValue="1") Integer page, Model model){
		//如果有查询条件，需要先对查询条件进行编码
		if(queryString != null){
			try{
				queryString=new String(queryString.getBytes("iso8859-1"), "utf-8");
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		SearchResult searchResult = searchService.search(queryString, page);
		model.addAttribute("query", queryString);
		model.addAttribute("totalPages", searchResult.getPageCount());
		model.addAttribute("itemList", searchResult.getItemList());
		model.addAttribute("page", searchResult.getCurPage());
		
		return "search";
	}
}
