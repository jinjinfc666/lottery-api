package com.jll.dao;

import java.util.Date;

import com.jll.common.utils.DateUtil;


public class PageQueryDao {
	
	private Date startDate;
	private Date endDate;
	private String billNo;
	private Integer pageIndex;
	private Integer pageSize;
	
	private Integer DEFAULT_PAGE_INDEX=1;
	private Integer DEFAULT_PAGE_SIZE=20;
	
	public PageQueryDao() {
		this.pageIndex = DEFAULT_PAGE_INDEX;
		this.pageSize = DEFAULT_PAGE_SIZE;
	}

	public PageQueryDao( Integer pageIndex, Integer pageSize) {
		super();
		this.pageIndex = pageIndex;
		this.pageSize = pageSize;
	}
	

	public PageQueryDao(Date startDate, Date endDate, Integer pageIndex, Integer pageSize) {
		super();
		this.startDate = startDate;
		this.endDate = endDate;
		this.pageIndex = pageIndex;
		this.pageSize = pageSize;
	}

	public Date getStartDate() {
		if(null == startDate){
			return DateUtil.getDateDayStart(new Date());
		}
		return startDate;
	}


	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}


	public Date getEndDate() {
		if(null == endDate){
			return DateUtil.getDateDayEnd(new Date());
		}
		return endDate;
	}


	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}


	public String getBillNo() {
		return billNo;
	}


	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}


	public Integer getPageIndex() {
		if(null == pageSize){
			return DEFAULT_PAGE_INDEX;
		}
		return pageIndex;
	}


	public void setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
	}


	public Integer getPageSize() {
		if(null == pageSize){
			return DEFAULT_PAGE_SIZE;
		}
		return pageSize;
	}


	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	
	
}
