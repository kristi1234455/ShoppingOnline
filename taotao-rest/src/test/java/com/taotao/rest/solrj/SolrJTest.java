package com.taotao.rest.solrj;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class SolrJTest {

	@Test
	public void addDocument() throws Exception {
		SolrServer solrServer=new HttpSolrServer("http://192.168.244.128:8080/solr");
		SolrInputDocument solrInputDocument=new SolrInputDocument();
		solrInputDocument.addField("id", "test002");
		solrInputDocument.addField("item_title", "测试商品2");
		solrInputDocument.addField("item_price", 54341);
		solrServer.add(solrInputDocument);
		solrServer.commit();
	}
	
	@Test
	public void delDocument() throws Exception {
		SolrServer solrServer=new HttpSolrServer("http://192.168.244.128:8080/solr");
		solrServer.deleteById("test002");
		//solrServer.deleteByQuery("*:*");
		solrServer.commit();
	}
	
	@Test
	public void queryDocument() throws Exception {
		SolrServer solrServer=new HttpSolrServer("http://192.168.244.128:8080/solr");
		SolrQuery solrQuery=new SolrQuery();
		solrQuery.setQuery("*:*");
		solrQuery.setStart(20);
		solrQuery.setRows(50);
		
		QueryResponse response = solrServer.query(solrQuery);
		
		SolrDocumentList solrDocumentList = response.getResults();
		System.out.println("共查询到总记录数："+solrDocumentList.getNumFound());
		for(SolrDocument solrDocument : solrDocumentList){
			System.out.println(solrDocument.get("id"));
			System.out.println(solrDocument.get("item_title"));
			System.out.println(solrDocument.get("item_price"));
		}
	}
}
