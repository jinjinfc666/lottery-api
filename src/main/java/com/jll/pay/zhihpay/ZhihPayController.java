package com.jll.pay.zhihpay;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jll.entity.display.ZhihPayNotices;
import com.jll.pay.order.DepositOrderService;


@RestController
@RequestMapping({"/zhihPay"})
public class ZhihPayController
{
	private Logger logger = Logger.getLogger(ZhihPayController.class);
  
	@Resource
	ZhihPayService zhihPayService;
	
	@Resource
	DepositOrderService depositOrderService;
  
	private final String KEY_RESPONSE_STATUS = "success";
	
	@PostConstruct
	public void init() {
	  
	}
  
  /**
   * payment platform will call this method to inform that the deposit record already in database.
   * next step , this method have to change the status of deposit order, and change the balance of user
 * @param userName
 * @return        {"success": true}
 */
  @RequestMapping(value={"/notices/{noticeType}"}, method={RequestMethod.POST}, consumes={MediaType.APPLICATION_FORM_URLENCODED_VALUE}, produces={MediaType.TEXT_PLAIN_VALUE})
  public String notices(@PathVariable("noticeType") int noticeType ,
		  ZhihPayNotices notices,
		  HttpServletRequest request){
  
    
    if(!zhihPayService.isNoticesValid(notices)) {
    	return "FAIL";
    }
    
    boolean isNotified = depositOrderService.isOrderNotified(notices.getOrder_no());
    if(isNotified) {
    	return "SUCCESS";
    }
    depositOrderService.receiveDepositOrder(notices.getOrder_no());
    return "SUCCESS";
  }
}
