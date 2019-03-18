package com.taotao.portal.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.HttpClientUtil;
import com.taotao.common.utils.JsonUtils;
import com.taotao.portal.pojo.SearchResult;
import com.taotao.portal.service.SearchService;

@Service
public class SearchServiceImpl implements SearchService {

	@Value("${SEARCH_BASE_URL}")
	private String SEARCH_BASE_URL;
	
	@Override
	public SearchResult search(String queryString, int page) {
		try{
			Map<String, String> map=new HashMap<>();
			map.put("q", queryString);
			map.put("page", page+"");
			String json = HttpClientUtil.doGet(SEARCH_BASE_URL,map);
			TaotaoResult taotaoResult = TaotaoResult.formatToPojo(json, SearchResult.class);
			if(taotaoResult.getStatus() == 200){
				SearchResult searchResult = (SearchResult) taotaoResult.getData();
				return searchResult;
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

}
