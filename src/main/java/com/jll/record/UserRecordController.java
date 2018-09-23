package com.jll.record;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jll.common.constants.Constants.AccOperationType;
import com.jll.common.constants.Constants.SysCodeTypes;
import com.jll.common.utils.Utils;
import com.jll.dao.PageQueryDao;
import com.jll.entity.OrderInfo;
import com.jll.entity.UserAccountDetails;
import com.jll.sys.promo.PromoService;

//@Api2Doc(id = "UserRecord", name = "User Record")
//@ApiComment(seeClass = UserInfo.class)
@RestController
@RequestMapping({ "/record" })
public class UserRecordController {
	
	@Resource
	UserRecordService userRecordService;
	
	@Resource
	PromoService promoService;
	
	/**
	 
	 * @param params  
	 * {
	"pageIndex":1,
	"pageSize":20,
	"startDate":"2017-03-21 11:43:26",
	"endDate":"2017-03-21 11:43:26",
	"isZh":0 //不传查询所有
	}
	 * @return
	 */
	//@ApiComment("Get User Bet Order")
	@RequestMapping(value="/{userId}/bet-order", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> getUserBetOrder(
			@PathVariable("userId") int userId,
			@RequestBody Map<String, String> params) {
		PageQueryDao page = new PageQueryDao(Utils.toDate(params.get("startDate")),Utils.toDate(params.get("endDate")),Utils.toInteger(params.get("pageIndex")),
				Utils.toInteger(params.get("pageSize")));
		OrderInfo query = new OrderInfo();
		query.setUserId(userId);
		query.setIsZh(Utils.toInteger(params.get("isZh")));
		return userRecordService.getUserBetRecord(query, page);
	}
	
	
	//@ApiComment("Get User Bet Types")
	@RequestMapping(value="/betTypes", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> getUserBetTypes() {
		return userRecordService.getUserBetType();
	}
	
	
	/**
	 
	 * @param params  
	 * {
	"pageIndex":1,
	"pageSize":20,
	"startDate":"2017-03-21 11:43:26",
	"endDate":"2017-03-21 11:43:26",
	"orderId":123456 ,//不传查询所有
	"operationType":123456 //不传查询所有
	}
	 * @return
	 */
	//@ApiComment("Get User Credit Record")
	@RequestMapping(value="/{userId}/credit", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> getUserCreditRecord(
			@PathVariable("userId") int userId,
			@RequestBody Map<String, String> params) {
		PageQueryDao page = new PageQueryDao(Utils.toDate(params.get("startDate")),Utils.toDate(params.get("endDate")),Utils.toInteger(params.get("pageIndex")),
				Utils.toInteger(params.get("pageSize")));
		 UserAccountDetails query = new UserAccountDetails();
		query.setUserId(userId);
		query.setOrderId(Utils.toInteger(params.get("orderId")));
		query.setOperationType(Utils.toString(params.get("operationType")));
		return userRecordService.getUserCreditRecord(query, page);
	}
	
	
	//@ApiComment("Get User Sing in day Record")
	@RequestMapping(value="/{userId}/sing-in", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> getUserSingInDay(
			@PathVariable("userId") int userId,
			@RequestBody PageQueryDao page) {
		UserAccountDetails query = new UserAccountDetails();
		query.setUserId(userId);
		query.setOrderId(promoService.getPromoByCode(SysCodeTypes.SIGN_IN_DAY.getCode()).getId());
		query.setOperationType(AccOperationType.DAILY_SIGN_IN.getCode());
		return userRecordService.getUserCreditRecord(query, page);
	}
	
	
	//@ApiComment("Get User Credit Types")
	@RequestMapping(value="/creditTypes", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> getUserCreditTypes(@PathVariable("userName") String userName) {
		return userRecordService.getUserCreditType();
	}

}
