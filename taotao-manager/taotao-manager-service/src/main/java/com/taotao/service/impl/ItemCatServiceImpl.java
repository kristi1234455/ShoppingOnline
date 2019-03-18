package com.taotao.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.TreeNode;
import com.taotao.mapper.TbItemCatMapper;
import com.taotao.pojo.TbItemCat;
import com.taotao.pojo.TbItemCatExample;
import com.taotao.pojo.TbItemCatExample.Criteria;
import com.taotao.service.ItemCatService;

@Service
public class ItemCatServiceImpl implements ItemCatService {
	
	@Autowired
	private TbItemCatMapper itemCatMapper;
	
	//查询商品的所有目录
	@Override
	public List<TreeNode> getItemCatList(long parentId) {
		TbItemCatExample tbItemCatExample=new TbItemCatExample();
		Criteria criteria=tbItemCatExample.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		List<TbItemCat> list=itemCatMapper.selectByExample(tbItemCatExample);
		
		List<TreeNode> resultList=new ArrayList<TreeNode>();
		for(TbItemCat tbItemCat : list){
			TreeNode node = new TreeNode(tbItemCat.getId(), tbItemCat.getName(), 
					tbItemCat.getIsParent()?"closed":"open");
			resultList.add(node);
		}
		
		return resultList;
	}

}
