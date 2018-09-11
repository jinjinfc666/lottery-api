package com.jll.user.wallet;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jll.common.constants.Constants;
import com.jll.common.constants.Message;
import com.jll.entity.UserInfo;
import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;

@Api2Doc(id = "wallet", name = "用户账户信息")
@ApiComment(seeClass = UserInfo.class)
@RestController
@RequestMapping({ "/wallet" })
public class WalletController {
	private Logger logger = Logger.getLogger(WalletController.class);

	@Resource
	WalletService walletService;
	//通过用户名(false)或时间去查询(true)
	@RequestMapping(value={"/queryUserAccount"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> queryUserAccount(@RequestParam(name = "userName", required = false) String userName,
			@RequestParam(name = "startTime", required = true) String startTime,
			  @RequestParam(name = "endTime", required = true) String endTime,
			  @RequestParam(name = "pageIndex", required = true) Integer pageIndex,//当前请求页
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		Integer pageSize=Constants.Pagination.SUM_NUMBER.getCode();
		ret.put("pageSize", pageSize);
		ret.put("pageIndex", pageIndex);
		ret.put("userName", userName);
		ret.put("startTime", startTime);
		ret.put("endTime", endTime);
		try {
			Map<String,Object> map=walletService.queryUserAccount(ret);
			map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			return map;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
	//锁定用户资金
	@RequestMapping(value={"/updateUserAccountState"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updateUserAccountState(@RequestParam(name = "userId", required = true) Integer userId,
			@RequestParam(name = "state", required = true) Integer state,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		ret.put("userId", userId);
		ret.put("state", state);
		try {
			Map<String,Object> map=walletService.updateState(userId, state);
			return map;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
	//通过id
	@RequestMapping(value={"/queryByIdUserAccount"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> queryByIdUserAccount(@RequestParam(name = "id", required = false) Integer id,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		ret.put("id", id);
		try {
			Map<String,Object> map=walletService.queryByIdUserAccount(ret);
			map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			return map;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
	//查询所有的类型
	@RequestMapping(value={"/queryOperationType"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> queryOperationType() {
		Map<String, Object> ret = new HashMap<>();
		try {
			Map<String,String> map=Constants.UserAccountOperationType.getMap();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", map);
			return ret;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
	//通过用户ID查询当前用户的钱包
	@RequestMapping(value={"/queryByUserIdUserAccount"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> queryByUserIdUserAccount(@RequestParam(name = "userId", required = true) Integer userId,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		ret.put("userId", userId);
		try {
			Map<String,Object> map=walletService.queryByUserIdUserAccount(ret);
			map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			return map;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
}
