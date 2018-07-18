package com.jll.entity;
// Generated 2018-7-18 10:09:13 by Hibernate Tools 5.2.10.Final

import java.util.Date;

/**
 * SiteMessFeedback generated by hbm2java
 */
public class SiteMessFeedback implements java.io.Serializable {

	private Integer id;
	private Integer fbUserId;
	private Integer mesId;
	private String content;
	private Date fbTime;
	private Integer isRead;

	public SiteMessFeedback() {
	}

	public SiteMessFeedback(Integer fbUserId, Integer mesId, String content, Date fbTime, Integer isRead) {
		this.fbUserId = fbUserId;
		this.mesId = mesId;
		this.content = content;
		this.fbTime = fbTime;
		this.isRead = isRead;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getFbUserId() {
		return this.fbUserId;
	}

	public void setFbUserId(Integer fbUserId) {
		this.fbUserId = fbUserId;
	}

	public Integer getMesId() {
		return this.mesId;
	}

	public void setMesId(Integer mesId) {
		this.mesId = mesId;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getFbTime() {
		return this.fbTime;
	}

	public void setFbTime(Date fbTime) {
		this.fbTime = fbTime;
	}

	public Integer getIsRead() {
		return this.isRead;
	}

	public void setIsRead(Integer isRead) {
		this.isRead = isRead;
	}

}
