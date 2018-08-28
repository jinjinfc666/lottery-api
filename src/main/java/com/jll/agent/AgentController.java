package com.jll.agent;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jll.common.constants.Message;
import com.jll.dao.PageQueryDao;
import com.jll.entity.MemberPlReport;
import com.jll.entity.OrderInfo;
import com.jll.entity.UserAccountDetails;
import com.jll.entity.UserInfo;
import com.jll.user.UserInfoService;
import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;


@Api2Doc(id = "AgentSuperior", name = "Agent Superior")
@RestController
@RequestMapping({ "/agent" })
public class AgentController {
	
	@Resource
	UserInfoService userInfoService;
	
	@Resource
	AgentService agentService;
	
	
	@ApiComment(value="Get Agent Lower User Bet Order",seeClass = OrderInfo.class)
	@RequestMapping(value="/lower/bet-order", method = { RequestMethod.POST }, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> getAgentLowerBetOrder(
			 String userName,
			 OrderInfo order,
			 PageQueryDao page) {
		UserInfo superior = userInfoService.getCurLoginInfo();
		if(null == superior){
			Map<String, Object> resp = new HashMap<String, Object>();
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_INVALID_USER_NAME.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_INVALID_USER_NAME.getErrorMes());
			return resp;
		}
		return agentService.getAgentLowerBetOrder(userName,order,superior.getId(),page);
	}
	
	@ApiComment(value="Get Agent Lower User Credit Order",seeClass = UserAccountDetails.class)
	@RequestMapping(value="/lower/credit-order", method = { RequestMethod.POST }, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> getAgentLowerCreditOrder(
			String userName,
			UserAccountDetails order,
			PageQueryDao page) {
		UserInfo superior = userInfoService.getCurLoginInfo();
		if(null == superior){
			Map<String, Object> resp = new HashMap<String, Object>();
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_INVALID_USER_NAME.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_INVALID_USER_NAME.getErrorMes());
			return resp;
		}
		return agentService.getAgentLowerCreditOrder(userName,order,superior.getId(),page);
	}
	
	@ApiComment(value="Get Agent Lower User Profit Report",seeClass = MemberPlReport.class)
	@RequestMapping(value="/lower/profit-report", method = { RequestMethod.POST }, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> getAgentLowerProfitReport(
			 String userName,
			 PageQueryDao page) {
		UserInfo superior = userInfoService.getCurLoginInfo();
		if(null == superior){
			Map<String, Object> resp = new HashMap<String, Object>();
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_INVALID_USER_NAME.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_INVALID_USER_NAME.getErrorMes());
			return resp;
		}
		return agentService.getAgentLowerProfitReport(userName,superior.getId(),page);
	}
	
	
	@ApiComment("User Profit Report")
	@RequestMapping(value="/{userName}/profit-report", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> userProfitReport(
			@PathVariable("userName") String userName,PageQueryDao page) {
		return userInfoService.userProfitReport(userName,page);
	}
}
