package com.taotao.common.pojo;

import java.util.List;

public class EUDataGridResult {
	private long total;
	private List<?> rows;
	public EUDataGridResult() {
		super();
	}
	public EUDataGridResult(long total, List<?> rows) {
		super();
		this.total = total;
		this.rows = rows;
	}
	public EUDataGridResult(Integer total, List<?> rows) {
		super();
		this.total = total;
		this.rows = rows;
	}
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	public List<?> getRows() {
		return rows;
	}
	public void setRows(List<?> rows) {
		this.rows = rows;
	}
}

