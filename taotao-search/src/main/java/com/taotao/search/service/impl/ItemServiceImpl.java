package com.taotao.search.service.impl;

import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.ExceptionUtil;
import com.taotao.search.mapper.ItemMapper;
import com.taotao.search.pojo.Item;
import com.taotao.search.service.ItemService;

@Service
public class ItemServiceImpl implements ItemService {

	@Autowired
	private ItemMapper itemMapper;

	@Autowired
	private SolrServer solrServer;

	@Override
	public TaotaoResult importAllItems() {
		try {
			List<Item> itemList = itemMapper.getItemList();
		
			for(Item item : itemList){
				SolrInputDocument solrInputDocument=new SolrInputDocument();
				solrInputDocument.setField("id", item.getId());
				solrInputDocument.setField("item_title", item.getTitle());
				solrInputDocument.setField("item_price", item.getPrice());
				solrInputDocument.setField("item_sell_point", item.getSell_point());
				solrInputDocument.setField("item_image", item.getImage());
				solrInputDocument.setField("item_category_name", item.getCategory_name());
				solrInputDocument.setField("item_desc", item.getItem_des());
				
				solrServer.add(solrInputDocument);
			}
			solrServer.commit();
		} catch (Exception e) {
			e.printStackTrace();
			return TaotaoResult.build(500, ExceptionUtil.getStackTrace(e));
		}
		return TaotaoResult.ok();
	}
}
