package com.taotao.rest.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.JsonUtils;
import com.taotao.mapper.TbItemDescMapper;
import com.taotao.mapper.TbItemMapper;
import com.taotao.mapper.TbItemParamItemMapper;
import com.taotao.mapper.TbItemParamMapper;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.pojo.TbItemParam;
import com.taotao.pojo.TbItemParamItem;
import com.taotao.pojo.TbItemParamItemExample;
import com.taotao.pojo.TbItemParamItemExample.Criteria;
import com.taotao.rest.dao.JedisClient;
import com.taotao.rest.service.ItemService;

@Service
public class ItemServiceImpl implements ItemService {

	@Autowired
	private TbItemMapper tbItemMapper;
	@Autowired
	private TbItemDescMapper tbItemDescMapper;
	@Autowired
	private TbItemParamItemMapper tbItemParamItemMapper;
	
	@Value("${REDIS_ITEM_KEY}")
	private String REDIS_ITEM_KEY;
	@Value("${REDIS_ITEM_EXPIRE}")
	private Integer REDIS_ITEM_EXPIRE;
	
	@Autowired
	private JedisClient jedisClient;
	
	@Override
	public TaotaoResult getItemBaseInfo(long itemId) {
		//从缓存中取出
		try{
			String json= jedisClient.get(REDIS_ITEM_KEY+":"+itemId+":base");
			if(!StringUtils.isBlank(json)){
				TbItem tbItem = JsonUtils.jsonToPojo(json, TbItem.class);
				return TaotaoResult.ok(tbItem);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//缓存中没有，就从数据库中取出，并放一份到缓存中
		TbItem tbItem = tbItemMapper.selectByPrimaryKey(itemId);
		try{
			//放一份到缓存中，且设置这个字段的过期时间
			jedisClient.set(REDIS_ITEM_KEY+":"+itemId+":base", JsonUtils.objectToJson(tbItem));
			jedisClient.expire(REDIS_ITEM_KEY+":"+itemId+":base", REDIS_ITEM_EXPIRE);
		}catch(Exception e){
			e.printStackTrace();
		}
		return TaotaoResult.ok(tbItem);
	}

	@Override
	public TaotaoResult getItemDesc(long itemId) {
		//从缓存中取出商品描述信息
		try{
			String json = jedisClient.get(REDIS_ITEM_KEY+":"+itemId+":desc");
			if(!StringUtils.isBlank(json)){
				TbItemDesc tbItemDesc = JsonUtils.jsonToPojo(json, TbItemDesc.class);
				return TaotaoResult.ok(tbItemDesc);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//如果没有，从数据库中取出，然后保存一份到缓存中
		TbItemDesc tbItemDesc = tbItemDescMapper.selectByPrimaryKey(itemId);
		try{
			jedisClient.set(REDIS_ITEM_KEY+":"+itemId+":desc",JsonUtils.objectToJson(tbItemDesc));
			jedisClient.expire(REDIS_ITEM_KEY+":"+itemId+":desc", REDIS_ITEM_EXPIRE);
		}catch(Exception e){
			e.printStackTrace();
		}
		return TaotaoResult.ok(tbItemDesc);
	}

	@Override
	public TaotaoResult getItemParam(long itemId) {
		//从缓存中取出数据
		try{
			String json = jedisClient.get(REDIS_ITEM_KEY+":"+itemId+":param");
			if(!StringUtils.isBlank(json)){
				TbItemParam tbItemParam = JsonUtils.jsonToPojo(json, TbItemParam.class);
				return TaotaoResult.ok(tbItemParam);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//缓存中没有，就从数据库中取，保留一份到缓存中
		TbItemParamItemExample example=new TbItemParamItemExample();
		Criteria criteria=example.createCriteria();
		criteria.andItemIdEqualTo(itemId);
		List<TbItemParamItem> list = tbItemParamItemMapper.selectByExampleWithBLOBs(example);
		if(list !=null && list.size() >0){
			TbItemParamItem paramItem=list.get(0);
			try{
				jedisClient.set(REDIS_ITEM_KEY+":"+itemId+":param", JsonUtils.objectToJson(paramItem));
				jedisClient.expire(REDIS_ITEM_KEY+":"+itemId+":param", REDIS_ITEM_EXPIRE);
			}catch(Exception e){
				e.printStackTrace();
			}
			return TaotaoResult.ok(paramItem);
		}
		return TaotaoResult.build(400, "此商品没有规格参数");
	}

}
