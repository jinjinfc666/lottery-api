package com.jll.dao;

import java.util.ArrayList;
import java.util.List;

public class PageBean<T> {

	/**
	 * 当前请求页码
	 * 从 0 开始
	 */
	private Integer pageIndex;
	
	/**
	 * 总页数
	 */
	private Long totalPages;
	
	/**
	 * 每页记录数
	 */
	private Integer pageSize;
	/**
	 * 总记录数
	 */
	private Long totalNumber;
	
	private List<T> content;

	private List<Object> params;
	
	public Integer getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
	}

	public Long getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(Long totalPages) {
		this.totalPages = totalPages;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public List<T> getContent() {
		return content;
	}

	public void setContent(List<T> content) {
		this.content = content;
	}

	public Long getTotalNumber() {
		return totalNumber;
	}

	public void setTotalNumber(Long totalNumber) {
		this.totalNumber = totalNumber;
	}

	public List<Object> getParams() {
		return params;
	}

	public void setParams(List<Object> params) {
		this.params = params;
	}
	
	
}
