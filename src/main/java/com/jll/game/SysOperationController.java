package com.jll.game;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jll.entity.Issue;
import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;

@Api2Doc(id = "SysOperation", name = "Sys Operation")
@ApiComment(seeClass = Issue.class)
@RestController
@RequestMapping({"/sys/oper"})


public class SysOperationController{
	
	  private Logger logger = Logger.getLogger(SysOperationController.class);
	  
	  @Resource
	  IssueService issueService;
  
	  @ApiComment("updte issue open number")
	  @RequestMapping(value={"/issue/{issueNum}"}, method={org.springframework.web.bind.annotation.RequestMethod.POST}, produces={"application/json"})
	  public Map<String, Object> updateIssueOpenNum(@PathVariable("issueNum") String issueNum,
			  @RequestBody Map<String, String> params){
		  
		  return issueService.updateIssueOpenNum(issueNum,params);
	  }
	  
	  
	  @ApiComment("issue payout")
	  @RequestMapping(value={"/issue/{issueNum}/payout"}, method={org.springframework.web.bind.annotation.RequestMethod.POST}, produces={"application/json"})
	  public Map<String, Object> betOrderPayout(@PathVariable("issueNum") String issueNum){
		  return issueService.betOrderPayout(issueNum);
	  }
	  
	  
	  @ApiComment("cancel current issue all  payout")
	  @RequestMapping(value={"/issue/{issueNum}/cancel-payout"}, method={org.springframework.web.bind.annotation.RequestMethod.POST}, produces={"application/json"})
	  public Map<String, Object> calcelIssuePayout(@PathVariable("issueNum") String issueNum){
		  return issueService.calcelIssuePayout(issueNum);
	  }
	  
	  
	  @ApiComment("issue re payout")
	  @RequestMapping(value={"/issue/{issueNum}/re-payout"}, method={org.springframework.web.bind.annotation.RequestMethod.POST}, produces={"application/json"})
	  public Map<String, Object> betOrderRePayout(@PathVariable("issueNum") String issueNum){
		  return issueService.betOrderRePayout(issueNum);
	  }
	  
	  @ApiComment("issue disbale")
	  @RequestMapping(value={"/issue/{issueNum}/disbale"}, method={org.springframework.web.bind.annotation.RequestMethod.POST}, produces={"application/json"})
	  public Map<String, Object> issueDisbale(@PathVariable("issueNum") String issueNum){
		  return issueService.issueDisbale(issueNum);
	  }
	  
	  @ApiComment("issue delay payout")
	  @RequestMapping(value={"/issue/{issueNum}/delay-payout"}, method={org.springframework.web.bind.annotation.RequestMethod.POST}, produces={"application/json"})
	  public Map<String, Object> issueDelayePayout(@PathVariable("issueNum") String issueNum,
			  @RequestBody Map<String, String> params){
		  return issueService.issueDelayePayout(issueNum,params);
	  }
	  
	  @ApiComment("order manual payout")
	  @RequestMapping(value={"/order/{orderNum}/manual-payout"}, method={org.springframework.web.bind.annotation.RequestMethod.POST}, produces={"application/json"})
	  public Map<String, Object> manualPayoutOrder(@PathVariable("orderNum") String orderNum){
		  return issueService.manualPayoutOrder(orderNum);
	  }
	  
	  @ApiComment("order cancel")
	  @RequestMapping(value={"/order/{orderNum}/cancel"}, method={org.springframework.web.bind.annotation.RequestMethod.POST}, produces={"application/json"})
	  public Map<String, Object> orderCancel(@PathVariable("orderNum") String orderNum){
		  return issueService.orderCancel(orderNum);
	  }
	  
}
