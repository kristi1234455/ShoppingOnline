package com.taotao.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.rest.service.RedisService;

@Controller
@RequestMapping("/cache/sync")
public class RedisController {
	
	@Autowired
	private RedisService redisService;
	
	@RequestMapping("/content/{contentCid}")
	@ResponseBody
	public TaotaoResult syncContent(@PathVariable Long contentCid){
		TaotaoResult result=redisService.syncContent(contentCid);
		return result;
	}
	
	@RequestMapping("/itemCat/{parentId}")
	@ResponseBody
	public TaotaoResult syncItemCat(@PathVariable Long parentId){
		TaotaoResult result=redisService.syncContent(parentId);
		return result;
	}
}
