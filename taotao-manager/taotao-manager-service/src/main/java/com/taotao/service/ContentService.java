package com.taotao.service;

import java.util.List;

import com.taotao.common.pojo.EUDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbContent;

public interface ContentService {

	EUDataGridResult getContentList(Integer page,Integer rows,Long contentCid);
	
	TaotaoResult insertContent(TbContent content);

	TaotaoResult updateContent(TbContent content);

	TaotaoResult deleteContent(Long id);
}
