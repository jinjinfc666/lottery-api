package com.jll.pay.tlCloud;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.jll.common.constants.Message;
import com.jll.entity.DepositApplication;
import com.jll.entity.display.TlCloudNotices;
import com.jll.pay.order.DepositOrderService;


@RestController
@RequestMapping({"/tlCloud"})
public class TlCouldController
{
	private Logger logger = Logger.getLogger(TlCouldController.class);

	@Resource
	TlCloudService tlCloudService;

	@Resource
	DepositOrderService depositOrderService;

	private final String KEY_RESPONSE_STATUS = "success";

	@PostConstruct
	public void init() {

	}

	@RequestMapping(value={"/orders/{orderId}"}, method={RequestMethod.DELETE}, produces={"application/json"})
	public Map<String, Object> cancelOrder(@PathVariable("orderId") String orderId)
	{
		Map<String, Object> ret = new HashMap<>();
		if(StringUtils.isEmpty(orderId)){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return ret;
		}
		DepositApplication depositOrder = depositOrderService.queryDepositOrderById(orderId);
		if(depositOrder == null) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_PAYMENT_DEPOSIT_ERROR_ORDER.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_PAYMENT_DEPOSIT_ERROR_ORDER.getErrorMes());
			return ret;
		}
		String retCode = tlCloudService.cancelOrder(depositOrder.getOrderNum());
		if(retCode.equals(String.valueOf(Message.status.SUCCESS.getCode()))) {
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		}else {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, retCode);
			ret.put(Message.KEY_ERROR_MES, Message.Error.getErrorByCode(retCode).getErrorMes());
		}

		return ret;
	}

	/**
	 * payment platform will call this method to inform that the deposit record already in database.
	 * next step , this method have to change the status of deposit order, and change the balance of user
	 * @param userName
	 * @return        {"success": true}
	 */
	@RequestMapping(value={"/notices"}, method={RequestMethod.POST}, consumes={"application/json"}, produces={"application/json"})
	public Map<String, Object> notices(@RequestBody TlCloudNotices notices,
			HttpServletRequest request)
	{
		Map<String, Object> ret = new HashMap<>();

		if(notices == null 
				|| notices.getOrder_id() == null 
				|| notices.getOrder_id().length() == 0) {    	
			ret.put(KEY_RESPONSE_STATUS, false);
			return ret;
		}

		boolean isNotified = depositOrderService.isOrderNotified(notices.getOrder_id());
		if(isNotified) {
			ret.put(KEY_RESPONSE_STATUS, false);
			return ret;
		}
		depositOrderService.receiveDepositOrder(notices.getOrder_id(),"");
		ret.put(KEY_RESPONSE_STATUS, true);
		return ret;
	}
}
