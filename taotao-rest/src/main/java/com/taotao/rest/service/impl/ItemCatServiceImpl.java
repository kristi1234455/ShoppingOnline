package com.taotao.rest.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.common.utils.JsonUtils;
import com.taotao.mapper.TbItemCatMapper;
import com.taotao.pojo.TbItemCat;
import com.taotao.pojo.TbItemCatExample;
import com.taotao.pojo.TbItemCatExample.Criteria;
import com.taotao.rest.dao.JedisClient;
import com.taotao.rest.pojo.CatNode;
import com.taotao.rest.pojo.CatResult;
import com.taotao.rest.service.ItemCatService;

@Service
public class ItemCatServiceImpl implements ItemCatService {

	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Value("${INDEX_ITEMCAT_REDIS_KEY}")
	private String INDEX_ITEMCAT_REDIS_KEY;
	@Autowired
	private JedisClient jedisClient;

	@Override
	public CatResult getItemCatList() {
		CatResult catResult = new CatResult();
		// 查询分类列表
		catResult.setData(getCatList(0));
		return catResult;
	}

	public List<?> getCatList(long parentId) {
		//从缓存中取数据
		try {
			//把parentId转换为字符串，因为缓存中存储的都是字符串
			String result=jedisClient.hget(INDEX_ITEMCAT_REDIS_KEY, parentId+"");
			if(!(StringUtils.isBlank(result))){
				List<CatNode> list=JsonUtils.jsonToList(result, CatNode.class);
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//如果缓存中没有，就从数据库中取数据
		TbItemCatExample example = new TbItemCatExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		List<TbItemCat> list = itemCatMapper.selectByExample(example);

		List resultList = new ArrayList<>();
		int count=0;
		for (TbItemCat tbItemCat : list) {
			if (tbItemCat.getIsParent()) {
				CatNode catNode = new CatNode();
				if (parentId == 0) {
					catNode.setName("<a href='/products/" + tbItemCat.getId() + 
							".html'>" + tbItemCat.getName() + "</a>");
				} else {
					catNode.setName(tbItemCat.getName());
				}
				catNode.setUrl("/products/" + tbItemCat.getId() + ".html");
				catNode.setItem(getCatList(tbItemCat.getId()));

				resultList.add(catNode);
				count++;
				if(parentId ==0 && count >=14){
					break;
				}
			} else {// 如果是叶子节点
				resultList.add("/products/" + tbItemCat.getId() + ".html|" + tbItemCat.getName());
			}
		}
		//返回数据之前，保存一份到缓存中
		try {
			//把list转换成字符串，因为缓存中存储的都是字符串
			jedisClient.hset(INDEX_ITEMCAT_REDIS_KEY, parentId+"", JsonUtils.objectToJson(resultList));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}

}
