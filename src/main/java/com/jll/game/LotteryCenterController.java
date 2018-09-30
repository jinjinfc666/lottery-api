package com.jll.game;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.eclipse.jetty.util.StringUtil;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Constants;
import com.jll.common.constants.Constants.SysCodeTypes;
import com.jll.common.constants.Message;
import com.jll.common.utils.StringUtils;
import com.jll.entity.Issue;
import com.jll.entity.OrderInfo;
import com.jll.entity.PlayType;
import com.jll.entity.SysCode;
import com.jll.entity.UserInfo;
import com.jll.game.order.OrderService;
import com.jll.game.playtype.PlayTypeService;
import com.jll.sysSettings.syscode.SysCodeService;
import com.jll.user.UserInfoService;
import com.jll.user.wallet.WalletService;
import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;

@Api2Doc(id = "lotteryCenter", name = "lottery center")
@ApiComment(seeClass = UserInfo.class)
@RestController
@RequestMapping({ "/lotteries" })
public class LotteryCenterController {
	
	private Logger logger = Logger.getLogger(LotteryCenterController.class); 
	
	@Resource
	IssueService issueServ;
	
	@Resource
	SysCodeService sysCodeServ;
	
	@Resource
	LotteryCenterService lotCenServ;
	
	@Resource
	CacheRedisService cacheServ;
	
	@Resource
	PlayTypeService playTypeServ;
	
	@Resource
	OrderService orderServ;
	
	@Resource
	WalletService walletServ;
	
	@Resource
	UserInfoService userServ;
	
	@RequestMapping(value="/{lottery-type}/pre-bet", method = { RequestMethod.POST }, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> PreBet(@PathVariable(name = "lottery-type", required = true) String lotteryType,
			@RequestBody Map<String, Object> params){
		boolean isTimesValid = false;
		boolean isMonUnitValid = false;
		boolean isPlayTypeValid = false;
		Map<String, Object> resp = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		String betNum = (String)params.get("betNum");
		Integer times = (Integer)params.get("times");
		Float monUnit = ((Double)params.get("monUnit")).floatValue();
		Integer playType = (Integer)params.get("playType");
		String retCode = null;
		
		params.put("lottoType", lotteryType);
		params.put("monUnit", monUnit);
		
		if(StringUtils.isBlank(betNum)) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_GAME_INVALID_BET_NUM.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_GAME_INVALID_BET_NUM.getErrorMes());
			return resp;
		}
		
		isTimesValid = cacheServ.isTimesValid(lotteryType, times);
		
