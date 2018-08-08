package com.jll.entity;
// Generated 2018-8-8 15:26:50 by Hibernate Tools 5.2.10.Final

/**
 * SysCode generated by hbm2java
 */
public class SysCode implements java.io.Serializable {

	private Integer id;
	private Integer codeType;
	private Integer isCodeType;
	private String codeName;
	private String codeVal;
	private Integer seq;
	private Integer state;
	private String remark;

	public SysCode() {
	}

	public SysCode(Integer codeType, Integer isCodeType, String codeName, String codeVal, Integer seq, Integer state,
			String remark) {
		this.codeType = codeType;
		this.isCodeType = isCodeType;
		this.codeName = codeName;
		this.codeVal = codeVal;
		this.seq = seq;
		this.state = state;
		this.remark = remark;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCodeType() {
		return this.codeType;
	}

	public void setCodeType(Integer codeType) {
		this.codeType = codeType;
	}

	public Integer getIsCodeType() {
		return this.isCodeType;
	}

	public void setIsCodeType(Integer isCodeType) {
		this.isCodeType = isCodeType;
	}

	public String getCodeName() {
		return this.codeName;
	}

	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}

	public String getCodeVal() {
		return this.codeVal;
	}

	public void setCodeVal(String codeVal) {
		this.codeVal = codeVal;
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

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
