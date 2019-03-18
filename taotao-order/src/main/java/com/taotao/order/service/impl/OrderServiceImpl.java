package com.taotao.order.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.mapper.TbOrderItemMapper;
import com.taotao.mapper.TbOrderMapper;
import com.taotao.mapper.TbOrderShippingMapper;
import com.taotao.order.dao.JedisClient;
import com.taotao.order.pojo.Order;
import com.taotao.order.service.OrderService;
import com.taotao.pojo.TbOrder;
import com.taotao.pojo.TbOrderExample;
import com.taotao.pojo.TbOrderItem;
import com.taotao.pojo.TbOrderItemExample;
import com.taotao.pojo.TbOrderItemExample.Criteria;
import com.taotao.pojo.TbOrderShipping;
import com.taotao.pojo.TbOrderShippingExample;

@Service
public class OrderServiceImpl implements OrderService {
	@Autowired
	private TbOrderMapper tbOrderMapper;
	@Autowired
	private TbOrderItemMapper tbOrderItemMapper;
	@Autowired
	private TbOrderShippingMapper tbOrderShippingMapper;

	@Autowired
	private JedisClient jedisClient;
	@Value("${ORDER_GEN_KEY}")
	private String ORDER_GEN_KEY;
	@Value("${ORDER_INIT_ID}")
	private String ORDER_INIT_ID;
	@Value("${ORDER_DETAIL_GEN_KEY}")
	private String ORDER_DETAIL_GEN_KEY;
	
	@Override
	public TaotaoResult createOrder(TbOrder order, List<TbOrderItem> itemList, TbOrderShipping orderShipping) {
		String json = jedisClient.get(ORDER_GEN_KEY);
		if(StringUtils.isBlank(json)){//没有订单号，就需要生成一个订单号
			jedisClient.set(ORDER_GEN_KEY, ORDER_INIT_ID);
		}
		//如果有订单号，就需要自增
		long orderId = jedisClient.incr(ORDER_GEN_KEY);
		order.setOrderId(orderId +"");
		order.setStatus(1);//状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭
		Date date=new Date();
		order.setCreateTime(date);
		order.setUpdateTime(date);
		order.setBuyerRate(0);//0：未评价 1：已评价
		tbOrderMapper.insert(order);
		
		for(TbOrderItem tbOrderItem : itemList){
			long orderDetailId = jedisClient.incr(ORDER_DETAIL_GEN_KEY);
			tbOrderItem.setId(orderDetailId+"");
			tbOrderItem.setOrderId(orderId+"");
			tbOrderItemMapper.insert(tbOrderItem);
		}
		
		orderShipping.setOrderId(orderId+"");
		orderShipping.setCreated(date);
		orderShipping.setUpdated(date);
		tbOrderShippingMapper.insert(orderShipping);
		
		return TaotaoResult.ok(orderId);
	}

	@Override
	public TaotaoResult getOrderById(long orderId) {
		Order order=new Order();
		TbOrder tbOrder = tbOrderMapper.selectByPrimaryKey(orderId+"");
		order.setPayment(tbOrder.getPayment());
		order.setPostFee(tbOrder.getPostFee());
		order.setUserId(tbOrder.getUserId());
		order.setBuyerMessage(tbOrder.getBuyerMessage());
		order.setBuyerNick(tbOrder.getBuyerNick());
		
		TbOrderItemExample example=new TbOrderItemExample();
		Criteria criteria = example.createCriteria();
		criteria.andOrderIdEqualTo(orderId+"");
		List<TbOrderItem> tbOrderItems = tbOrderItemMapper.selectByExample(example);
		order.setOrderItems(tbOrderItems);
		
		TbOrderShippingExample example2=new TbOrderShippingExample();
		com.taotao.pojo.TbOrderShippingExample.Criteria criteria2 = example2.createCriteria();
		criteria2.andOrderIdEqualTo(orderId+"");
		List<TbOrderShipping> tbOrderShippings = tbOrderShippingMapper.selectByExample(example2);
		if(tbOrderShippings !=null && tbOrderShippings.size() >0){
			order.setOrderShipping(tbOrderShippings.get(0));
		}
		
		return TaotaoResult.ok(order);
	}

	@Override
	public TaotaoResult getOrderByPage(long userId, int page, int count) {
		List<Order> result=new ArrayList<>();
		TbOrderExample tbOrderExample=new TbOrderExample();
		com.taotao.pojo.TbOrderExample.Criteria criteria=tbOrderExample.createCriteria();
		criteria.andUserIdEqualTo(userId);
		List<TbOrder> list = tbOrderMapper.selectByExample(tbOrderExample);
		for(TbOrder tbOrder : list){
			Order order =new Order();
			order.setOrderId(tbOrder.getOrderId());
			order.setPayment(tbOrder.getPayment());
			order.setPaymentType(tbOrder.getPaymentType());
			order.setStatus(tbOrder.getStatus());
			order.setCreateTime(tbOrder.getCreateTime());
			order.setPostFee(tbOrder.getPostFee());
			order.setUserId(tbOrder.getUserId());
			order.setBuyerMessage(tbOrder.getBuyerMessage());
			order.setBuyerNick(tbOrder.getBuyerNick());
			
			result.add(order);
		}
		return TaotaoResult.ok(result);
	}
}
