package com.jll.sys.deposit;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Constants;
import com.jll.common.constants.Message;
import com.jll.entity.PayChannel;
import com.jll.entity.PayType;
import com.jll.entity.SysCode;
import com.jll.entity.UserAccountDetails;
import com.jll.sysSettings.syscode.SysCodeService;
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
	@Resource
	FTPService fTPService;
	@Resource
	SysCodeService sysCodeService;
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
		if(StringUtils.isBlank(payType.getName())&&StringUtils.isBlank(payType.getNickName())&&StringUtils.isBlank(payType.getTypeClass())) 
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
			String payTypeName=Constants.SysCodeTypes.PAYMENT_PLATFORM.getCode();
			Integer bigCodeNameId=sysCodeService.queryByCodeName(payTypeName);
			List<?> lists=payTypeService.queryAllPayType(bigCodeNameId);
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", lists);
			return ret;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
	//通过id查询
	@RequestMapping(value={"/queryPayTypeById"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> queryPayTypeById(@RequestParam(name = "id", required = true) Integer id,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		try {
			String payTypeName=Constants.SysCodeTypes.PAYMENT_PLATFORM.getCode();
			Integer bigCodeNameId=sysCodeService.queryByCodeName(payTypeName);
			List<?> lists=payTypeService.queryPayTypeById(id, bigCodeNameId);
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", lists);
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
	//修改状态
	@RequestMapping(value={"/updatePayTypeState"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updatePayTypeState(@RequestParam(name = "id", required = true) Integer id,
			  @RequestParam(name = "state", required = true) Integer state,//1为有效0为无效
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		PayType payType=new PayType();
		try {
			payType.setId(id);
			payType.setState(state);
			Map<String,Object> map=payTypeService.updatePayType(payType);
			return map;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//修改是否是第三方
	@RequestMapping(value={"/updatePayTypeIsTp"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updatePayTypeIsTp(@RequestParam(name = "id", required = true) Integer id,
			  @RequestParam(name = "isTp", required = true) Integer isTp,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		PayType payType=new PayType();
		try {
			payType.setId(id);
			payType.setIsTp(isTp);
			Map<String,Object> map=payTypeService.updatePayType(payType);
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
	//选择showType
	@RequestMapping(value={"/queryShowType"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> queryShowType() {
		Map<String, Object> ret = new HashMap<>();
		try {
			ret.clear();
			Map<Integer,String> map=Constants.PayChannelShowType.getMap();
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
//	//添加充值渠道需要的pay_code
//	@RequestMapping(value={"/queryPayCode"}, method={RequestMethod.GET}, produces={"application/json"})
//	public Map<String, Object> queryPayCode(@RequestParam(name = "platId", required = true) String platId,
//			  HttpServletRequest request) {
//		Map<String, Object> ret = new HashMap<>();
//		try {
//			ret.clear();
//			if(platId.equals(Constants.PayType.WISDOM_PAYMENT.getCode())) {
//				ret.put(Message.KEY_DATA, Constants.WisdomPayment.getMap());
//			}else if(platId.equals(Constants.PayType.CAI_PAY.getCode())) {
//				ret.put(Message.KEY_DATA, Constants.CaiPay.getMap());
//			}else if(platId.equals(Constants.PayType.TLY_PAY.getCode())){
//				Map<String,SysCode> map=cacheServ.getSysCode(Constants.SysCodeTypes.BANK_CODE_LIST.getCode());
//				Map<String,String> mapNew=new HashMap<String,String>();
//				Set<String> keys = map.keySet();   //此行可省略，直接将map.keySet()写在for-each循环的条件中
//				for(String key:keys){
//					mapNew.put(map.get(key).getCodeName(), map.get(key).getRemark());
//				}
//				ret.put(Message.KEY_DATA, mapNew);
//			}
//			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
//		}catch(Exception e){
//			ret.clear();
//			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
//			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
//			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
//		}
//		return ret;
//	}
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
//	//修改
//	@RequestMapping(value={"/updatePayChannel"}, method={RequestMethod.PUT}, produces={"application/json"})
//	public Map<String, Object> updatePayChannel(@RequestBody PayChannel payChannel) {
//		Map<String, Object> ret = new HashMap<>();
//		try {
//			Map<String,Object> map=payChannelService.updatePayChannel(payChannel);
//			return map;
//		}catch(Exception e){
//			ret.clear();
//			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
//			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
//			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
//		}
//		return ret;
//	}
	//查询所有
	@RequestMapping(value={"/queryPayChannel"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> queryPayChannel() {
		Map<String, Object> ret = new HashMap<>();
		try {
			List<PayChannel> map=payChannelService.queryAll();
			Map<Integer,PayChannel> mapPayChannel=new HashMap<Integer, PayChannel>();
			for(int i=0;i<map.size();i++) {
				PayChannel payChannel=map.get(i);
				if(payChannel.getSeq()!=null) {
					mapPayChannel.put(payChannel.getSeq(), payChannel);
				}
			}
			TreeMap treemap = new TreeMap(mapPayChannel);
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", treemap);
			return ret;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//通过充值方式Id查询这个充值方式下的所有充值渠道
	@RequestMapping(value={"/ByPayTypeIdPayChannel"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> queryByPayTypeIdPayChannel(@RequestParam(name = "payTypeId", required = true) Integer payTypeId,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		try {
			List<PayChannel> map=payChannelService.queryByPayTypeIdPayChannel(payTypeId);
			Map<Integer,PayChannel> mapPayChannel=new HashMap<Integer, PayChannel>();
			for(int i=0;i<map.size();i++) {
				PayChannel payChannel=map.get(i);
				if(payChannel.getSeq()!=null) {
					mapPayChannel.put(payChannel.getSeq(), payChannel);
				}
			}
			TreeMap treemap = new TreeMap(mapPayChannel);
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", treemap);
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
	//修改状态
	@RequestMapping(value={"/updatePayChannelState"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updatePayChannelState(@RequestParam(name = "id", required = true) Integer id,
			  @RequestParam(name = "state", required = true) Integer state,//1为有效0为无效
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		try {
			Map<String,Object> map=payChannelService.updatePayChannelState(id,state);
			return map;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//修改是否激活最大限制
	@RequestMapping(value={"/updatePayChannelEnable"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updatePayChannelEnable(@RequestParam(name = "id", required = true) Integer id,
			  @RequestParam(name = "enableMaxAmount", required = true) Integer enableMaxAmount,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		try {
			Map<String,Object> map=payChannelService.updatePayChannelEnableMaxAmount(id, enableMaxAmount);
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
	 * 充值二维码上传
	 * */
	@RequestMapping(value={"/uploadQRCode"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> uploadQRCode(@RequestParam(name = "imgName", required = true) String imgName,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		try {
			Map<String,Object> map=fTPService.upload(imgName);
			return map;
		}catch(Exception e){
			e.printStackTrace();
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	/**
	 * 前端需要的充值方式(全部为有效)
	 * */
	@RequestMapping(value={"/QDPayType"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> queryQDPayTypeName() {
		Map<String, Object> ret = new HashMap<>();
		try {
			String payTypeName=Constants.PayTypeName.PAY_TYPE_CLASS.getCode();
			List<PayType> payTypeList=cacheServ.getPayType(payTypeName);
			List<PayType> payTypeLists=new ArrayList<PayType>();
			for(int i=0; i<payTypeList.size();i++)    {   
				PayType payType=payTypeList.get(i);
			    if(payType.getState().intValue()==Constants.BankCardState.ENABLED.getCode()) {
			    	payTypeLists.add(payType);
			    }else {
			    	continue;
			    }
			 }
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", payTypeLists);
			return ret;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
	/**
	 * 前端需要的充值渠道(全部为有效)
	 * */
	@RequestMapping(value={"/QDPayChannel"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> queryQDPayChannel(@RequestParam(name = "payType", required = true) Integer payType,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		try {
			String payChannelName=Constants.PayChannel.PAY_CHANNEL.getCode();
			Map<Integer, PayChannel> payChannelList=cacheServ.getPayChannel(payChannelName);
			List<PayChannel> payChannelLists=new ArrayList<PayChannel>();
			if (payChannelList != null) {  
	            Set<Integer> keySet = payChannelList.keySet();  
	            for (Integer integer : keySet) {  
	            	PayChannel payChannel = payChannelList.get(integer);  
	            	if(payChannel.getPayType().intValue()==payType.intValue()&&payChannel.getState().intValue()==Constants.BankCardState.ENABLED.getCode()) {
	            		payChannelLists.add(payChannel);
	            	}else {
	            		continue;
	            	}
	            }  
	        }
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", payChannelLists);
			return ret;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
}
