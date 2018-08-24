package com.jll.sys.deposit;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Constants;
import com.jll.common.constants.Message;
import com.jll.entity.IpBlackList;
import com.jll.entity.PayChannel;
import com.jll.entity.PayType;
import com.jll.entity.PlayType;
import com.jll.entity.SysCode;
import com.jll.entity.UserAccountDetails;
import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;

@Api2Doc(id = "PayType", name = "pay types")
@ApiComment(seeClass = UserAccountDetails.class)
@RestController
@RequestMapping({"/pay-types"})
public class DepositController {
	private Logger logger = Logger.getLogger(DepositController.class);
	@Resource
	CacheRedisService cacheServ;
	@Resource
	PayTypeService payTypeService;
	@Resource
	PayChannelService payChannelService;
	/**
	 * 充值平台
	 * */
	//选择支付平台需要的数据
	@RequestMapping(value={"/queryPayTypeName"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> queryPayTypeName() {
		Map<String, Object> ret = new HashMap<>();
		String payTypeName=Constants.SysCodeTypes.PAYMENT_PLATFORM.getCode();
		try {
			ret.clear();
			Map<String,SysCode> map=cacheServ.getSysCode(payTypeName);
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", map);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//选择有效和无效
	@RequestMapping(value={"/queryIsOrNo"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> queryIsOrNo() {
		Map<String, Object> ret = new HashMap<>();
		try {
			ret.clear();
			Map<Integer,String> map=Constants.BankCardState.getIsOrNo();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", map);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//选择是否是第三方
	@RequestMapping(value={"/queryCaptchaIs"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> queryCaptchaIs() {
		Map<String, Object> ret = new HashMap<>();
		try {
			ret.clear();
			Map<Integer,String> map=Constants.PayTypeIs.getMap();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", map);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//添加
	@RequestMapping(value={"/addPayTypeName"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> addPayTypeName(@RequestParam(name = "name", required = true) String name,
			  @RequestParam(name = "nickName", required = true) String nickName,
			  @RequestParam(name = "state", required = true) Integer state,
			  @RequestParam(name = "isTp", required = true) Integer isTp,
			  @RequestParam(name = "codeName", required = true) String codeName,//就是platId也是 SysCode的codeName
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		ret.put("name", name);
		ret.put("nickName", nickName);
		ret.put("state", state);
		ret.put("isTp", isTp);
		ret.put("platId", codeName);
		try {
			Map<String,Object> map=payTypeService.addPayType(ret);
			int status=(int) map.get(Message.KEY_STATUS);	
			if(status==Message.status.SUCCESS.getCode()) {
				String payTypeName=Constants.PayTypeName.PAY_TYPE_CLASS.getCode();
				List<PayType> payTypeLists=cacheServ.getPayType(payTypeName);
				PayType payType=payTypeService.queryBy(name, nickName, codeName);
				if(payType!=null) {
					payTypeLists.add(payType);
					cacheServ.setPayType(payTypeName, payTypeLists);
				}
			}
			return map;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//修改
	@RequestMapping(value={"/updatePayType"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updatePayType(@RequestBody PayType payType) {
		Map<String, Object> ret = new HashMap<>();
		if(StringUtils.isBlank(payType.getName())
				&&StringUtils.isBlank(payType.getNickName())
				&&StringUtils.isBlank(payType.getPlatId())
				&&payType.getIsTp()==null||payType.getState()==null) 
		{
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
		if(payType.getId()==null){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
		try {
			Map<String,Object> map=payTypeService.updatePayType(payType);
			return map;
		}catch(Exception e){
			ret.clear();
			e.printStackTrace();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
	//查询所有 
	@RequestMapping(value={"/queryAllPayType"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> queryAllPayType() {
		Map<String, Object> ret = new HashMap<>();
		try {
			List<PayType> map=payTypeService.queryAllPayType();
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", map);
			return ret;
		}catch(Exception e){
			ret.clear();
			e.printStackTrace();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
	//修改排序
	@RequestMapping(value={"/updatePayTypeSeq"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updatePayTypeSeq(@RequestParam(name = "allId", required = true) String allId,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		try {
			Map<String,Object> map=payTypeService.updatePayTypeState(allId);
			return map;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	/**
	 * 充值渠道
	 * */
	//选择充值方式分类需要的数据
	@RequestMapping(value={"/queryTypeClass"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> queryTypeClass() {
		Map<String, Object> ret = new HashMap<>();
		String payTypeName=Constants.SysCodeTypes.PAY_TYPE.getCode();
		try {
			ret.clear();
			Map<String,SysCode> map=cacheServ.getSysCode(payTypeName);
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", map);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//选择充值方式需要的数据
	@RequestMapping(value={"/queryPayTypeId"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> queryPayTypeId() {
		Map<String, Object> ret = new HashMap<>();
		String payTypeName=Constants.PayTypeName.PAY_TYPE_CLASS.getCode();
		try {
			ret.clear();
			List<PayType> map=cacheServ.getPayType(payTypeName);
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", map);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//选择有效和无效
	@RequestMapping(value={"/queryPayChannelIsOrNo"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> queryPayChannelIsOrNo() {
		Map<String, Object> ret = new HashMap<>();
		try {
			ret.clear();
			Map<Integer,String> map=Constants.BankCardState.getIsOrNo();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", map);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//选择是否激活
	@RequestMapping(value={"/queryEMA"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> queryEMA() {
		Map<String, Object> ret = new HashMap<>();
		try {
			ret.clear();
			Map<Integer,String> map=Constants.PayChannnelEMA.getMap();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", map);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//添加
	@RequestMapping(value={"/addPayChannel"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> addPayChannel(@RequestBody PayChannel payChannel) {
		Map<String, Object> ret = new HashMap<>();
		try {
			Map<String,Object> map=payChannelService.addPayChannel(payChannel);
			int status=(int) map.get(Message.KEY_STATUS);	
			if(status==Message.status.SUCCESS.getCode()) {
				String payChannelName=Constants.PayChannel.PAY_CHANNEL.getCode();
				PayChannel payChannelCache=payChannelService.queryLast();
				cacheServ.setPayChannel(payChannelName, payChannelCache);
			}
			return map;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//修改
	@RequestMapping(value={"/updatePayChannel"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updatePayChannel(@RequestBody PayChannel payChannel) {
		Map<String, Object> ret = new HashMap<>();
		try {
			Map<String,Object> map=payChannelService.updatePayChannel(payChannel);
			return map;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//查询所有
	@RequestMapping(value={"/queryPayChannel"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> queryPayChannel() {
		Map<String, Object> ret = new HashMap<>();
		try {
			List<PayChannel> map=payChannelService.queryAll();
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", map);
			return ret;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//修改排序
	@RequestMapping(value={"/updatePayChannelSeq"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updatePayChannelSeq(@RequestParam(name = "allId", required = true) String allId,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		try {
			Map<String,Object> map=payChannelService.updatePayChannelSeq(allId);
			return map;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
}