		if(!isTimesValid) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_SYSTEM_INVALID_BETTING_TIMES.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_SYSTEM_INVALID_BETTING_TIMES.getErrorMes());
			return resp;
		}
		
		isMonUnitValid = cacheServ.isMonUnitValid(lotteryType, monUnit);
		if(!isMonUnitValid) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_SYSTEM_INVALID_BETTING_MONEY_UNIT.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_SYSTEM_INVALID_BETTING_MONEY_UNIT.getErrorMes());
			return resp;
		}
		
		isPlayTypeValid = cacheServ.isPlayTypeValid(lotteryType, playType);
		if(!isPlayTypeValid) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_GAME_INVALID_PLAY_TYPE.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_GAME_INVALID_PLAY_TYPE.getErrorMes());
			return resp;
		}
		
		retCode = lotCenServ.PreBet(params, data);
		if(StringUtil.isBlank(retCode) || 
				!retCode.equals(Integer.toString(Message.status.SUCCESS.getCode()))) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_GAME_FAILED_PROCESS_BETTING_NUM.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_GAME_FAILED_PROCESS_BETTING_NUM.getErrorMes());
			return resp;
		}
		
		resp.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		resp.put(Message.KEY_DATA, data);
		return resp;
	}
	
	@RequestMapping(value="/{lottery-type}/bet/zh/{zhFlag}/wallet/{walletId}", method = { RequestMethod.POST }, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> bet(@PathVariable(name = "lottery-type", required = true) String lotteryType,
			@PathVariable(name = "zhFlag", required = true) int zhFlag,
			@PathVariable(name = "walletId", required = true) int walletId,
			@RequestBody List<OrderInfo> orders){
		Map<String, Object> resp = new HashMap<String, Object>();
		boolean isLotteryTypeExisting = false;
		String retCode = null;
		
		isLotteryTypeExisting = cacheServ.isCodeExisting(SysCodeTypes.LOTTERY_TYPES, lotteryType);
		if(!isLotteryTypeExisting) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_GAME_LOTTERY_TYPE_INVALID.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_GAME_LOTTERY_TYPE_INVALID.getErrorMes());
			return resp;
		}
				
		Constants.ZhState zh = Constants.ZhState.getByCode(zhFlag);
		if(zh == null) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_GAME_INVALID_ZH_FLAG.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_GAME_INVALID_ZH_FLAG.getErrorMes());
			return resp;
		}
		
		if(orders == null || orders.size() == 0) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_GAME_NO_ORDER.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_GAME_NO_ORDER.getErrorMes());
			return resp;
		}
		
		if(zh.getCode() == Constants.ZhState.NON_ZH.getCode()
				&& orders.size() > 1) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_GAME_MULTIPLE_ORDERS_NOT_ALLOWED.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_GAME_MULTIPLE_ORDERS_NOT_ALLOWED.getErrorMes());
			return resp;
		}
		
		retCode = orderServ.saveOrders(orders, walletId, zhFlag,lotteryType);
		if(!String.valueOf(Message.status.SUCCESS.getCode()).equals(retCode)) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.getErrorByCode(retCode).getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.getErrorByCode(retCode).getErrorMes());
			return resp;
		}
		
		resp.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		
		return resp;
	}
	
	
	@RequestMapping(value="/{lottery-type}/betting-issue", method = { RequestMethod.GET }, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> queryBettingIssue(@PathVariable(name = "lottery-type", required = true) String lotteryType){
		Map<String, Object> resp = new HashMap<String, Object>();
		
		Map<String, Object> data = new HashMap<>();
		List<PlayType> playTypes = null;
		List<PlayType> playTypess = new ArrayList<PlayType>();
		boolean isExisting = false;
		boolean hasMoreIssue = false;
		Issue currentIssue = null;
		Issue lastIssue = null;
		SysCode lotteryTypeObj = null;
		Date nowTime = null;
		
		isExisting = cacheServ.isCodeExisting(SysCodeTypes.LOTTERY_TYPES, lotteryType);
		if(!isExisting) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_GAME_LOTTERY_TYPE_INVALID.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_GAME_LOTTERY_TYPE_INVALID.getErrorMes());
			return resp;
		}
		
		hasMoreIssue = lotCenServ.hasMoreIssue(lotteryType);
		if(!hasMoreIssue) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_GAME_END.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_GAME_END.getErrorMes());
			return resp;
		}
		if(!lotteryType.equals(Constants.LottoType.MMC.getCode())) {
			currentIssue = lotCenServ.queryBettingIssue(lotteryType);
			if(currentIssue == null) {
				resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
				resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_GAME_NO_START.getCode());
				resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_GAME_NO_START.getErrorMes());
				return resp;
			}
		}
		lastIssue = lotCenServ.queryLastIssue(lotteryType);
		
		lotteryTypeObj = cacheServ.getSysCode(SysCodeTypes.LOTTERY_TYPES.getCode(), lotteryType);
		
		playTypes = cacheServ.getPlayType(lotteryTypeObj);
		
		if(playTypes!=null&&playTypes.size()>0) {
			Integer stateas=null;
			for(int i=0; i<playTypes.size();i++)    {   
			     PlayType playType=playTypes.get(i);
			     stateas=playType.getState();
			     if((int)stateas==1) {
			    	 playTypess.add(playType);
				}
			 }
		}
		
		
		
		if(playTypes == null || playTypes.size() == 0) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_GAME_MISSTING_PLAY_TYPE.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_GAME_MISSTING_PLAY_TYPE.getErrorMes());
			return resp;
		}
		
		if(!lotteryType.equals(Constants.LottoType.MMC.getCode())) {
			String codeTypeName = "lottery_config_" + lotteryType;
			String codeValName = Constants.LotteryAttributes.BETTING_END_TIME.getCode();
			SysCode lottoAttri = cacheServ.getSysCode(codeTypeName, codeValName);
			nowTime = new Date();
			Long downCounter = currentIssue.getEndTime().getTime() - nowTime.getTime();
			downCounter = downCounter/1000 - Long.valueOf(lottoAttri.getCodeVal());
			currentIssue.setDownCounter(downCounter);
			data.put("currIssue", currentIssue);
		}else {
			data.put("currIssue", null);
		}
		data.put("lastIssue", lastIssue);
		data.put("playType", playTypess);
		
		resp.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		resp.put(Message.KEY_DATA, data);
		return resp;
	}
	
