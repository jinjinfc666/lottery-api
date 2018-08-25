package com.jll.game;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Constants;
import com.jll.common.constants.Message;
import com.jll.common.utils.StringUtils;
import com.jll.entity.Issue;
import com.jll.entity.PlayType;
import com.jll.entity.SysCode;
import com.jll.entity.UserInfo;
import com.jll.game.cqssc.PlayTypeFactory;
import com.jll.game.playtype.PlayTypeFacade;
import com.jll.game.playtype.PlayTypeService;
import com.jll.user.UserInfoService;

@Configuration
@PropertySource("classpath:sys-setting.properties")
@Service
@Transactional
public class LotteryCenterServiceImpl implements LotteryCenterService
{
	private Logger logger = Logger.getLogger(LotteryCenterServiceImpl.class);

	@Value("${sys_lottery_type_impl}")
	private String lotteryTypeImpl;
	
	@Resource
	IssueService issueServ;
	
	@Resource
	CacheRedisService cacheServ;
	
	@Resource
	PlayTypeService playTypeServ;
	
	@Resource
	UserInfoService userServ;
	
	@Override
	public synchronized void makeAPlan() {
		if(StringUtils.isBlank(lotteryTypeImpl)) {
			return ;
		}
		
		String[] impls = lotteryTypeImpl.split(",");
		if(impls == null || impls.length == 0) {
			return;
		}
		
		for(String impl : impls) {
			LotteryTypeService lotteryTypeServ = LotteryTypeFactory.getInstance().createLotteryType(impl);
			if(lotteryTypeServ == null) {
				continue;
			}
			
			String lotteryType = lotteryTypeServ.getLotteryType();
			boolean isPlanExisting = isPlanExisting(lotteryType);
			if(isPlanExisting) {
				continue;
			}
			
			List<Issue> issues = lotteryTypeServ.makeAPlan();
			if(issues != null && issues.size() > 0) {
				String cacheKey = Constants.KEY_PRE_PLAN + lotteryType;
				cacheServ.setPlan(cacheKey, issues);
			}
		}
	}

	private boolean isPlanExisting(String lotteryType) {
		return cacheServ.isPlanExisting(lotteryType);
	}

	@Override
	public boolean hasMoreIssue(String lotteryType) {
		String cacheKey = Constants.KEY_PRE_PLAN + lotteryType;
		List<Issue> issues = cacheServ.getPlan(cacheKey);
		Date nowTime = new Date();
		Issue currIssue = null;
		BulletinBoard bulletinBoard = cacheServ.getBulletinBoard(lotteryType);
		
		if(bulletinBoard == null 
				|| issues == null
				|| issues.size() == 0) {
			return false;
		}
		
		currIssue = bulletinBoard.getCurrIssue();
				
		for(Issue issueTemp : issues) {
			if(currIssue != null) {
				if(currIssue.getStartTime().getTime() > issueTemp.getStartTime().getTime()) {
					continue;
				}
			}
			if(issueTemp.getStartTime().getTime() > nowTime.getTime()
					&& issueTemp.getState() == Constants.IssueState.INIT.getCode()) {
				return true;
			}
		}
		
		return false;
	}


	@Override
	public Issue queryBettingIssue(String lotteryType) {
		BulletinBoard bulletinBoard = cacheServ.getBulletinBoard(lotteryType);
		
		if(bulletinBoard == null) {
			return null;
		}
		
		return bulletinBoard.getCurrIssue();
	}

	@Override
	public Issue queryLastIssue(String lotteryType) {
		BulletinBoard bulletinBoard = cacheServ.getBulletinBoard(lotteryType);
		
		if(bulletinBoard == null) {
			return null;
		}
		
		return bulletinBoard.getLastIssue();
	}
	
	
	/**
	 * 定时遍历期次，修改状态
	 */
	
