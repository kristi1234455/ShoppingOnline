package com.taotao.sso.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.CookieUtils;
import com.taotao.common.utils.JsonUtils;
import com.taotao.mapper.TbUserMapper;
import com.taotao.pojo.TbUser;
import com.taotao.pojo.TbUserExample;
import com.taotao.pojo.TbUserExample.Criteria;
import com.taotao.sso.dao.JedisClient;
import com.taotao.sso.service.UserService;


@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private TbUserMapper tbUserMapper;
	
	@Autowired
	private JedisClient jedisClient;
	@Value("${REDIS_USER_SESSION_KEY}")
	private String REDIS_USER_SESSION_KEY;
	@Value("${SSO_SESSION_EXPIRE}")
	private Integer SSO_SESSION_EXPIRE;
	
	@Override
	public TaotaoResult checkData(String content, Integer type) {
		//创建查询条件
		TbUserExample tbUserExample=new TbUserExample();
		Criteria criteria = tbUserExample.createCriteria();
		//对数据进行校验：1、2、3分别代表username、phone、email
		if(1 == type){
			criteria.andUsernameEqualTo(content);
		}
		if(2 == type){
			criteria.andPasswordEqualTo(content);
		}
		if(3 == type){
			criteria.andEmailEqualTo(content);
		}
		//执行查询
		List<TbUser> list = tbUserMapper.selectByExample(tbUserExample);
		if(list == null || list.size() == 0){//说明数据库中没有重名的，这个注册可以用
			return TaotaoResult.ok(true);
		}
		return TaotaoResult.ok(false);
	}

	@Override
	public TaotaoResult createUser(TbUser user) {
		user.setCreated(new Date());
		user.setUpdated(new Date());
		user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
		tbUserMapper.insert(user);
		return TaotaoResult.ok();
	}

	@Override
	public TaotaoResult userLogin(String username, String password
			,HttpServletRequest request,HttpServletResponse response) {
		TbUserExample tbUserExample=new TbUserExample();
		Criteria criteria = tbUserExample.createCriteria();
		criteria.andUsernameEqualTo(username);
		List<TbUser> list = tbUserMapper.selectByExample(tbUserExample);
		
		//如果没有此用户名
		if(null == list || list.size() == 0){
			return TaotaoResult.build(400, "此用户不存在");
		}
		//如果找到该用户，需要对比该用户的登陆密码
		TbUser tbUser = list.get(0);
		if(!DigestUtils.md5DigestAsHex(password.getBytes()).equals(tbUser.getPassword())){
			return TaotaoResult.build(400, "密码错误，请重新再试");
		}
		//用户名和密码正确
		String token = UUID.randomUUID().toString();
		tbUser.setPassword(null);
		//在redis缓存中保存token，用来快速取出用户信息
		jedisClient.set(REDIS_USER_SESSION_KEY+":"+token, JsonUtils.objectToJson(tbUser));
		jedisClient.expire(REDIS_USER_SESSION_KEY+":"+token, SSO_SESSION_EXPIRE);
		
		//cookie中保存token，用来共享用户信息，且设置过期时间为浏览器关闭，即失效
		CookieUtils.setCookie(request, response, "TT_TOKEN", token);//cookie名字是固定的，jsp页面根据该名字得到cookie值
		return TaotaoResult.ok(token);
	}

	@Override
	public TaotaoResult getUserByToken(String token) {
		String json= jedisClient.get(REDIS_USER_SESSION_KEY+":"+token);
		//如果没有找到，说明已经过期
		if(StringUtils.isBlank(json)){
			return TaotaoResult.build(400, "此session已经过期，请重新登陆！");
		}
		//如果找到，则返回，且更新过期时间
		jedisClient.expire(REDIS_USER_SESSION_KEY+":"+token, SSO_SESSION_EXPIRE);
		return TaotaoResult.ok(JsonUtils.jsonToPojo(json, TbUser.class));
		//ok方法中需传入Object，则需要先转化为pojo对象
	}

	@Override
	public TaotaoResult logout(String token) {
		jedisClient.expire(REDIS_USER_SESSION_KEY+":"+token, 0);
		return TaotaoResult.ok();
	}

	
}