package com.jll.dao;

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
	
	private List<T> content;

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
	
	
}
