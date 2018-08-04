package com.jll.game;

import java.util.HashMap;
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
import com.jll.common.constants.Constants.SysCodeTypes;
import com.jll.common.constants.Message;
import com.jll.entity.Issue;
import com.jll.entity.OrderInfo;
import com.jll.entity.PlayType;
import com.jll.entity.UserInfo;
import com.jll.sysSettings.codeManagement.SysCodeService;
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
	
	@RequestMapping(value="/pre-bet", method = { RequestMethod.POST }, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> PreBet(@RequestBody OrderInfo order){
		
		
		return null;
	}
	
	@RequestMapping(value="/bet", method = { RequestMethod.POST }, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> bet(@RequestBody OrderInfo order){
		
		
		return null;
	}
	
	@RequestMapping(value="/{lottery-type}/betting-issue", method = { RequestMethod.GET }, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> queryBettingIssue(@PathVariable(name = "lottery-type", required = true) String lotteryType){
		Map<String, Object> resp = new HashMap<String, Object>();
		
		Map<String, Object> data = new HashMap<>();
		
		boolean isExisting = cacheServ.isCodeExisting(SysCodeTypes.LOTTERY_TYPES, lotteryType);
		if(!isExisting) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_GAME_LOTTERY_TYPE_INVALID.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_GAME_LOTTERY_TYPE_INVALID.getErrorMes());
			return resp;
		}
		
		boolean hasMoreIssue = lotCenServ.hasMoreIssue(lotteryType);
		if(!hasMoreIssue) {
			resp.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			resp.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_GAME_END.getCode());
			resp.put(Message.KEY_ERROR_MES, Message.Error.ERROR_GAME_END.getErrorMes());
			return resp;
		}
		
		Issue currentIssue = lotCenServ.queryBettingIssue(lotteryType);
		
		Issue lastIssue = lotCenServ.queryLastIssue(lotteryType);
		
		PlayType playType = lotCenServ.queryPlayType(lotteryType);
		
		data.put("lastIssue", lastIssue);
		data.put("currIssue", currentIssue);
		data.put("playType", playType);
		
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
