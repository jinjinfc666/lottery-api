package com.jll.entity;
// Generated 2018-7-18 10:09:13 by Hibernate Tools 5.2.10.Final

import java.util.Date;

/**
 * SysNotification generated by hbm2java
 */
public class SysNotification implements java.io.Serializable {

	private Integer id;
	private String title;
	private String content;
	private Date expireTime;
	private Integer receiverType;
	private Integer receiver;
	private Date createTime;
	private Integer creator;

	public SysNotification() {
	}

	public SysNotification(String title, String content, Date expireTime, Integer receiverType, Integer receiver,
			Date createTime, Integer creator) {
		this.title = title;
		this.content = content;
		this.expireTime = expireTime;
		this.receiverType = receiverType;
		this.receiver = receiver;
		this.createTime = createTime;
		this.creator = creator;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getExpireTime() {
		return this.expireTime;
	}

	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}

	public Integer getReceiverType() {
		return this.receiverType;
	}

	public void setReceiverType(Integer receiverType) {
		this.receiverType = receiverType;
	}

	public Integer getReceiver() {
		return this.receiver;
	}

	public void setReceiver(Integer receiver) {
		this.receiver = receiver;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getCreator() {
		return this.creator;
	}

	public void setCreator(Integer creator) {
		this.creator = creator;
	}

}
