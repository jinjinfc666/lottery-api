package com.jll.game;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jll.common.constants.Constants;
import com.jll.common.constants.Message;
import com.jll.entity.UserInfo;
import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;

@Api2Doc(id = "IssueController", name = "Issue Controller")
@ApiComment(seeClass = UserInfo.class)
@RestController
@RequestMapping({ "/issues" })
public class IssueController {
	
	private Logger logger = Logger.getLogger(IssueController.class); 
	
	@Resource
	IssueService issueServ;
	//近期注单   前端只传过来彩种，后台默认只查询30条记录给前端
	@RequestMapping(value="/getIssues", method = { RequestMethod.GET },  produces={"application/json"})
	public Map<String, Object> queryRecentBet(@RequestParam(name = "lotteryType", required = true) String lotteryType,
			  @RequestParam(name = "state", required = false) Integer state,
			  @RequestParam(name = "issueNum", required = false) String issueNum,
			  @RequestParam(name = "startTime", required = true) String startTime,
			  @RequestParam(name = "endTime", required = true) String endTime,
			  @RequestParam(name = "pageIndex", required = true) Integer pageIndex,//当前请求页
			  HttpServletRequest request){
		Integer pageSize=Constants.Pagination.SUM_NUMBER.getCode();
		Map<String,Object> ret=new HashMap<String,Object>();
		try {
			ret.clear();
			ret=issueServ.queryAllByIssue(lotteryType, state,startTime, endTime, pageIndex, pageSize, issueNum);
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//获取期次状态
	@RequestMapping(value="/getIssuesState", method = { RequestMethod.GET },  produces={"application/json"})
	public Map<String, Object> getIssueState(){
		Map<String,Object> ret=new HashMap<String,Object>();
		try {
			ret.clear();
			Map<Integer,String> map=Constants.IssueState.getMap();
			ret.put("data", map);
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//追号需要的期号信息
	@RequestMapping(value="/IsZhIssues", method = { RequestMethod.GET },  produces={"application/json"})
	public Map<String, Object> getIsZhIssues(@RequestParam(name = "lotteryType", required = true) String lotteryType,
			  HttpServletRequest request){
		Map<String,Object> ret=new HashMap<String,Object>();
		try {
			ret.clear();
			ret=issueServ.queryIsZhIssue(lotteryType);
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
}
