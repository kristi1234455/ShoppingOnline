package com.taotao.portal.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.HttpClientUtil;
import com.taotao.common.utils.JsonUtils;
import com.taotao.portal.pojo.Order;
import com.taotao.portal.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

	@Value("${ORDER_BASE_URL}")
	private String ORDER_BASE_URL;
	@Value("${ORDER_CREATE_URL}")
	private String ORDER_CREATE_URL;
	
	@Override
	public String createOrder(Order order) {
		//调用taotao-order的服务提交订单。
		//这里将order变成json格式数据，采用post提交。因为Post的是一个json数据，而不是一个表单
		// 如果是post，且是表单，是调用doPost方法，第二个参数是map
		String json = HttpClientUtil.doPostJson(ORDER_BASE_URL + ORDER_CREATE_URL,JsonUtils.objectToJson(order));
		//把json转换成taotaoResult
		TaotaoResult taotaoResult = TaotaoResult.format(json);
		if (taotaoResult.getStatus() == 200) {
			Object orderId =  taotaoResult.getData();
			return orderId.toString();
		}
		return "";
	}
}

