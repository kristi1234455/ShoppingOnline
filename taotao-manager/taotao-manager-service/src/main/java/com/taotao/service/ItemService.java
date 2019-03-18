package com.taotao.service;

import java.util.List;

import com.taotao.common.pojo.EUDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.pojo.TreeNode;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;

public interface ItemService {

	TbItem getItemById(long itemId);
	
	EUDataGridResult getItemList(int page, int rows);
	
	TaotaoResult createItem(TbItem item, TbItemDesc itemDesc,String itemParams);

}
