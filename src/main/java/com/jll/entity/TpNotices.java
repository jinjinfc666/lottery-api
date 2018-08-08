package com.jll.entity;
// Generated 2018-8-8 15:26:50 by Hibernate Tools 5.2.10.Final

import java.util.Date;

/**
 * TpNotices generated by hbm2java
 */
public class TpNotices implements java.io.Serializable {

	private Integer id;
	private String result;
	private Float amount;
	private String orderNum;
	private String transactionNum;
	private Date createTime;

	public TpNotices() {
	}

	public TpNotices(String result, Float amount, String orderNum, String transactionNum, Date createTime) {
		this.result = result;
		this.amount = amount;
		this.orderNum = orderNum;
		this.transactionNum = transactionNum;
		this.createTime = createTime;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getResult() {
		return this.result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Float getAmount() {
		return this.amount;
	}

	public void setAmount(Float amount) {
		this.amount = amount;
	}

	public String getOrderNum() {
		return this.orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public String getTransactionNum() {
		return this.transactionNum;
	}

	public void setTransactionNum(String transactionNum) {
		this.transactionNum = transactionNum;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}
