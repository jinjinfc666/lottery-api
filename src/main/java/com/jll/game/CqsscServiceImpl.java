package com.jll.game;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.constants.Constants;
import com.jll.entity.Issue;

@Configuration
@PropertySource("classpath:sys-setting.properties")
@Service
@Transactional
public class CqsscServiceImpl implements LotteryTypeService
{
	private Logger logger = Logger.getLogger(CqsscServiceImpl.class);

	private final String lotteryType = "cqssc";
	
	@Resource
	IssueService issueServ;
	
	@Override
	public List<Issue> makeAPlan() {
		//10:00-22:00（72期）10分钟一期，22:00-02:00（48期）5分钟一期
		List<Issue> issues = new ArrayList<>();
		int maxAmount = 120;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR, 10);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		for(int i = 0; i < maxAmount; i++) {
			if(i < 72) {
				Issue issue = new Issue();
				issue.setStartTime(calendar.getTime());
				calendar.add(Calendar.MINUTE, 10);
				issue.setEndTime(calendar.getTime());
				issue.setIssueNum(generateLottoNumber(++i));
				issue.setLotteryType(lotteryType);
				issue.setState(Constants.IssueState.INIT.getCode());
				
				issues.add(issue);
			}else{
				Issue issue = new Issue();
				issue.setStartTime(calendar.getTime());
				calendar.add(Calendar.MINUTE, 5);
				issue.setEndTime(calendar.getTime());
				issue.setIssueNum(generateLottoNumber(++i));
				issue.setLotteryType(lotteryType);
				issue.setState(Constants.IssueState.INIT.getCode());
				
				issues.add(issue);
			}
		}
		
		issueServ.savePlan(issues);
		return issues;
	}

	@Override
	public String getLotteryType() {
		return lotteryType;
	}
	
	
	private String generateLottoNumber(int seq) {
		StringBuffer buffer = new StringBuffer();
		Date curr = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		DecimalFormat numFormat = new DecimalFormat("000");

		buffer.append(format.format(curr)).append("-").append(numFormat.format(seq));
		
		String ret = buffer.toString();
		logger.debug("lotto number :::::" + ret);
		return ret;
		
	}
	
}
