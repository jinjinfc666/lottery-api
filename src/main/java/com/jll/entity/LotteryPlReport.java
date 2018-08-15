package com.jll.entity;
// Generated 2018-8-14 16:52:58 by Hibernate Tools 5.2.10.Final

import java.math.BigDecimal;
import java.util.Date;

/**
 * LotteryPlReport generated by hbm2java
 */
public class LotteryPlReport implements java.io.Serializable {

	private Integer id;
	private Date createTime;
	private String codeName;
	private String userName;
	private BigDecimal consumption;
	private BigDecimal cancelAmount;
	private BigDecimal returnPrize;
	private BigDecimal rebate;
	private BigDecimal profit;
	private Integer userType;

	public LotteryPlReport() {
	}

	public LotteryPlReport(Date createTime) {
		this.createTime = createTime;
	}

	public LotteryPlReport(Date createTime, String codeName, String userName, BigDecimal consumption,
			BigDecimal cancelAmount, BigDecimal returnPrize, BigDecimal rebate, BigDecimal profit, Integer userType) {
		this.createTime = createTime;
		this.codeName = codeName;
		this.userName = userName;
		this.consumption = consumption;
		this.cancelAmount = cancelAmount;
		this.returnPrize = returnPrize;
		this.rebate = rebate;
		this.profit = profit;
		this.userType = userType;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCodeName() {
		return this.codeName;
	}

	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public BigDecimal getConsumption() {
		return this.consumption;
	}

	public void setConsumption(BigDecimal consumption) {
		this.consumption = consumption;
	}

	public BigDecimal getCancelAmount() {
		return this.cancelAmount;
	}

	public void setCancelAmount(BigDecimal cancelAmount) {
		this.cancelAmount = cancelAmount;
	}

	public BigDecimal getReturnPrize() {
		return this.returnPrize;
	}

	public void setReturnPrize(BigDecimal returnPrize) {
		this.returnPrize = returnPrize;
	}

	public BigDecimal getRebate() {
		return this.rebate;
	}

	public void setRebate(BigDecimal rebate) {
		this.rebate = rebate;
	}

	public BigDecimal getProfit() {
		return this.profit;
	}

	public void setProfit(BigDecimal profit) {
		this.profit = profit;
	}

	public Integer getUserType() {
		return this.userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

}
