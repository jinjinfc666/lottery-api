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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.cache.CacheObject;
import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Constants;
import com.jll.common.utils.StringUtils;
import com.jll.entity.Issue;
import com.jll.entity.PlayType;
import com.jll.entity.SysCode;

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
		if(issues == null || issues.size() == 0) {
			return false;
		}
		
		BulletinBoard bulletinBoard = cacheServ.getBulletinBoard(lotteryType);
		if(bulletinBoard == null 
				|| bulletinBoard.getCurrIssue() == null) {
			return false;
		}
		
		Issue currIssue = bulletinBoard.getCurrIssue();
		for(int i = 0; i < issues.size(); i++) {
			Issue issueTemp = issues.get(i);
			if(issueTemp.getId().intValue() == currIssue.getId().intValue()
					&& i < issues.size() -1) {
				return true;
			}
		}
		return false;
	}


	@Override
	public Issue queryBettingIssue(String lotteryType) {
		
		return null;
	}

	@Override
	public Issue queryLastIssue(String lotteryType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PlayType queryPlayType(String lotteryType) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	/**
	 * 定时遍历期次，修改状态
	 */
	
	public void processScheduleIssue() {
		Map<String, SysCode> lottoTypes = cacheServ.getSysCode(Constants.SysCodeTypes.LOTTERY_TYPES.getCode());
		if(lottoTypes == null || lottoTypes.size() == 0) {
			return ;
		}
		
		Iterator<String> ite = lottoTypes.keySet().iterator();
		while(ite.hasNext()) {
			String key = ite.next();
			SysCode sysCode = lottoTypes.get(key);			
			if(sysCode.getCodeType().intValue() == Constants.SysCodeTypesFlag.code_val.getCode()) {
				processBulletinBoard(sysCode);
			}
		}
	}

	private void processBulletinBoard(SysCode lottoType) {
		
		BulletinBoard bulletinBoard = initBulletinBoard(lottoType);
		
		if(!isPlanExisting(lottoType.getCodeName())) {
			return ;
		}
		
		Issue currIssue = bulletinBoard.getCurrIssue();
		
		if(currIssue == null) {
			currIssue = moveToNext(currIssue, lottoType.getCodeName());
			
			/*
			Issue firstPlan = plans.get(0);
			if(firstPlan.getStartTime().getTime() > currDay.getTime()) {
				return;
			}
			
			currIssue = firstPlan;
			
			currIssue.setState(Constants.IssueState.BETTING.getCode());
			issueServ.saveIssue(currIssue);
			
			plans.remove(0);
			plans.add(0, currIssue);
			cacheServ.setPlan(cacheKey, plans);
			
			return;*/
		}
		
		if(currIssue == null) {
			return;
		}
		
		currIssue = changeIssueState(currIssue, lottoType.getCodeName());
		
		if(currIssue.getState() == Constants.IssueState.END_ISSUE.getCode()
				&& hasMoreIssue(lottoType.getCodeName())) {
			currIssue = moveToNext(currIssue, lottoType.getCodeName());
		}
		
		/*Date endTime = currIssue.getEndTime();
		Date endBettingTime = DateUtils.addSeconds(endTime, -45);
		if(currDay.getTime() < endBettingTime.getTime()) {
			return;
		}
		
		if(currIssue.getState() == Constants.IssueState.BETTING.getCode()) {
			//TODO change the state to end betting
			return;
			
		}
		
		if(currDay.getTime() < endTime.getTime()) {
			return;
		}
		
		if(currIssue.getState() == Constants.IssueState.END_BETTING.getCode()) {
			//TODO change the state to end 
			//TODO try to obtain the result of draw
			bulletinBoard.setLastIssue(currIssue);
			Issue nextIssue = getNextPlan(currIssue, plans);
			bulletinBoard.setCurrIssue(nextIssue);
			return;
			
		}*/
		
	}

	private Issue changeIssueState(Issue currIssue, String lottoType) {
		Date currTime = new Date();
		Date endTime = currIssue.getEndTime();
		Date endBettingTime = DateUtils.addSeconds(endTime, -45);
		Issue ret = currIssue;
		String cacheKey = Constants.KEY_PRE_PLAN + lottoType;
		List<Issue> plans = cacheServ.getPlan(cacheKey);
		Integer indx = 0;
		
		if(currIssue.getState() == Constants.IssueState.BETTING.getCode()
				&& endBettingTime.getTime() <= currTime.getTime()) {
			ret.setState(Constants.IssueState.END_BETTING.getCode());
		}else if(currIssue.getState() == Constants.IssueState.END_BETTING.getCode()
				&& endTime.getTime() <= currTime.getTime()) {
			ret.setState(Constants.IssueState.END_ISSUE.getCode());
		}
		
		indx = getIndexOfIssue(currIssue, plans);
		if(indx == null) {
			return null;
		}
		
		indx++;
		ret = plans.get(indx);
		plans.remove(indx.intValue());
		plans.add(indx, currIssue);
		cacheServ.setPlan(cacheKey, plans);

		issueServ.saveIssue(currIssue);
		return ret;
	}

	private Issue moveToNext(Issue currIssue, String lotteryType) {
		//move to first issue of plans		
		Issue nextIssue = null;
		Integer indx = 0;
		String cacheKey = Constants.KEY_PRE_PLAN + lotteryType;		
		List<Issue> plans = cacheServ.getPlan(cacheKey);
		BulletinBoard bulletinBoard = cacheServ.getBulletinBoard(lotteryType);
				
		if(!hasMoreIssue(lotteryType)) {
			return null;
		}
		
		indx = getIndexOfIssue(currIssue, plans);
		if(indx == null) {
			return null;
		}
		
		indx++;
		nextIssue = plans.get(indx);
		nextIssue.setState(Constants.IssueState.BETTING.getCode());
		plans.remove(indx.intValue());
		plans.add(indx, currIssue);
		cacheServ.setPlan(cacheKey, plans);
		issueServ.saveIssue(nextIssue);
		
		bulletinBoard.setLastIssue(currIssue);
		bulletinBoard.setCurrIssue(nextIssue);
		cacheServ.setBulletinBoard(lotteryType, bulletinBoard);
		
		return nextIssue;
	}

	private Integer getIndexOfIssue(Issue currIssue, List<Issue> plans) {
		int indx = 0;
		
		if(currIssue == null) {
			return -1;
		}
		
		for(Issue issue : plans) {
			if(issue.getId().intValue() == currIssue.getId().intValue()) {
				return indx;
			}
			indx++;
		}
		
		return null;
	}

	private BulletinBoard initBulletinBoard(SysCode lottoType) {
		BulletinBoard bulletinBoard = cacheServ.getBulletinBoard(lottoType.getCodeName());
		if(bulletinBoard == null) {
			bulletinBoard = new BulletinBoard();
		}
		
		if(!isPlanExisting(lottoType.getCodeName())) {
			bulletinBoard.setCurrIssue(null);
		}
		
		cacheServ.setBulletinBoard(lottoType.getCodeName(), bulletinBoard);
		
		return bulletinBoard;
	}

	
}
