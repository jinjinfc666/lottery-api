package com.jll.entity;
// Generated 2018-8-8 15:26:50 by Hibernate Tools 5.2.10.Final

import java.math.BigDecimal;
import java.util.Date;

/**
 * LotteryPlatformProfit generated by hbm2java
 */
public class LotteryPlatformProfit implements java.io.Serializable {

	private Integer id;
	private Date createTime;
	private String codeName;
	private String lotteryType;
	private String issueNum;
	private Integer playTypeid;
	private String playType;
	private BigDecimal betting;
	private BigDecimal cancelAmount;
	private BigDecimal winning;
	private BigDecimal rebate;
	private BigDecimal platformProfit;

	public LotteryPlatformProfit() {
	}

	public LotteryPlatformProfit(Date createTime, String codeName, String lotteryType, String issueNum,
			Integer playTypeid, String playType, BigDecimal betting, BigDecimal cancelAmount, BigDecimal winning,
			BigDecimal rebate, BigDecimal platformProfit) {
		this.createTime = createTime;
		this.codeName = codeName;
		this.lotteryType = lotteryType;
		this.issueNum = issueNum;
		this.playTypeid = playTypeid;
		this.playType = playType;
		this.betting = betting;
		this.cancelAmount = cancelAmount;
		this.winning = winning;
		this.rebate = rebate;
		this.platformProfit = platformProfit;
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

	public Integer getPlayTypeid() {
		return this.playTypeid;
	}

	public void setPlayTypeid(Integer playTypeid) {
		this.playTypeid = playTypeid;
	}

	public String getPlayType() {
		return this.playType;
	}

	public void setPlayType(String playType) {
		this.playType = playType;
	}

	public BigDecimal getBetting() {
		return this.betting;
	}

	public void setBetting(BigDecimal betting) {
		this.betting = betting;
	}

	public BigDecimal getCancelAmount() {
		return this.cancelAmount;
	}

	public void setCancelAmount(BigDecimal cancelAmount) {
		this.cancelAmount = cancelAmount;
	}

	public BigDecimal getWinning() {
		return this.winning;
	}

	public void setWinning(BigDecimal winning) {
		this.winning = winning;
	}

	public BigDecimal getRebate() {
		return this.rebate;
	}

	public void setRebate(BigDecimal rebate) {
		this.rebate = rebate;
	}

	public BigDecimal getPlatformProfit() {
		return this.platformProfit;
	}

	public void setPlatformProfit(BigDecimal platformProfit) {
		this.platformProfit = platformProfit;
	}

}
