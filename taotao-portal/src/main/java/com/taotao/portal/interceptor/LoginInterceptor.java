package com.taotao.portal.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.common.utils.CookieUtils;
import com.taotao.pojo.TbUser;
import com.taotao.portal.service.impl.UserServiceImpl;

public class LoginInterceptor implements HandlerInterceptor {

	@Autowired
	private UserServiceImpl userService;
	
	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		//返回ModelAndView之后
		//响应用户之后
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		//handler执行之后，返回ModelAndView之前
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		//在handler执行之前，进行判断，是否已经登陆
		String token = CookieUtils.getCookieValue(request, "TT_TOKEN");
		TbUser tbUser = userService.getUserByToken(token);
		if(null == tbUser){
			response.sendRedirect(userService.SSO_BASE_URL + userService.SSO_PAGE_LOGIN 
					+ "?redirect=" + request.getRequestURL());//调用了UserServiceImpl的值
//这个url请求，带有参数，参数名为redirect，参数值为request.getRequestURL()方法，该方法返回的是请求的全路径
//转到登录页面，登录成功后，会再次转到登录前，用户请求的页面
			return false;//返回false
		}
		//取到用户信息，放行
		//把用户信息放入request中
		request.setAttribute("user", tbUser);
		//返回值决定handler是否执行。true：执行，false：不执行。
		return true;
	}

}
