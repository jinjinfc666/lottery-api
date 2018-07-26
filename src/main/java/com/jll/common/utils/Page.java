package com.jll.common.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Page implements Serializable,Cloneable {

	public static final Integer PAGE_BEGIN_INDEX = 1;
	public static final Integer PAGE_DEFAULT_SIZE = 20;
	private static final long serialVersionUID = 1L;
	private Integer pageNumber;
	private Integer totalPages;
	private Integer size;
	private List<?> pageContents;
	private Integer totalRecords;
	private Integer curRecords;
	
	public Page() {
		pageNumber = 0;
		totalPages = 0;
		size = 0;
		pageContents = new ArrayList<Object>();
		totalRecords = 0;
		curRecords= 0;
	}

	public Page(List<?> pageContents) {
		pageNumber = 0;
		totalPages = 0;
		size = 0;
		this.pageContents = pageContents;
		totalRecords = 0;
		curRecords= 0;
		this.pageContents = pageContents;
	}
	
	public Integer getCurRecords() {
		return curRecords;
	}

	public void setCurRecords(Integer curRecords) {
		this.curRecords = curRecords;
	}

	public void setPageContents(List<?> pageContents) {
		this.pageContents = pageContents;
		this.setCurRecords(0);
		if(null != pageContents){
			this.setCurRecords(pageContents.size());
		}
	}

	public List<?>  getPageContents() {
		return pageContents;
	}

	public Integer getPageNumber() {
		return pageNumber;
	}

	public Integer getSize() {
		return size;
	}
	
	public Integer getTotalPages() {
		return totalPages;
	}

	public Integer getTotalRecords() {
		return totalRecords;
	}

	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = totalPages == null ? pageNumber : pageNumber.intValue() <= totalPages.intValue() ? pageNumber : PAGE_BEGIN_INDEX;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public void setTotalPages(Integer totalPages) {
		if (pageNumber != null)
			pageNumber = totalPages.intValue() >= pageNumber.intValue() ? pageNumber : PAGE_BEGIN_INDEX;
		this.totalPages = totalPages;
	}

	public void setTotalRecords(Integer totalRecords) {
		this.totalRecords = totalRecords;
	}

	@Override
	public String toString() {
		return (new StringBuilder("Page[pageNumber:")).append(pageNumber).append(";size:").append(size).append(";totalPages:").append(totalPages).append(";pageContent:").append(pageContents.toString()).append("]").toString();
	}

	@Override
	protected Page clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return (Page)super.clone();
	}
}
