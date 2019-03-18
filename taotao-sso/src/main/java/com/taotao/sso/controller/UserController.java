package com.taotao.sso.controller;


import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.ExceptionUtil;
import com.taotao.pojo.TbUser;
import com.taotao.sso.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;
	
	@RequestMapping("/check/{param}/{type}")
	@ResponseBody
	public Object checkData(@PathVariable String param,@PathVariable Integer type, 
			String callback){
		TaotaoResult result=null;
		
		//参数有效性判断：参数不能为空;type必须是1\2\3中的一个;
		if(StringUtils.isBlank(param)){
			result= TaotaoResult.build(400, "校验内容不能为空");
		}
		if(type == null){
			result= TaotaoResult.build(400, "校验内容类型不能为空");
		}
		if(type !=1 && type !=2 && type !=3){
			result = TaotaoResult.build(400, "校验内容类型错误");
		}
		if(null !=result){//校验后，result有值，则校验出错，需要返回到index首页,或regist页面，重新注册
			if(null != callback){
				MappingJacksonValue mappingJacksonValue=new MappingJacksonValue(result);
				mappingJacksonValue.setJsonpFunction(callback);
				return mappingJacksonValue;//有回调函数，拼接后返回
			}else{
				return result;//没有回调函数，直接返回
			}
		}
		
		//校验后，结果完全正确，可以直接调用服务，进行注册
		try{
			result = userService.checkData(param, type);
			
		}catch(Exception e){
			result=TaotaoResult.build(500, ExceptionUtil.getStackTrace(e));
		}
		if(null != callback){//校验完成后，也要判断回调，如果有回调，返回到相应页面，否则，直接返回结果
			MappingJacksonValue mappingJacksonValue=new MappingJacksonValue(result);
			mappingJacksonValue.setJsonpFunction(callback);
			return mappingJacksonValue;
		}else{
			return result;
		}
	}
	
	@RequestMapping(value="/register", method=RequestMethod.POST)
	@ResponseBody
	public TaotaoResult createUser(TbUser user) {
		try {
			TaotaoResult result = userService.createUser(user);
			return result;
		} catch (Exception e) {
			return TaotaoResult.build(500, ExceptionUtil.getStackTrace(e));
		}
	}
	
	@RequestMapping(value="/login", method=RequestMethod.POST)
	@ResponseBody
	public TaotaoResult userLogin(String username, String password
			,HttpServletRequest request,HttpServletResponse response) {
		try {
			TaotaoResult result = userService.userLogin(username, password,request,response);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return TaotaoResult.build(500, ExceptionUtil.getStackTrace(e));
		}
	}
	
	@RequestMapping("/token/{token}")
	@ResponseBody
	public Object getUserByToken(@PathVariable String token, String callback) {
		TaotaoResult result = null;
		try {
			result = userService.getUserByToken(token);
		} catch (Exception e) {
			e.printStackTrace();
			result = TaotaoResult.build(500, ExceptionUtil.getStackTrace(e));
		}

		if(!StringUtils.isBlank(callback)){
			MappingJacksonValue mappingJacksonValue=new MappingJacksonValue(result);
			mappingJacksonValue.setJsonpFunction(callback);
			return mappingJacksonValue;
		}
		return result;
	}
	
	@RequestMapping("/logout/{token}")
	@ResponseBody
	public Object logout(@PathVariable String token,String callback){
		TaotaoResult result;
		try{
			result = userService.logout(token);
		}catch(Exception e){
			e.printStackTrace();
			result=TaotaoResult.build(500, ExceptionUtil.getStackTrace(e));
		}
		if(StringUtils.isBlank(callback)){
			return "login";
		}else{
			MappingJacksonValue mappingJacksonValue=new MappingJacksonValue(result);
			mappingJacksonValue.setJsonpFunction(callback);
			return mappingJacksonValue;
		}
	}
}
