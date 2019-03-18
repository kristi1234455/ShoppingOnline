package com.taotao.rest.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.ExceptionUtil;
import com.taotao.rest.dao.JedisClient;
import com.taotao.rest.service.RedisService;

@Service
public class RedisServiceImpl implements RedisService {

	@Value("${INDEX_CONTENT_REDIS_KEY}")
	private String INDEX_CONTENT_REDIS_KEY;
	@Autowired
	private JedisClient jedisClient;
	@Value("${INDEX_ITEMCAT_REDIS_KEY}")
	private String INDEX_ITEMCAT_REDIS_KEY;
	
	@Override
	public TaotaoResult syncContent(long contentCid) {
		//删除缓存中，这个contentId对应的值
		try{
			jedisClient.hdel(INDEX_CONTENT_REDIS_KEY, contentCid+"");
		}catch(Exception e){
			e.printStackTrace();
			return TaotaoResult.build(500, ExceptionUtil.getStackTrace(e));
		}
		return TaotaoResult.ok();
	}
	
	@Override
	public TaotaoResult syncItemCat(long parentId) {
		//删除缓存中，这个contentId对应的值
		try{
			jedisClient.hdel(INDEX_ITEMCAT_REDIS_KEY, parentId+"");
		}catch(Exception e){
			e.printStackTrace();
			return TaotaoResult.build(500, ExceptionUtil.getStackTrace(e));
		}
		return TaotaoResult.ok();
	}
}
