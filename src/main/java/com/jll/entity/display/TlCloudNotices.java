package com.jll.entity.display;

public class TlCloudNotices
{
	private String order_id;
	
	private String verified_by_user_id;
	
	private String Verified_by_username;
	
	private long verified_time;

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public String getVerified_by_user_id() {
		return verified_by_user_id;
	}

	public void setVerified_by_user_id(String verified_by_user_id) {
		this.verified_by_user_id = verified_by_user_id;
	}

	public String getVerified_by_username() {
		return Verified_by_username;
	}

	public void setVerified_by_username(String verified_by_username) {
		Verified_by_username = verified_by_username;
	}

	public long getVerified_time() {
		return verified_time;
	}

	public void setVerified_time(long verified_time) {
		this.verified_time = verified_time;
	}
  
	
}
