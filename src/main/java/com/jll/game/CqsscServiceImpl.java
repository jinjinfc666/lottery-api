package com.jll.game;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Constants;
import com.jll.common.http.HttpRemoteStub;
import com.jll.entity.Issue;
import com.jll.entity.SysCode;
import com.jll.spring.extend.SpringContextUtil;

/*@Configuration
@PropertySource("classpath:sys-setting.properties")*/
//@Service
//@Transactional
public class CqsscServiceImpl implements LotteryTypeService
{
	private Logger logger = Logger.getLogger(CqsscServiceImpl.class);

	private final String lotteryType = "cqssc";
	
	//@Resource
	IssueService issueServ = (IssueService)SpringContextUtil.getBean("issueServiceImpl");
	
	CacheRedisService cacheServ = (CacheRedisService)SpringContextUtil.getBean("cacheRedisServiceImpl");
	
	@Override
	public List<Issue> makeAPlan() {
		//10:00-22:00（72期）10分钟一期，22:00-02:00（48期）5分钟一期
		List<Issue> issues = new ArrayList<>();
		int maxAmount = 120;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR_OF_DAY, 10);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		for(int i = 0; i < maxAmount; i++) {
			if(i < 72) {
				Issue issue = new Issue();
				issue.setStartTime(calendar.getTime());
				calendar.add(Calendar.MINUTE, 10);
				issue.setEndTime(calendar.getTime());
				issue.setIssueNum(generateLottoNumber(i + 1));
				issue.setLotteryType(lotteryType);
				issue.setState(Constants.IssueState.INIT.getCode());
				
				issues.add(issue);
			}else{
				Issue issue = new Issue();
				issue.setStartTime(calendar.getTime());
				calendar.add(Calendar.MINUTE, 5);
				issue.setEndTime(calendar.getTime());
				issue.setIssueNum(generateLottoNumber(i + 1));
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
		SimpleDateFormat format = new SimpleDateFormat("yyMMdd");
		DecimalFormat numFormat = new DecimalFormat("000");

		buffer.append(format.format(curr)).append("-").append(numFormat.format(seq));
		
		String ret = buffer.toString();
		logger.debug("lotto number :::::" + ret);
		return ret;
		
	}

	@Override
	public void queryWinningNum(String issueNum) {
		String[] urls = null;
		Map<String, Object> result = null;
		String response = null;
		String codeTypeName = Constants.SysCodeTypes.LOTTERY_CONFIG_CQSSC.getCode();
		String codeName = Constants.LotteryAttributes.URL_WINING_NUMBER_EXTENAL.getCode();
		SysCode sysCode = cacheServ.getSysCode(codeTypeName, codeName);
		String winningNum = null;
		Issue issue = null;
		int maxCounter = 3600;
		int currCounter = 0;
		
		if(sysCode == null
				|| StringUtils.isBlank(sysCode.getCodeVal())) {
			return;
		}
		
		urls = sysCode.getCodeVal().split(",");
		while(currCounter < maxCounter) {
			for(String url : urls) {
				url = url.replace("{issue_id}", issueNum);
				
				try {
					result = HttpRemoteStub.synGet(new URI(url), null, null);
					
					if(result != null && result.size() > 0) {
						if(result.get("responseBody") != null) {
							response = (String)result.get("responseBody");
							if(response.contains(issueNum)) {
								if(response.contains("code")) {//360
									winningNum = parse360API(response);
								}else if(response.contains("winningNumber")) {//网易
									winningNum = parse163API(response);
								}
								
								if(!StringUtils.isBlank(winningNum)) {
									//store into to database
									issue = issueServ.getIssueByIssueNum(issueNum);
									issue.setRetNum(winningNum);
									issueServ.saveIssue(issue);
									
									//inform the progress to payout
									cacheServ.publishMessage(Constants.TOPIC_PAY_OUT, issueNum);
									return;								
								}
							}
						}
					}
				} catch (URISyntaxException e) {
					logger.error(e.getMessage());
				}
			}
			
			currCounter++;
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private String parse163API(String response) {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> retItems = null;
		
		try {
			retItems = mapper.readValue(response, HashMap.class);
			Map awardNumberInfoList = (Map)retItems.get("awardNumberInfoList");
			String winningNumber = (String)awardNumberInfoList.get("winningNumber");
			
			return winningNumber;
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	private String parse360API(String response) {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> retItems = null;
		
		try {
			retItems = mapper.readValue(response, HashMap.class);
			//Map awardNumberInfoList = (Map)retItems.get("code");
			String winningNumber = (String)retItems.get("code");
			
			return winningNumber;
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
}
