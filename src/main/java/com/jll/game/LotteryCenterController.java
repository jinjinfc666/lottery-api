package com.jll.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Constants;
import com.jll.common.constants.Constants.SysCodeTypes;
import com.jll.common.constants.Message;
import com.jll.entity.Issue;
import com.jll.entity.OrderInfo;
import com.jll.entity.PlayType;
import com.jll.entity.SysCode;
import com.jll.entity.UserInfo;
import com.jll.game.order.OrderService;
import com.jll.game.playtype.PlayTypeService;
import com.jll.sysSettings.syscode.SysCodeService;
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
	
	@RequestMapping(value="/pre-bet", method = { RequestMethod.POST }, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> PreBet(@RequestBody OrderInfo order){
		
		
		return null;
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
			resp.put(Message.KEY_ERROR_CODE, Message.Error.getErrorByCode(retCode));
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
		boolean isExisting = false;
		boolean hasMoreIssue = false;
		Issue currentIssue = null;
		Issue lastIssue = null;
		SysCode lotteryTypeObj = null;
		
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
		
		currentIssue = lotCenServ.queryBettingIssue(lotteryType);
		if(currentIssue == null) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_GAME_NO_START.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_GAME_NO_START.getErrorMes());
			return resp;
		}
		
		lastIssue = lotCenServ.queryLastIssue(lotteryType);
		
		lotteryTypeObj = cacheServ.getSysCode(SysCodeTypes.LOTTERY_TYPES.getCode(), lotteryType);
		
		playTypes = cacheServ.getPlayType(lotteryTypeObj);
		if(playTypes == null || playTypes.size() == 0) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_GAME_MISSTING_PLAY_TYPE.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_GAME_MISSTING_PLAY_TYPE.getErrorMes());
			return resp;
		}
		
		if(lastIssue != null) {
			data.put("lastIssue", lastIssue);
		}
		
		data.put("currIssue", currentIssue);
		data.put("playType", playTypes);
		
		resp.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		resp.put(Message.KEY_DATA, data);
		return resp;
	}
	
	@RequestMapping(value="/{lottery-type}/play-types", method = { RequestMethod.GET }, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> queryPlayType(@PathVariable(name = "lottery-type", required = true) String lotteryType){
		
		
		return null;
	}
	
	@RequestMapping(value="/{lottery-type}/times", method = { RequestMethod.GET }, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> queryTimes(@PathVariable(name = "lottery-type", required = true) String lotteryType){
		
		
		return null;
	}
	
	@RequestMapping(value="/{lottery-type}/patterns", method = { RequestMethod.GET }, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> queryPattern(@PathVariable(name = "lottery-type", required = true) String lotteryType){
		
		
		return null;
	}
	
	@RequestMapping(value="/{lottery-type}/prize-rates", method = { RequestMethod.GET }, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> queryPrizeRate(@PathVariable(name = "lottery-type", required = true) String lotteryType){
		
		
		return null;
	}
}
