package com.taotao.search.service.impl;

import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.search.dao.SearchDao;
import com.taotao.search.pojo.SearchResult;
import com.taotao.search.service.SearchService;

@Service
public class SearchServiceImpl implements SearchService {

	@Autowired
	private SearchDao searchDao;
	
	@Override
	public SearchResult search(String queryString, int page, int rows) throws Exception {
		SolrQuery solrQuery=new SolrQuery();
		solrQuery.setQuery(queryString);
		//设置分页信息，当前页是从第几条记录开始，每页显示多少条记录
		solrQuery.setStart((page-1)*rows);
		solrQuery.setRows(rows);
		solrQuery.set("df","item_keywords");
		//设置高亮
		solrQuery.setHighlight(true);
		solrQuery.addHighlightField("item_title");
		solrQuery.setHighlightSimplePre("<em style=\"color:red\">");
		solrQuery.setHighlightSimplePost("</em>");
		
		//执行查询
		SearchResult searchResult = searchDao.search(solrQuery);
		//设置SearchResult返回对象中的分页信息
		long recordCount = searchResult.getRecordCount();
		long pageCount=recordCount/rows;
		if(recordCount % rows > 0){
			pageCount ++;
		}
		searchResult.setPageCount(pageCount);
		searchResult.setCurPage(page);
		return searchResult;
	}

}
