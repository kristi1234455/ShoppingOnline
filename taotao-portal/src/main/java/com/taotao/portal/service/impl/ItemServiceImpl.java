package com.taotao.portal.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.HttpClientUtil;
import com.taotao.common.utils.JsonUtils;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.pojo.TbItemParamItem;
import com.taotao.portal.pojo.ItemInfo;
import com.taotao.portal.service.ItemService;

@Service
public class ItemServiceImpl implements ItemService {

	@Value("${REST_BASE_URL}")
	private String REST_BASE_URL;
	@Value("${ITME_INFO_URL}")
	private String ITME_INFO_URL;
	@Value("${ITEM_DESC_URL}")
	private String ITEM_DESC_URL;
	@Value("${ITEM_PARAM_URL}")
	private String ITEM_PARAM_URL;
	
	@Override
	public TbItem getItemById(Long itemId) {
		try{
			String json= HttpClientUtil.doGet(REST_BASE_URL+ITME_INFO_URL+itemId);
			if(!StringUtils.isBlank(json)){
				TaotaoResult result = TaotaoResult.formatToPojo(json, ItemInfo.class);
				if(result.getStatus() == 200){
					ItemInfo tbItem = (ItemInfo) result.getData();
					return tbItem;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getItemDescById(Long itemId) {
		try{
			String json = HttpClientUtil.doGet(REST_BASE_URL+ITEM_DESC_URL+itemId);
			System.out.println(json);
			if(!StringUtils.isBlank(json)){
				TaotaoResult taotaoResult = TaotaoResult.formatToPojo(json, TbItemDesc.class);
				if(taotaoResult.getStatus() == 200){
					TbItemDesc tbItemDesc = (TbItemDesc) taotaoResult.getData();
					String itemDesc = tbItemDesc.getItemDesc();
					return itemDesc;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getItemParam(Long itemId) {
		try{
			String json = HttpClientUtil.doGet(REST_BASE_URL+ITEM_PARAM_URL+itemId);
			if(!StringUtils.isBlank(json)){
				TaotaoResult taotaoResult = TaotaoResult.formatToPojo(json, TbItemParamItem.class);
				if(taotaoResult.getStatus() == 200){
					TbItemParamItem tbItemParamItem = (TbItemParamItem)taotaoResult.getData();
					String paramData = tbItemParamItem.getParamData();
					
					//将规格参数生成html页面，然后返回，item.jsp页面只接收该数据后，直接追加到tab页面
					//将规格参数数据，json格式的数据，内部是两个Map类型的数据，一个group，一个params，转化成java对象
					List<Map> jsonList= JsonUtils.jsonToList(paramData, Map.class);//先将json转化为list，内部类型是map
					StringBuffer sb=new StringBuffer();
					sb.append("<table cellpadding=\"0\" cellspacing=\"1\" width=\"100%\" border=\"0\" class=\"Ptable\">\n");
					sb.append("    <tbody>\n");
					for(Map m1 : jsonList){//遍历list，依次取出map
						sb.append("        <tr>\n");
						sb.append("            <th class=\"tdTitle\" colspan=\"2\">"+m1.get("group")+"</th>\n");//第一个map，key为group
						sb.append("        </tr>\n");
						List<Map> list2 = (List<Map>) m1.get("params");//第二个map，key为params。第一次遍历时，取不到，第二次遍历时，才取到
						for(Map m2 : list2){//第二次遍历时，取到key为params的值，再次遍历，取出规格参数项和规格参数值。也是map类型
							sb.append("        <tr>\n");
							sb.append("            <td class=\"tdTitle\">"+m2.get("k")+"</td>\n");
							sb.append("            <td>"+m2.get("v")+"</td>\n");
							sb.append("        </tr>\n");
						}
					}
					sb.append("    </tbody>\n");
					sb.append("</table>");
					//返回html片段
					return sb.toString();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}

}
