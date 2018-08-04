package com.jll.entity;
// Generated 2018-7-25 12:24:23 by Hibernate Tools 5.2.10.Final

import java.util.Date;

/**
 * TpNotices generated by hbm2java
 */
public class PlayType implements java.io.Serializable {

	private Integer id;
	private String lotteryType;
	private String name;
	private String desc;
	private Integer state;
	private Integer seq;
	private Date createTime;

	public PlayType() {
	}

	public PlayType(String lotteryType, String name, String desc, Integer state,Integer seq, Date createTime) {
		this.lotteryType = lotteryType;
		this.name = name;
		this.desc = desc;
		this.state = state;
		this.seq = seq;
		this.createTime = createTime;
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

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

}
