package com.jll.entity;
// Generated 2018-8-14 16:52:58 by Hibernate Tools 5.2.10.Final

import java.math.BigDecimal;

/**
 * UserAccount generated by hbm2java
 */
public class UserAccount implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4413435954571834313L;
	
	private Integer id;
	private Integer userId;
	private String accName;
	private Double balance;
	private Double freeze;
	private Double prize;
	private Long rewardPoints;
	private Integer accType;
	private Integer state;
	private String remark;

	public UserAccount() {
	}

	public UserAccount(Integer userId, String accName, Double balance, Double freeze, Double prize,
			Long rewardPoints, Integer accType,Integer state, String remark) {
		this.userId = userId;
		this.accName = accName;
		this.balance = balance;
		this.freeze = freeze;
		this.prize = prize;
		this.rewardPoints = rewardPoints;
		this.accType = accType;
		this.state = state;
		this.remark = remark;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return this.userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getAccName() {
		return this.accName;
	}

	public void setAccName(String accName) {
		this.accName = accName;
	}

	public Double getBalance() {
		return this.balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public Double getFreeze() {
		return this.freeze;
	}

	public void setFreeze(Double freeze) {
		this.freeze = freeze;
	}

	public Double getPrize() {
		return this.prize;
	}

	public void setPrize(Double prize) {
		this.prize = prize;
	}

	public Long getRewardPoints() {
		return this.rewardPoints;
	}

	public void setRewardPoints(Long rewardPoints) {
		this.rewardPoints = rewardPoints;
	}

	public Integer getAccType() {
		return this.accType;
	}

	public void setAccType(Integer accType) {
		this.accType = accType;
	}
	
	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
