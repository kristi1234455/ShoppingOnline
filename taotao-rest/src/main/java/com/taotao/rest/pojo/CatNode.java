package com.taotao.rest.pojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CatNode {
	@JsonProperty("n")//表示在转换成json数据之后，本来是name为key，现在改为n为key
	private String name;
	
	@JsonProperty("u")
	private String url;
	
	@JsonProperty("i")
	private List<?> item;//第一、二层是catNode，第三层是string字符串

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<?> getItem() {
		return item;
	}

	public void setItem(List<?> item) {
		this.item = item;
	}

}
