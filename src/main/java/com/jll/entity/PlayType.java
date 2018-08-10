package com.jll.entity;
// Generated 2018-8-8 15:26:50 by Hibernate Tools 5.2.10.Final

import java.util.Date;

/**
 * PlayType generated by hbm2java
 */
public class PlayType implements java.io.Serializable {

	private Integer id;
	private String lotteryType;
	private String classification;
	private String name;
	private String desc;
	private Integer state;
	private Integer mulSinFlag;
	private Integer isHidden;
	private Integer seq;
	private Date createTime;

	public PlayType() {
	}

	public PlayType(String lotteryType, String classification,String name, String desc, Integer state, Integer mulSinFlag ,Integer isHidden,Integer seq, Date createTime) {
		this.lotteryType = lotteryType;
		this.classification=classification;
		this.name = name;
		this.desc = desc;
		this.state = state;
		this.mulSinFlag = mulSinFlag;
		this.isHidden = isHidden;
		this.seq = seq;
		this.createTime = createTime;
	}
	
	public Integer getIsHidden() {
		return isHidden;
	}

	public void setIsHidden(Integer isHidden) {
		this.isHidden = isHidden;
	}

	public Integer getMulSinFlag() {
		return mulSinFlag;
	}

	public void setMulSinFlag(Integer mulSinFlag) {
		this.mulSinFlag = mulSinFlag;
	}

	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
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

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return this.desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Integer getState() {
		return this.state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}