	public synchronized void processScheduleIssue() {
		Map<String, SysCode> lottoTypes = cacheServ.getSysCode(Constants.SysCodeTypes.LOTTERY_TYPES.getCode());
		if(lottoTypes == null || lottoTypes.size() == 0) {
			return ;
		}
		
		Iterator<String> ite = lottoTypes.keySet().iterator();
		while(ite.hasNext()) {
			String key = ite.next();
			SysCode sysCode = lottoTypes.get(key);
			if(sysCode.getIsCodeType().intValue() == Constants.SysCodeTypesFlag.code_val.getCode()) {
				processBulletinBoard(sysCode);
			}
		}
	}

	private void processBulletinBoard(SysCode lottoType) {
		Issue currIssue = null;
		initBulletinBoard(lottoType);
		
		if(!isPlanExisting(lottoType.getCodeName())) {
			return ;
		}
						
		currIssue = changeIssueState(lottoType.getCodeName());
		
		if(currIssue != null && 
				currIssue.getState() == Constants.IssueState.END_ISSUE.getCode()
				&& hasMoreIssue(lottoType.getCodeName())) {
			cacheServ.publishMessage(Constants.TOPIC_WINNING_NUMBER, currIssue.getIssueNum());
			
			currIssue = moveToNext(currIssue, lottoType.getCodeName());
		}
	}

	private Issue changeIssueState(String lottoType) {
		BulletinBoard bulletinBoard = cacheServ.getBulletinBoard(lottoType);
		Issue currIssue = null;
		Date currTime = new Date();
		Date startTime = null;
		Date endTime = null;
		Date endBettingTime = null;
		Issue temp = null;
		String cacheKey = Constants.KEY_PRE_PLAN + lottoType;
		List<Issue> plans = cacheServ.getPlan(cacheKey);
		Integer indx = 0;
		String codeTypeName = "lottery_config_" + lottoType;
		String codeValName = Constants.LotteryAttributes.BETTING_END_TIME.getCode();
		SysCode lottoAttri = cacheServ.getSysCode(codeTypeName, codeValName);
		boolean hasChanged = false;
		
		if(lottoAttri == null) {
			return null;
		}
		
		if(bulletinBoard == null) {
			return null;
		}
		currIssue = bulletinBoard.getCurrIssue();
		
		if(currIssue == null) {
			return null;
		}
		
		startTime = currIssue.getStartTime();
		endTime = currIssue.getEndTime();
		endBettingTime = DateUtils.addSeconds(endTime, 
				Integer.valueOf(lottoAttri.getCodeVal())*-1);
		endTime = DateUtils.addSeconds(endTime, -5);
		temp = issueServ.getIssueById(currIssue.getId());
		
		if(temp == null) {
			return null;
		}
		
		if(currIssue.getState() == Constants.IssueState.BETTING.getCode()
				&& endBettingTime.getTime() <= currTime.getTime()) {
			currIssue.setState(Constants.IssueState.END_BETTING.getCode());
			temp.setState(Constants.IssueState.END_BETTING.getCode());
			hasChanged = true;
		}else if(currIssue.getState() == Constants.IssueState.END_BETTING.getCode()
				&& endTime.getTime() <= currTime.getTime()) {
			currIssue.setState(Constants.IssueState.END_ISSUE.getCode());
			temp.setState(Constants.IssueState.END_ISSUE.getCode());
			hasChanged = true;
		}else if(currIssue.getState() == Constants.IssueState.INIT.getCode()
				&& startTime.getTime() <= currTime.getTime()) {
			currIssue.setState(Constants.IssueState.BETTING.getCode());
			temp.setState(Constants.IssueState.BETTING.getCode());
			hasChanged = true;
		}
		
		if(hasChanged) {
			indx = getIndexOfIssue(currIssue, plans);
			if(indx == null) {
				return null;
			}
			plans.set(indx, currIssue);
			bulletinBoard.setCurrIssue(currIssue);
			
			issueServ.saveIssue(temp);
			cacheServ.setPlan(cacheKey, plans);
			cacheServ.setBulletinBoard(lottoType, bulletinBoard);
		}

		return currIssue;
	}

