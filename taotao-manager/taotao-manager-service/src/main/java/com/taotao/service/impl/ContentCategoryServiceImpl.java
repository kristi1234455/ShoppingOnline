package com.taotao.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.pojo.TreeNode;
import com.taotao.mapper.TbContentCategoryMapper;
import com.taotao.pojo.TbContentCategory;
import com.taotao.pojo.TbContentCategoryExample;
import com.taotao.pojo.TbContentCategoryExample.Criteria;
import com.taotao.service.ContentCategoryService;

@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {
	@Autowired
	private TbContentCategoryMapper tbContentCategoryMapper;

	@Override
	public List<TreeNode> getCategoryList(long parentId) {
		// 根据parentid查询节点列表
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		// 执行查询
		List<TbContentCategory> list = tbContentCategoryMapper.selectByExample(example);
		List<TreeNode> resultList=new ArrayList<>();
		for(TbContentCategory tbContentCategory : list){
			TreeNode treeNode=new TreeNode();
			treeNode.setId(tbContentCategory.getId());
			treeNode.setText(tbContentCategory.getName());
			treeNode.setState(tbContentCategory.getIsParent()?"closed":"open");
			resultList.add(treeNode);
		}
		return resultList;
	}

	@Override
	public TaotaoResult insertContentCategory(long parentId, String name) {
		TbContentCategory tbContentCategory=new TbContentCategory();
		tbContentCategory.setParentId(parentId);
		tbContentCategory.setName(name);
		tbContentCategory.setCreated(new Date());
		tbContentCategory.setUpdated(new Date());
		tbContentCategory.setIsParent(false);
		tbContentCategory.setSortOrder(1);
		tbContentCategory.setStatus(1);
		tbContentCategoryMapper.insert(tbContentCategory);//更改mapper文件后，tbContentCategory就已经有了id
		
		TbContentCategory parentCat=tbContentCategoryMapper.selectByPrimaryKey(parentId);
		if(!parentCat.getIsParent()){
			parentCat.setIsParent(true);
			tbContentCategoryMapper.updateByPrimaryKey(parentCat);
		}
		return TaotaoResult.ok(tbContentCategory);
	}

	@Override
	public TaotaoResult deleteContentCategory(Long parentId, Long id) {
		TbContentCategoryExample tbContentCategoryExample=new TbContentCategoryExample();
		Criteria criteria=tbContentCategoryExample.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		criteria.andIdEqualTo(id);
		int i = tbContentCategoryMapper.deleteByPrimaryKey(id);
		if(i > 0){
			return TaotaoResult.ok();
		}
		return TaotaoResult.build(400, "删除不成功，请重新再试！");
	}

	@Override
	public TaotaoResult updateContentCategory(Long id, String name) {
		TbContentCategory tbContentCategory=new TbContentCategory();
		tbContentCategory.setName(name);
		tbContentCategory.setId(id);
		tbContentCategory.setUpdated(new Date());
		TbContentCategoryExample tbContentCategoryExample=new TbContentCategoryExample();
		Criteria criteria=tbContentCategoryExample.createCriteria();
		criteria.andIdEqualTo(id);
		int result=tbContentCategoryMapper.updateByExampleSelective(tbContentCategory, tbContentCategoryExample);
		if(result > 0){
			return TaotaoResult.ok();
		}
		return TaotaoResult.build(400, "更新不成功，请重新再试！");
	}
}
