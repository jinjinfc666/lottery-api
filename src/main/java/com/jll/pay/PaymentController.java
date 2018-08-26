package com.jll.pay;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jll.common.constants.Message;
import com.jll.dao.PageQueryDao;
import com.jll.entity.DepositApplication;
import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;

@Api2Doc(id = "PaymentInfo", name = "Payment Info")
@ApiComment(seeClass = DepositApplication.class)
@RestController
@RequestMapping({"/payment"})
public class PaymentController
{
	
  private Logger logger = Logger.getLogger(PaymentController.class);
  
  @Resource
  PaymentService paymentService;
  
	  @ApiComment("Get User Deposit Times")
	  @RequestMapping(value={"/depositTimes/{userId}"}, method={org.springframework.web.bind.annotation.RequestMethod.GET}, produces={"application/json"})
	  public Map<String, Object> depositTimes(@PathVariable("userId") int userId){
		  	Map<String, Object> ret = new HashMap<String, Object>();
		    if (userId < 0){
		    	ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
				ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
				ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
				return ret;
		    }else{
		      long depositTimes = this.paymentService.queryDepositTimes(userId);
		      ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		      ret.put(Message.KEY_DATA,Long.valueOf(depositTimes));
		    }
		    return ret;
	 }
  
	  @ApiComment("Get Pay Channel List")
	  @RequestMapping(value={"/channel-List"}, method={org.springframework.web.bind.annotation.RequestMethod.GET}, produces={"application/json"})
	  public Map<String, Object> getChannelList(){
			Map<String, Object> ret = new HashMap<String, Object>();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		    ret.put(Message.KEY_DATA,paymentService.getUserPayChannel());
		    return ret;
	  }
  
  
	  @ApiComment("Get User Pay Order")
	  @RequestMapping(value={"/{userId}/pay-order"}, method={org.springframework.web.bind.annotation.RequestMethod.GET}, produces={"application/json"})
	  public Map<String, Object> getUserPayOrder(@PathVariable("userId") int userId,
				@RequestBody PageQueryDao page){
		    return paymentService.getUserPayOrder(userId,page);
	  }
	  
	  @ApiComment("User Pay Order To System")
	  @RequestMapping(value={"/{userId}/pay-loading"}, method={org.springframework.web.bind.annotation.RequestMethod.GET}, produces={"application/json"})
	  public Map<String, Object> payOrderToSystem(@PathVariable("userId") int userId,
				@RequestBody DepositApplication info,
				@RequestParam(name="payerName") String payerName,
				@RequestParam(name="payCardNumber") String payCardNumber,
				HttpServletRequest request,
				HttpServletResponse response){
		  Map<String, Object> params = new HashMap<String, Object>();
		  params.put("reqHost", request.getServerName() +":"+ request.getServerPort());
		  params.put("reqContext", request.getServletContext().getContextPath());
		  params.put("reqIP", request.getRemoteAddr());
		  params.put("payerName",payerName);
		  params.put("payCardNumber",payCardNumber);
		  
		  Map<String,Object> ret = paymentService.payOrderToSystem(userId,info,params);
		  if(null != ret.get("isRedirect")){
			  try {
				response.sendRedirect(ret.get("isRedirect").toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		  }
		  return ret;
	  }
}