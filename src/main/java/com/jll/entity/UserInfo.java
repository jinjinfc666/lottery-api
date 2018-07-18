package com.jll.entity;
// Generated 2018-7-18 10:09:13 by Hibernate Tools 5.2.10.Final

import java.math.BigDecimal;
import java.util.Date;

/**
 * UserInfo generated by hbm2java
 */
public class UserInfo implements java.io.Serializable {

	private Integer id;
	private String realName;
	private String userName;
	private String loginPwd;
	private String fundPwd;
	private Integer state;
	private Integer level;
	private Integer loginCount;
	private Date unlockTime;
	private Integer userType;
	private String superior;
	private BigDecimal rebate;
	private String phoneNum;
	private String qq;
	private String wechat;
	private String email;
	private Integer isValidPhone;
	private Integer isValidEmail;
	private String regIp;
	private Date createTime;
	private Integer creator;

	public UserInfo() {
	}

	public UserInfo(String realName, String userName, String loginPwd, String fundPwd, Integer state, Integer level,
			Integer loginCount, Date unlockTime, Integer userType, String superior, BigDecimal rebate, String phoneNum,
			String qq, String wechat, String email, Integer isValidPhone, Integer isValidEmail, String regIp,
			Date createTime, Integer creator) {
		this.realName = realName;
		this.userName = userName;
		this.loginPwd = loginPwd;
		this.fundPwd = fundPwd;
		this.state = state;
		this.level = level;
		this.loginCount = loginCount;
		this.unlockTime = unlockTime;
		this.userType = userType;
		this.superior = superior;
		this.rebate = rebate;
		this.phoneNum = phoneNum;
		this.qq = qq;
		this.wechat = wechat;
		this.email = email;
		this.isValidPhone = isValidPhone;
		this.isValidEmail = isValidEmail;
		this.regIp = regIp;
		this.createTime = createTime;
		this.creator = creator;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getRealName() {
		return this.realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getLoginPwd() {
		return this.loginPwd;
	}

	public void setLoginPwd(String loginPwd) {
		this.loginPwd = loginPwd;
	}

	public String getFundPwd() {
		return this.fundPwd;
	}

	public void setFundPwd(String fundPwd) {
		this.fundPwd = fundPwd;
	}

	public Integer getState() {
		return this.state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getLevel() {
		return this.level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getLoginCount() {
		return this.loginCount;
	}

	public void setLoginCount(Integer loginCount) {
		this.loginCount = loginCount;
	}

	public Date getUnlockTime() {
		return this.unlockTime;
	}

	public void setUnlockTime(Date unlockTime) {
		this.unlockTime = unlockTime;
	}

	public Integer getUserType() {
		return this.userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	public String getSuperior() {
		return this.superior;
	}

	public void setSuperior(String superior) {
		this.superior = superior;
	}

	public BigDecimal getRebate() {
		return this.rebate;
	}

	public void setRebate(BigDecimal rebate) {
		this.rebate = rebate;
	}

	public String getPhoneNum() {
		return this.phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getQq() {
		return this.qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getWechat() {
		return this.wechat;
	}

	public void setWechat(String wechat) {
		this.wechat = wechat;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getIsValidPhone() {
		return this.isValidPhone;
	}

	public void setIsValidPhone(Integer isValidPhone) {
		this.isValidPhone = isValidPhone;
	}

	public Integer getIsValidEmail() {
		return this.isValidEmail;
	}

	public void setIsValidEmail(Integer isValidEmail) {
		this.isValidEmail = isValidEmail;
	}

	public String getRegIp() {
		return this.regIp;
	}

	public void setRegIp(String regIp) {
		this.regIp = regIp;
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
