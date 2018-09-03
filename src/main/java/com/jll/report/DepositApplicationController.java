package com.jll.report;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jll.entity.DepositApplication;
import com.jll.entity.OrderInfo;
import com.jll.entity.UserAccountDetails;
import com.jll.entity.UserInfo;
import com.jll.game.order.OrderService;
import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;

@Api2Doc(id = "DepositInfo", name = "depositInfo")
@ApiComment(seeClass = UserInfo.class)
@RestController
@RequestMapping({ "/deposit" })
public class DepositApplicationController {
	
	private Logger logger = Logger.getLogger(DepositApplicationController.class);
	
	@Resource
	DepositApplicationService depositApplicationService;
	
	@ApiComment(value="Get Deposit Info",seeClass=DepositApplication.class)
   	@RequestMapping(value="/{orderNum}/info", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
   	public Map<String, Object> getDepositInfo(
   			@PathVariable("orderNum") String orderNum) {
   		return depositApplicationService.getDepositInfoByOrderNum(orderNum);
   	}
	
	/**
	 * @param 
	 * orderNum 订单号
	 * amount：实际到账金额
	 * remark:备注
	 * @param dep
	 * @return
	 */
	
	@ApiComment(value="Add Amount By Deposit order",seeClass=OrderInfo.class)
   	@RequestMapping(value="/add-amount", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
   	public Map<String, Object> addAmountByDepositOrder(
   			@RequestBody DepositApplication dep) {
   		return depositApplicationService.addAmountByDepositOrder(dep);
   	}
	
}