	private Issue moveToNext(Issue currIssue, String lotteryType) {
		Issue nextIssue = null;
		Integer indx = 0;
		String cacheKey = Constants.KEY_PRE_PLAN + lotteryType;		
		List<Issue> plans = cacheServ.getPlan(cacheKey);
		BulletinBoard bulletinBoard = cacheServ.getBulletinBoard(lotteryType);
				
		if(!hasMoreIssue(lotteryType)) {
			return null;
		}
		
		indx = getIndexOfNextIssue(currIssue, plans);
		if(indx == null) {
			return null;
		}
		
		//indx++;
		nextIssue = plans.get(indx);
		nextIssue.setState(Constants.IssueState.BETTING.getCode());
		plans.set(indx, nextIssue);
		issueServ.saveIssue(nextIssue);
		cacheServ.setPlan(cacheKey, plans);
		
		bulletinBoard.setCurrIssue(nextIssue);
		cacheServ.setBulletinBoard(lotteryType, bulletinBoard);
		
		return nextIssue;
	}

	private Integer getIndexOfNextIssue(Issue currIssue, List<Issue> plans) {
		int indx = 0;
		Date nowTime = new Date();
				
		for(Issue issueTemp : plans) {
			if(currIssue != null) {
				if(currIssue.getStartTime().getTime() > issueTemp.getStartTime().getTime()) {
					indx++;
					continue;
				}
			}
			if(issueTemp.getStartTime().getTime() <= nowTime.getTime()
					&& issueTemp.getEndTime().getTime() > nowTime.getTime()
					&& issueTemp.getState() == Constants.IssueState.INIT.getCode()) {
				return indx;
			}
			
			indx++;
		}
		
		return null;
	}
	
	private Integer getIndexOfIssue(Issue currIssue, List<Issue> plans) {
		int indx = 0;
		
		if(currIssue == null || plans == null || plans.size() == 0) {
			return null;
		}
				
		for(Issue issueTemp : plans) {
			if(issueTemp.getId().intValue() == currIssue.getId().intValue()) {
				return indx;
			}
			
			indx++;
		}
		
		return null;
	}

	private void initBulletinBoard(SysCode lottoType) {
		BulletinBoard bulletinBoard = cacheServ.getBulletinBoard(lottoType.getCodeName());
		if(bulletinBoard == null) {
			bulletinBoard = new BulletinBoard();
			cacheServ.setBulletinBoard(lottoType.getCodeName(), bulletinBoard);
		}
		
		if(!isPlanExisting(lottoType.getCodeName())) {
			bulletinBoard.setCurrIssue(null);
			cacheServ.setBulletinBoard(lottoType.getCodeName(), bulletinBoard);
		}else {
			moveToNext(bulletinBoard.getCurrIssue(), lottoType.getCodeName());
		}
	}

	@Override
	public String PreBet(Map<String, Object> params, Map<String, Object> data) {
		String playTypeName = null;
		PlayTypeFacade playTypeFacade = null;
		String lotteryType = (String)params.get("lottoType");
		Integer playTypeId = (Integer)params.get("playType");
		PlayType playType = playTypeServ.queryById(playTypeId);
		String userName = SecurityContextHolder.getContext().getAuthentication().getName();
		UserInfo user = userServ.getUserByUserName(userName);
		
		playTypeName = lotteryType + "/" + playType.getClassification() + "/" + playType.getPtName();
		playTypeFacade = PlayTypeFactory.getInstance().getPlayTypeFacade(playTypeName);
		
		Map<String, Object> retData = playTypeFacade.preProcessNumber(params, user);
		data.putAll(retData);
		
		return Integer.toString(Message.status.SUCCESS.getCode());
	}

	
}
