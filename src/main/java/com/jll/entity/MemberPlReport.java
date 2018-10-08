package com.jll.entity;
// Generated 2018-8-14 16:52:58 by Hibernate Tools 5.2.10.Final

import java.math.BigDecimal;
import java.util.Date;

/**
 * MemberPlReport generated by hbm2java
 */
public class MemberPlReport implements java.io.Serializable {

	private Integer id;
	private Date createTime;
	private String userName;
	private BigDecimal deposit;    
	private BigDecimal withdrawal;
	private BigDecimal deduction;
	private BigDecimal consumption;
	private BigDecimal cancelAmount;
	private BigDecimal returnPrize;
	private BigDecimal rebate;
	private BigDecimal currentBalance;
	private Integer rechargeMember;
	private Integer newMembers;
	private BigDecimal profit;
	private Integer userType;

	public MemberPlReport() {
	}

	public MemberPlReport(Date createTime) {
		this.createTime = createTime;
	}

	public MemberPlReport(Date createTime, String userName, BigDecimal deposit,
			BigDecimal withdrawal, BigDecimal deduction, BigDecimal consumption, BigDecimal cancelAmount,
			BigDecimal returnPrize, BigDecimal rebate, BigDecimal currentBalance, Integer rechargeMember,
			Integer newMembers, BigDecimal profit, Integer userType) {
		this.createTime = createTime;
		this.userName = userName;
		this.deposit = deposit;
		this.withdrawal = withdrawal;
		this.deduction = deduction;
		this.consumption = consumption;
		this.cancelAmount = cancelAmount;
		this.returnPrize = returnPrize;
		this.rebate = rebate;
		this.currentBalance = currentBalance;
		this.rechargeMember = rechargeMember;
		this.newMembers = newMembers;
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

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public BigDecimal getDeposit() {
		return this.deposit;
	}

	public void setDeposit(BigDecimal deposit) {
		this.deposit = deposit;
	}


	public BigDecimal getWithdrawal() {
		return this.withdrawal;
	}

	public void setWithdrawal(BigDecimal withdrawal) {
		this.withdrawal = withdrawal;
	}

	public BigDecimal getDeduction() {
		return this.deduction;
	}

	public void setDeduction(BigDecimal deduction) {
		this.deduction = deduction;
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

	public BigDecimal getCurrentBalance() {
		return this.currentBalance;
	}

	public void setCurrentBalance(BigDecimal currentBalance) {
		this.currentBalance = currentBalance;
	}

	public Integer getRechargeMember() {
		return this.rechargeMember;
	}

	public void setRechargeMember(Integer rechargeMember) {
		this.rechargeMember = rechargeMember;
	}

	public Integer getNewMembers() {
		return this.newMembers;
	}

	public void setNewMembers(Integer newMembers) {
		this.newMembers = newMembers;
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
