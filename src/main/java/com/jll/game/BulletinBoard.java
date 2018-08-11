package com.jll.game;

import java.io.Serializable;

import com.jll.entity.Issue;

public class BulletinBoard implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Issue lastIssue;
	
	private Issue currIssue;

	public Issue getLastIssue() {
		return lastIssue;
	}

	public void setLastIssue(Issue lastIssue) {
		this.lastIssue = lastIssue;
	}

	public Issue getCurrIssue() {
		return currIssue;
	}

	public void setCurrIssue(Issue currIssue) {
		if(currIssue == null 
				|| (this.currIssue != null && (this.currIssue.getId() == currIssue.getId()))) {
			return ;
		}
		
		this.lastIssue = this.currIssue;
		this.currIssue = currIssue;
	}
	
	
}

