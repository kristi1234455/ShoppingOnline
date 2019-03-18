package com.taotao.portal.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.CookieUtils;
import com.taotao.common.utils.HttpClientUtil;
import com.taotao.common.utils.JsonUtils;
import com.taotao.pojo.TbItem;
import com.taotao.portal.pojo.CartItem;
import com.taotao.portal.service.CartService;

@Service
public class CartServiceImpl implements CartService {
	@Value("${REST_BASE_URL}")
	private String REST_BASE_URL;
	@Value("${ITME_INFO_URL}")
	private String ITME_INFO_URL;
	
	@Override
	public TaotaoResult addCartItem(long itemId, int num, HttpServletRequest request, HttpServletResponse response) {
		CartItem cartItem=null;
		//取出购物车中的商品
		List<CartItem> list = getCartItemList(request);
		//如果购物车中有该商品
		for(CartItem cartItem2 : list){
			if(cartItem2.getId() == itemId){
				cartItem2.setNum(cartItem2.getNum()+num);
				cartItem=cartItem2;
				break;
			}
		}
		//如果购物车中没有该商品
		if(null == cartItem){
			cartItem = new CartItem();
			String json = HttpClientUtil.doGet(REST_BASE_URL+ITME_INFO_URL+itemId);
			TaotaoResult taotaoResult = TaotaoResult.formatToPojo(json, TbItem.class);
			if(taotaoResult.getStatus() == 200){
				TbItem tbItem = (TbItem)taotaoResult.getData();
				cartItem.setId(tbItem.getId());
				cartItem.setTitle(tbItem.getTitle());
				cartItem.setImage(tbItem.getImage()==null ? "" :tbItem.getImage().split(",")[0]);
				cartItem.setNum(num);
				cartItem.setPrice(tbItem.getPrice());
			}
			//将该商品添加到购物车
			list.add(cartItem);
		}
		//将购物车写入cookie
		CookieUtils.setCookie(request, response, "TT_CART", JsonUtils.objectToJson(list),true);
		return TaotaoResult.ok();
	}
	
	private List<CartItem> getCartItemList(HttpServletRequest request) {
		String json = CookieUtils.getCookieValue(request, "TT_CART",true);
		if(StringUtils.isBlank(json)){
			return new ArrayList<>();
		}
		try{
			List<CartItem> list = JsonUtils.jsonToList(json, CartItem.class);
			return list;
		}catch(Exception e){
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
	
	public List<CartItem> getCartItemList(HttpServletRequest request,HttpServletResponse response){
		List<CartItem> cartItemList = getCartItemList(request);
		return cartItemList;
	}

	@Override
	public TaotaoResult deleteCartItem(long itemId, HttpServletRequest request, HttpServletResponse response) {
		List<CartItem> list = getCartItemList(request);
		for(CartItem cartItem : list){
			if(cartItem.getId() == itemId){
				list.remove(cartItem);
				break;
			}
		}
		//重新设置cookie
		CookieUtils.setCookie(request, response, "TT_CART", JsonUtils.objectToJson(list),true);
		return TaotaoResult.ok();
	}

	@Override
	public TaotaoResult updateCartItem(long itemId, int num, HttpServletRequest request, HttpServletResponse response) {
		CartItem cartItem=null;
		//取出购物车中的商品
		List<CartItem> list = getCartItemList(request);
		//如果购物车中有该商品
		for(CartItem cartItem2 : list){
			if(cartItem2.getId() == itemId){
				cartItem2.setNum(num);
				cartItem=cartItem2;
				break;
			}
		}
		//如果购物车中没有该商品
		if(null == cartItem){
			cartItem = new CartItem();
			String json = HttpClientUtil.doGet(REST_BASE_URL+ITME_INFO_URL+itemId);
			TaotaoResult taotaoResult = TaotaoResult.formatToPojo(json, TbItem.class);
			if(taotaoResult.getStatus() == 200){
				TbItem tbItem = (TbItem)taotaoResult.getData();
				cartItem.setId(tbItem.getId());
				cartItem.setTitle(tbItem.getTitle());
				cartItem.setImage(tbItem.getImage()==null ? "" :tbItem.getImage().split(",")[0]);
				cartItem.setNum(num);
				cartItem.setPrice(tbItem.getPrice());
			}
			//将该商品添加到购物车
			list.add(cartItem);
		}
		//将购物车写入cookie
		CookieUtils.setCookie(request, response, "TT_CART", JsonUtils.objectToJson(list),true);
		return TaotaoResult.ok();
	}
}
