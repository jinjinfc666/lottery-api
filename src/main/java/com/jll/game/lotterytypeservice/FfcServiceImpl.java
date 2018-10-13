package com.jll.game.lotterytypeservice;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Constants;
import com.jll.common.utils.DateUtil;
import com.jll.entity.Issue;
import com.jll.game.IssueService;
import com.jll.game.order.OrderService;
import com.jll.game.playtype.PlayTypeService;
import com.jll.spring.extend.SpringContextUtil;
import com.jll.user.UserInfoService;
import com.jll.user.details.UserAccountDetailsService;
import com.jll.user.wallet.WalletService;

/**
 * 双分彩
 * @author Administrator
 *
 */
public class FfcServiceImpl extends DefaultPrivateLottoTypeServiceImpl
{
	private Logger logger = Logger.getLogger(FfcServiceImpl.class);

	private final String lotteryType = "ffc";
	
	IssueService issueServ = (IssueService)SpringContextUtil.getBean("issueServiceImpl");
	
	CacheRedisService cacheServ = (CacheRedisService)SpringContextUtil.getBean("cacheRedisServiceImpl");
	
	WalletService walletServ = (WalletService)SpringContextUtil.getBean("walletServiceImpl");
	
	OrderService orderInfoServ = (OrderService)SpringContextUtil.getBean("orderServiceImpl");
	
	PlayTypeService playTypeServ = (PlayTypeService)SpringContextUtil.getBean("playTypeServiceImpl");
	
	UserAccountDetailsService accDetailsServ = (UserAccountDetailsService)SpringContextUtil.getBean("userAccountDetailsServiceImpl");
	
	UserInfoService userServ = (UserInfoService)SpringContextUtil.getBean("userInfoServiceImpl");
	
	String codeTypeName = Constants.SysCodeTypes.LOTTERY_CONFIG_FFC.getCode();
	
	
	@Override
	public List<Issue> makeAPlan() {
		//00:00-23:59  1分钟一期
		List<Issue> issues = new ArrayList<>();
		int maxAmount = 1440;
		Calendar calendar = Calendar.getInstance();
		Date today = new Date();
		today = DateUtil.addMinutes(today, 10);
		calendar.setTime(today);
		
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		for(int i = 0; i < maxAmount; i++) {
			Issue issue = new Issue();
			issue.setStartTime(calendar.getTime());
			calendar.add(Calendar.MINUTE, 1);
			issue.setEndTime(calendar.getTime());
			issue.setIssueNum(generateLottoNumber(i + 1, today));
			issue.setLotteryType(lotteryType);
			issue.setState(Constants.IssueState.INIT.getCode());
			
			issues.add(issue);
		}
		
		issueServ.savePlan(issues);
		return issues;
	}

	@Override
	public String getLotteryType() {
		return lotteryType;
	}
	
	
	private String generateLottoNumber(int seq, Date curr) {
		StringBuffer buffer = new StringBuffer();
		//Date curr = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyMMdd");
		DecimalFormat numFormat = new DecimalFormat("000");

		buffer.append(format.format(curr)).append("-").append(numFormat.format(seq));
		
		String ret = buffer.toString();
		logger.debug("lotto number :::::" + ret);
		return ret;
		
	}

	@Override
	public String getCodeTypeName() {
		return codeTypeName;
	}
}
