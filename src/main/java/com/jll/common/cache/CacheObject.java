package com.jll.common.cache;

import java.io.Serializable;

/**  
 * AbstractBaseRedisDao 
 * 
 * 
 */   
public class CacheObject<V> implements Serializable{  
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected String key;

	protected V content;
	
	protected Integer expired;
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public V getContent() {
		return content;
	}

	public void setContent(V content) {
		this.content = content;
	}

	public Integer getExpired() {
		return expired;
	}

	public void setExpired(Integer expired) {
		this.expired = expired;
	}
      
    
}  