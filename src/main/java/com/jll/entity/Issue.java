package com.jll.entity;
// Generated 2018-7-18 10:09:13 by Hibernate Tools 5.2.10.Final

import java.util.Date;

/**
 * Issue generated by hbm2java
 */
public class Issue implements java.io.Serializable {

	private Integer id;
	private String lotteryType;
	private String issueNum;
	private String retNum;
	private Integer state;
	private Date startTime;
	private Date endTime;

	public Issue() {
	}

	public Issue(String lotteryType, String issueNum, String retNum, Integer state, Date startTime, Date endTime) {
		this.lotteryType = lotteryType;
		this.issueNum = issueNum;
		this.retNum = retNum;
		this.state = state;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLotteryType() {
		return this.lotteryType;
	}

	public void setLotteryType(String lotteryType) {
		this.lotteryType = lotteryType;
	}

	public String getIssueNum() {
		return this.issueNum;
	}

	public void setIssueNum(String issueNum) {
		this.issueNum = issueNum;
	}

	public String getRetNum() {
		return this.retNum;
	}

	public void setRetNum(String retNum) {
		this.retNum = retNum;
	}

	public Integer getState() {
		return this.state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Date getStartTime() {
		return this.startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return this.endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

}
