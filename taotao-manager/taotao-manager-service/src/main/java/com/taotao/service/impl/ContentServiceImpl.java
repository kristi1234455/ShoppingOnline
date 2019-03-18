package com.taotao.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.pojo.EUDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.HttpClientUtil;
import com.taotao.mapper.TbContentMapper;
import com.taotao.pojo.TbContent;
import com.taotao.pojo.TbContentExample;
import com.taotao.pojo.TbContentExample.Criteria;
import com.taotao.service.ContentService;

@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper tbContentMapper;
	@Value("${REST_BASE_URL}")
	private String REST_BASE_URL;
	@Value("${REST_CONTENT_SYNC_URL}")
	private String REST_CONTENT_SYNC_URL;

	@Override
	public EUDataGridResult getContentList(Integer page, Integer rows, Long contentCid) {
		PageHelper.startPage(page, rows);

		TbContentExample tbContentExample = new TbContentExample();
		Criteria criteria = tbContentExample.createCriteria();
		criteria.andCategoryIdEqualTo(contentCid);
		List<TbContent> list = tbContentMapper.selectByExample(tbContentExample);

		PageInfo<TbContent> pageInfo = new PageInfo<>(list);
		EUDataGridResult result = new EUDataGridResult(pageInfo.getTotal(), list);
		return result;
	}

	@Override
	public TaotaoResult insertContent(TbContent content) {
		// 补全pojo内容
		content.setCreated(new Date());
		content.setUpdated(new Date());
		tbContentMapper.insert(content);
		
		//改变数据库中content大广告位内容后，需要清空相应缓存
		try{
			HttpClientUtil.doGet(REST_BASE_URL+REST_CONTENT_SYNC_URL+content.getCategoryId());
		}catch(Exception e){
			e.printStackTrace();
			return TaotaoResult.build(500, "缓存同步失败");
		}
		
		return TaotaoResult.ok();
	}

	@Override
	public TaotaoResult updateContent(TbContent content) {
		// 补全pojo内容
		content.setCreated(new Date());
		content.setUpdated(new Date());
		int result=tbContentMapper.updateByPrimaryKeySelective(content);
		if(result > 0){
			//改变数据库中content大广告位内容后，需要清空相应缓存
			try{
				HttpClientUtil.doGet(REST_BASE_URL+REST_CONTENT_SYNC_URL+content.getCategoryId());
			}catch(Exception e){
				e.printStackTrace();
				return TaotaoResult.build(500, "缓存同步失败");
			}
			return TaotaoResult.ok();
		}
		return TaotaoResult.build(400,"更新失败，请稍后重试！");
	}

	@Override
	public TaotaoResult deleteContent(Long id) {
		TbContent tbContent=tbContentMapper.selectByPrimaryKey(id);
		Long cid=tbContent.getCategoryId();
		
		int result=tbContentMapper.deleteByPrimaryKey(id);
		 
		if(result > 0){
			//改变数据库中content大广告位内容后，需要清空相应缓存
			try{
				HttpClientUtil.doGet(REST_BASE_URL+REST_CONTENT_SYNC_URL+cid);
			}catch(Exception e){
				e.printStackTrace();
				return TaotaoResult.build(500, "缓存同步失败");
			}
			return TaotaoResult.ok();
		}
		return TaotaoResult.build(400,"删除失败，请稍后重试！");
	}
}
