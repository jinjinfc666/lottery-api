package com.jll.agent;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jll.common.constants.Message;
import com.jll.common.utils.Utils;
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
	
	
	/**
	 
	 * @param params  
	 * {
	"pageIndex":1,
	"pageSize":20,
	"startDate":"2017-03-21 11:43:26",
	"endDate":"2017-03-21 11:43:26",
	"userName":"test001",//不传查询所有
	"issueId":0,//不传查询所有
	"isZh":0//不传查询所有
	}
	 * @return
	 */
	@ApiComment(value="Get Agent Lower User Bet Order",seeClass = OrderInfo.class)
	@RequestMapping(value="/lower/bet-order", method = { RequestMethod.POST }, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> getAgentLowerBetOrder(
			@RequestBody Map<String, String> params) {
		UserInfo superior = userInfoService.getCurLoginInfo();
		if(null == superior){
			Map<String, Object> resp = new HashMap<String, Object>();
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_INVALID_USER_NAME.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_INVALID_USER_NAME.getErrorMes());
			return resp;
		}
		OrderInfo order = new OrderInfo();
		order.setIssueId(Utils.toInteger(params.get("issueId")));
		order.setIsZh(Utils.toInteger(params.get("isZh")));
		String userName = Utils.toString(params.get("userName"));
		PageQueryDao page = new PageQueryDao(Utils.toDate(params.get("startDate")),Utils.toDate(params.get("endDate")),Utils.toInteger(params.get("pageIndex")),
				Utils.toInteger(params.get("pageSize")));
		
		return agentService.getAgentLowerBetOrder(userName,order,superior,page);
	}
	

	/**
	 
	 * @param params  
	 * {
	"pageIndex":1,
	"pageSize":20,
	"startDate":"2017-03-21 11:43:26",
	"endDate":"2017-03-21 11:43:26",
	"userName":"test001",//不传查询所有
	"operationType":"betting" //不传查询所有
	}
	 * @return
	 */
	@ApiComment(value="Get Agent Lower User Credit Order",seeClass = UserAccountDetails.class)
	@RequestMapping(value="/lower/credit-order", method = { RequestMethod.POST }, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> getAgentLowerCreditOrder(@RequestBody Map<String, String> params) {
		UserInfo superior = userInfoService.getCurLoginInfo();
		if(null == superior){
			Map<String, Object> resp = new HashMap<String, Object>();
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_INVALID_USER_NAME.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_INVALID_USER_NAME.getErrorMes());
			return resp;
		}
		
		UserAccountDetails order = new UserAccountDetails();
		order.setOperationType(Utils.toString(params.get("operationType")));
		String userName = Utils.toString(params.get("userName"));
		PageQueryDao page = new PageQueryDao(Utils.toDate(params.get("startDate")),Utils.toDate(params.get("endDate")),Utils.toInteger(params.get("pageIndex")),
				Utils.toInteger(params.get("pageSize")));
		return agentService.getAgentLowerCreditOrder(userName,order,superior,page);
	}
	
	
	/**
	 
	 * @param params  
	 * {
	"pageIndex":1,
	"pageSize":20,
	"startDate":"2017-03-21 11:43:26",
	"endDate":"2017-03-21 11:43:26",
	"userName":"test001" //不传查询所有
	}
	 * @return
	 */
	@ApiComment(value="Get Agent Lower User Profit Report",seeClass = MemberPlReport.class)
	@RequestMapping(value="/lower/profit-report", method = { RequestMethod.POST }, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> getAgentLowerProfitReport(@RequestBody Map<String, String> params) {
		UserInfo superior = userInfoService.getCurLoginInfo();
		if(null == superior){
			Map<String, Object> resp = new HashMap<String, Object>();
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_INVALID_USER_NAME.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_INVALID_USER_NAME.getErrorMes());
			return resp;
		}
		String userName = Utils.toString(params.get("userName"));
		PageQueryDao page = new PageQueryDao(Utils.toDate(params.get("startDate")),Utils.toDate(params.get("endDate")),Utils.toInteger(params.get("pageIndex")),
				Utils.toInteger(params.get("pageSize")));
		return agentService.getAgentLowerProfitReport(userName,superior,page);
	}

}
