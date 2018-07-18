package com.jll.entity;
// Generated 2018-7-18 10:09:13 by Hibernate Tools 5.2.10.Final

/**
 * PayType generated by hbm2java
 */
public class PayType implements java.io.Serializable {

	private Integer id;
	private String name;
	private String nickName;
	private String typeClass;
	private Integer seq;
	private Integer state;
	private Integer isTp;
	private String platId;

	public PayType() {
	}

	public PayType(String name, String nickName, String typeClass, Integer seq, Integer state, Integer isTp,
			String platId) {
		this.name = name;
		this.nickName = nickName;
		this.typeClass = typeClass;
		this.seq = seq;
		this.state = state;
		this.isTp = isTp;
		this.platId = platId;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNickName() {
		return this.nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getTypeClass() {
		return this.typeClass;
	}

	public void setTypeClass(String typeClass) {
		this.typeClass = typeClass;
	}

	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Integer getState() {
		return this.state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getIsTp() {
		return this.isTp;
	}

	public void setIsTp(Integer isTp) {
		this.isTp = isTp;
	}

	public String getPlatId() {
		return this.platId;
	}

	public void setPlatId(String platId) {
		this.platId = platId;
	}

}
