package com.jll.entity;
// Generated 2018-7-25 12:24:23 by Hibernate Tools 5.2.10.Final

import java.util.Date;

/**
 * Promo generated by hbm2java
 */
public class Promo implements java.io.Serializable {

	private Integer id;
	private String promoName;
	private String promoType;
	private Integer isMultiple;
	private Float minDepositAmount;
	private Integer flowTimes;
	private Integer valueType;
	private Float value;
	private Date expiredTime;
	private Integer withdrawFlowTimes;
	private Date createTime;
	private Date creator;
	private Date updateTime;

	public Promo() {
	}

	public Promo(String promoName, String promoType, Integer isMultiple, Float minDepositAmount, Integer flowTimes,
			Integer valueType, Float value, Date expiredTime, Integer withdrawFlowTimes, Date createTime, Date creator,
			Date updateTime) {
		this.promoName = promoName;
		this.promoType = promoType;
		this.isMultiple = isMultiple;
		this.minDepositAmount = minDepositAmount;
		this.flowTimes = flowTimes;
		this.valueType = valueType;
		this.value = value;
		this.expiredTime = expiredTime;
		this.withdrawFlowTimes = withdrawFlowTimes;
		this.createTime = createTime;
		this.creator = creator;
		this.updateTime = updateTime;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPromoName() {
		return this.promoName;
	}

	public void setPromoName(String promoName) {
		this.promoName = promoName;
	}

	public String getPromoType() {
		return this.promoType;
	}

	public void setPromoType(String promoType) {
		this.promoType = promoType;
	}

	public Integer getIsMultiple() {
		return this.isMultiple;
	}

	public void setIsMultiple(Integer isMultiple) {
		this.isMultiple = isMultiple;
	}

	public Float getMinDepositAmount() {
		return this.minDepositAmount;
	}

	public void setMinDepositAmount(Float minDepositAmount) {
		this.minDepositAmount = minDepositAmount;
	}

	public Integer getFlowTimes() {
		return this.flowTimes;
	}

	public void setFlowTimes(Integer flowTimes) {
		this.flowTimes = flowTimes;
	}

	public Integer getValueType() {
		return this.valueType;
	}

	public void setValueType(Integer valueType) {
		this.valueType = valueType;
	}

	public Float getValue() {
		return this.value;
	}

	public void setValue(Float value) {
		this.value = value;
	}

	public Date getExpiredTime() {
		return this.expiredTime;
	}

	public void setExpiredTime(Date expiredTime) {
		this.expiredTime = expiredTime;
	}

	public Integer getWithdrawFlowTimes() {
		return this.withdrawFlowTimes;
	}

	public void setWithdrawFlowTimes(Integer withdrawFlowTimes) {
		this.withdrawFlowTimes = withdrawFlowTimes;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getCreator() {
		return this.creator;
	}

	public void setCreator(Date creator) {
		this.creator = creator;
	}

	public Date getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

}