//	@RequestMapping(value="/{lottery-type}/play-types", method = { RequestMethod.GET }, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
//	public Map<String, Object> queryPlayType(@PathVariable(name = "lottery-type", required = true) String lotteryType){
//		
//		
//		return null;
//	}
	
	@RequestMapping(value="/{lottery-type}/times", method = { RequestMethod.GET }, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> queryTimes(@PathVariable(name = "lottery-type", required = true) String lotteryType){
		Map<String, Object> resp = new HashMap<String, Object>();
		boolean isExisting = false;
		
		isExisting = cacheServ.isCodeExisting(SysCodeTypes.LOTTERY_TYPES, lotteryType);
		if(!isExisting) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_GAME_LOTTERY_TYPE_INVALID.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_GAME_LOTTERY_TYPE_INVALID.getErrorMes());
			return resp;
		}
		
		SysCode sysCode = cacheServ.getBetTimes(lotteryType);
		
		if(sysCode == null || StringUtils.isBlank(sysCode.getCodeVal())){
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_SYSTEM_INVALID_BETTING_TIMES.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_SYSTEM_INVALID_BETTING_TIMES.getErrorMes());
			return resp;
		}
		
		resp.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		resp.put("times", sysCode.getCodeVal());
		return resp;
	}
	
	@RequestMapping(value="/{lottery-type}/patterns", method = { RequestMethod.GET },produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> queryPattern(@PathVariable(name = "lottery-type", required = true) String lotteryType){
		Map<String, Object> resp = new HashMap<String, Object>();
		boolean isExisting = false;
		
		isExisting = cacheServ.isCodeExisting(SysCodeTypes.LOTTERY_TYPES, lotteryType);
		if(!isExisting) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_GAME_LOTTERY_TYPE_INVALID.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_GAME_LOTTERY_TYPE_INVALID.getErrorMes());
			return resp;
		}
		
		SysCode sysCode = cacheServ.getMoneyUnit(lotteryType);
		
		if(sysCode == null || StringUtils.isBlank(sysCode.getCodeVal())){
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_SYSTEM_INVALID_BETTING_MONEY_UNIT.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_SYSTEM_INVALID_BETTING_MONEY_UNIT.getErrorMes());
			return resp;
		}
		
		resp.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		resp.put("patterns", sysCode.getCodeVal());
		return resp;
	}
	
	@RequestMapping(value="/{lottery-type}/prize-rates", method = { RequestMethod.GET }, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> queryPrizeRate(@PathVariable(name = "lottery-type", required = true) String lotteryType){
		Map<String, Object> resp = new HashMap<String, Object>();
		String userName = null;
		boolean isExisting = false;
		UserInfo user = null;
		Float prizeRate = null;
		
		isExisting = cacheServ.isCodeExisting(SysCodeTypes.LOTTERY_TYPES, lotteryType);
		if(!isExisting) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_GAME_LOTTERY_TYPE_INVALID.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_GAME_LOTTERY_TYPE_INVALID.getErrorMes());
			return resp;
		}
		
		userName = SecurityContextHolder.getContext().getAuthentication().getName();
		user = userServ.getUserByUserName(userName);
		prizeRate = userServ.calPrizePattern(user, lotteryType);
		
		resp.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		resp.put("singleBettingPrize", prizeRate);
		return resp;
	}
	//未结算的注单  前端只传彩种，默认查询state为0的数据显示给前端
	@RequestMapping(value="/{lottery-type}/unsettled-bet", method = { RequestMethod.GET }, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> queryUnsettledBet(@PathVariable(name = "lottery-type", required = true) String lotteryType){
		Map<String,Object> map=new HashMap<String,Object>();
		List<OrderInfo> orderInfoList=new ArrayList<OrderInfo>();
		OrderInfo orderInfo=new OrderInfo();
		orderInfo.setId(1);
		orderInfo.setOrderNum("180911-0041");
		orderInfo.setUserId(2);
		orderInfo.setIssueId(4107);
		orderInfo.setWalletId(19);
		orderInfo.setPlayType(1);
		orderInfo.setBetNum("0,2,3,5,1");
		orderInfo.setBetTotal(60);
		orderInfo.setBetAmount((float)6084.44);
		orderInfo.setTimes(20);
		orderInfo.setState(0);
		orderInfo.setDelayPayoutFlag(0);
		orderInfo.setIsZh(1);
		orderInfo.setIsZhBlock(1);
		orderInfo.setTerminalType(1);
		orderInfoList.add(orderInfo);
		map.put("data", orderInfoList);
		map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return map;
	}
	//近期注单   前端只传过来彩种，后台默认只查询30条记录给前端
	@RequestMapping(value="/{lottery-type}/recent-bet", method = { RequestMethod.GET }, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> queryRecentBet(@PathVariable(name = "lottery-type", required = true) String lotteryType){
		Map<String,Object> map=new HashMap<String,Object>();
		List<OrderInfo> orderInfoList=new ArrayList<OrderInfo>();
		OrderInfo orderInfo=new OrderInfo();
		orderInfo.setId(1);
		orderInfo.setOrderNum("180911-0041");
		orderInfo.setUserId(2);
		orderInfo.setIssueId(4107);
		orderInfo.setWalletId(19);
		orderInfo.setPlayType(1);
		orderInfo.setBetNum("0,2,3,5,1");
		orderInfo.setBetTotal(60);
		orderInfo.setBetAmount((float)6084.44);
		orderInfo.setTimes(20);
		orderInfo.setState(0);
		orderInfo.setDelayPayoutFlag(0);
		orderInfo.setIsZh(1);
		orderInfo.setIsZhBlock(1);
		orderInfo.setTerminalType(1);
		orderInfoList.add(orderInfo);
		map.put("data", orderInfoList);
		map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return map;
	}
}
