package com.jll.game;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Constants;
import com.jll.common.utils.StringUtils;
import com.jll.entity.Issue;
import com.jll.entity.PlayType;

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
	public void makeAPlan() {
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
		// TODO Auto-generated method stub
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
	
	
}
