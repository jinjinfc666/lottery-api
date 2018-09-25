package com.jll.game.mesqueue;

import java.io.Serializable;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.jll.common.cache.CacheRedisServiceImpl;
import com.jll.common.threadpool.ThreadPoolManager;
import com.jll.common.utils.StringUtils;
import com.jll.entity.Issue;
import com.jll.game.IssueService;
import com.jll.game.LotteryTypeFactory;
import com.jll.game.LotteryTypeService;

@Configuration
@PropertySource("classpath:sys-setting.properties")
public class WinningNumberListenerImpl implements MessageDelegateListener {

	private Logger logger = Logger.getLogger(WinningNumberListenerImpl.class);
	
	@Resource
	IssueService issueServ;
	
	@Value("${sys_lottery_type_impl}")
	private String lotteryTypeImpl;
	
	@Override
	public void handleMessage(Serializable message) {
		String[] lottoTypeAndIssueNum = null;
		Issue issue = null;
		String lottoType = null;
		String issueNum = null;
		
		lottoTypeAndIssueNum = ((String)message).split("\\|");
		lottoType = lottoTypeAndIssueNum[0];
		issueNum = lottoTypeAndIssueNum[1];
		issue = issueServ.getIssueByIssueNum(lottoType, issueNum);
		if(issue == null || !StringUtils.isBlank(issue.getRetNum())) {
			return ;
		}
				
		if(StringUtils.isBlank(lotteryTypeImpl)) {
			return ;
		}
		
		String[] impls = lotteryTypeImpl.split(",");
		if(impls == null || impls.length == 0) {
			return;
		}
		
		for(String impl : impls) {
			LotteryTypeService lotteryTypeServ = LotteryTypeFactory
					.getInstance().createLotteryType(impl);
			if(lotteryTypeServ != null
					&& lotteryTypeServ.getLotteryType().equals(lottoType)) {
				
				ThreadPoolManager.getInstance().exeThread(new Runnable() {

					@Override
					public void run() {
						lotteryTypeServ.queryWinningNum((String)message);
					}
					
				});
				
				break;
			}
		}
	}

}
