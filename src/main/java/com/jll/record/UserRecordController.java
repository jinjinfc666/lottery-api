package com.jll.record;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jll.dao.PageQueryDao;
import com.jll.entity.OrderInfo;
import com.jll.entity.UserAccountDetails;
import com.jll.entity.UserInfo;
import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;

@Api2Doc(id = "UserBetRecord", name = "User Bet Record")
@ApiComment(seeClass = UserInfo.class)
@RestController
@RequestMapping({ "/record" })
public class UserRecordController {
	
	@Resource
	UserRecordService userRecordService;
	
	@ApiComment("Get User Bet Order")
	@RequestMapping(value="/{userId}/bet-order", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> getUserBetOrder(
			@PathVariable("userId") int userId,
			@RequestBody OrderInfo query,
			@RequestBody PageQueryDao page) {
		query.setUserId(userId);
		return userRecordService.getUserBetRecord(query, page);
	}
	
	
	@ApiComment("Get User Bet Types")
	@RequestMapping(value="/betTypes", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> getUserBetTypes() {
		return userRecordService.getUserBetType();
	}
	
	
	@ApiComment("Get User Credit Record")
	@RequestMapping(value="/{userId}/account-flow", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> getUserBetOrder(
			@PathVariable("userId") int userId,
			@RequestBody UserAccountDetails query,
			@RequestBody PageQueryDao page) {
		query.setUserId(userId);
		return userRecordService.getUserCreditRecord(query, page);
	}
	
	
	@ApiComment("Get User Credit Types")
	@RequestMapping(value="/creditTypes", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> getUserCreditTypes(@PathVariable("userName") String userName) {
		return userRecordService.getUserCreditType();
	}

}
