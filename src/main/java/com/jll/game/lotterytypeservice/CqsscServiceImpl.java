package com.jll.game.lotterytypeservice;

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
import com.jll.entity.UserAccount;
import com.jll.entity.UserAccountDetails;
import com.jll.entity.UserInfo;
import com.jll.game.BulletinBoard;
import com.jll.game.IssueService;
import com.jll.game.order.OrderService;
import com.jll.game.playtype.PlayTypeService;
import com.jll.spring.extend.SpringContextUtil;
import com.jll.user.UserInfoService;
import com.jll.user.details.UserAccountDetailsService;
import com.jll.user.wallet.WalletService;

/*@Configuration
@PropertySource("classpath:sys-setting.properties")*/
//@Service
//@Transactional
public class CqsscServiceImpl extends DefaultLottoTypeServiceImpl
{
	private Logger logger = Logger.getLogger(CqsscServiceImpl.class);

	private final String lotteryType = "cqssc";
	
	IssueService issueServ = (IssueService)SpringContextUtil.getBean("issueServiceImpl");
	
	CacheRedisService cacheServ = (CacheRedisService)SpringContextUtil.getBean("cacheRedisServiceImpl");
	
	WalletService walletServ = (WalletService)SpringContextUtil.getBean("walletServiceImpl");
	
	OrderService orderInfoServ = (OrderService)SpringContextUtil.getBean("orderServiceImpl");
	
	PlayTypeService playTypeServ = (PlayTypeService)SpringContextUtil.getBean("playTypeServiceImpl");
	
	UserAccountDetailsService accDetailsServ = (UserAccountDetailsService)SpringContextUtil.getBean("userAccountDetailsServiceImpl");
	
	UserInfoService userServ = (UserInfoService)SpringContextUtil.getBean("userInfoServiceImpl");
	
	@Override
	public List<Issue> makeAPlan() {
		//10:00-22:00（72期）10分钟一期，22:00-02:00（48期）5分钟一期
		List<Issue> issues = new ArrayList<>();
		int maxAmount = 120;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		for(int i = 0; i < maxAmount; i++) {
			if(i < 24
					||(96 <= i && i < 120)) {
				Issue issue = new Issue();
				issue.setStartTime(calendar.getTime());
				calendar.add(Calendar.MINUTE, 5);
				issue.setEndTime(calendar.getTime());
				issue.setIssueNum(generateLottoNumber(i + 1));
				issue.setLotteryType(lotteryType);
				issue.setState(Constants.IssueState.INIT.getCode());
				
				issues.add(issue);
				
			}else if(i == 24) {
				calendar.add(Calendar.HOUR_OF_DAY, 8);
				Issue issue = new Issue();
				issue.setStartTime(calendar.getTime());
				calendar.add(Calendar.MINUTE, 10);
				issue.setEndTime(calendar.getTime());
				issue.setIssueNum(generateLottoNumber(i + 1));
				issue.setLotteryType(lotteryType);
				issue.setState(Constants.IssueState.INIT.getCode());
				
				issues.add(issue);
			}else if(24 < i && i < 96){
				Issue issue = new Issue();
				issue.setStartTime(calendar.getTime());
				calendar.add(Calendar.MINUTE, 10);
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
	public void queryWinningNum(String message) {
		String[] lottoTypeAndIssueNum = null;
		String lottoType = null;
		String issueNum = null;
		String[] urls = null;
		Map<String, Object> result = null;
		String response = null;
		String codeTypeName = Constants.SysCodeTypes.LOTTERY_CONFIG_CQSSC.getCode();
		String codeName = Constants.LotteryAttributes.URL_WINING_NUMBER_EXTENAL.getCode();
		SysCode sysCode = cacheServ.getSysCode(codeTypeName, codeName);
		String winningNum = null;
		Issue issue = null;
		int maxCounter = 1800;
		int currCounter = 0;
		BulletinBoard bulletinBoard = null;
		
		lottoTypeAndIssueNum = ((String)message).split("\\|");
		lottoType = lottoTypeAndIssueNum[0];
		issueNum = lottoTypeAndIssueNum[1];
		bulletinBoard = cacheServ.getBulletinBoard(lottoType);
		if(sysCode == null
				|| StringUtils.isBlank(sysCode.getCodeVal())) {
			return;
		}
		
		urls = sysCode.getCodeVal().split(",");
		while(currCounter < maxCounter) {
			for(String url : urls) {
				url = url.replace("{issue_id}", issueNum.replace("-", ""));
				
				try {
					result = HttpRemoteStub.synGet(new URI(url), null, null);
					
					if(result != null && result.size() > 0) {
						if(result.get("responseBody") != null) {
							response = (String)result.get("responseBody");
							if(response.contains(issueNum.replace("-", ""))) {
								if(response.contains("code")) {//360
									winningNum = parse360API(response);
								}else if(response.contains("winningNumber")) {//网易
									winningNum = parse163API(response);
								}else if(response.contains("result")) {
									winningNum = parse168(response, "20"+ issueNum.replace("-", ""));
								}
							}
						}
					}
					
					if(!StringUtils.isBlank(winningNum)) {
						//store into to database
						issue = issueServ.getIssueByIssueNum(lottoType, issueNum);
						issue.setRetNum(winningNum.replaceAll(" ", ","));
						issue.setState(Constants.IssueState.LOTTO_DARW.getCode());
						issueServ.saveIssue(issue);
						
						if(bulletinBoard != null) {
							if(bulletinBoard.getLastIssue() != null) {
								Issue lastIssue = bulletinBoard.getLastIssue();
								if(lastIssue.getIssueNum().equals(issueNum)) {
									lastIssue.setRetNum(issue.getRetNum());
									cacheServ.setBulletinBoard(lottoType, bulletinBoard);												
								}
							}
						}
						//inform the progress to payout
						cacheServ.publishMessage(Constants.TOPIC_PAY_OUT, message);
						return;
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
		List awardNumberInfoList = null;
		Map winningNumMap = null;
		try {
			retItems = mapper.readValue(response, HashMap.class);
			awardNumberInfoList = (List)retItems.get("awardNumberInfoList");
			if(awardNumberInfoList != null && awardNumberInfoList.size() > 0) {
				winningNumMap = (Map)awardNumberInfoList.get(0);
				String winningNumber = (String)winningNumMap.get("winningNumber");
				return winningNumber;
			}
			
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
		Map  winningNumMap = null;
		List retItems = null;
		try {
			retItems = mapper.readValue(response, List.class);
			//awardNumberInfoList = (List)retItems.get("awardNumberInfoList");
			if(retItems != null && retItems.size() > 0) {
				winningNumMap = (Map)retItems.get(0);
				String winningNumber = (String)winningNumMap.get("code");
				return winningNumber;
			}
			
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
	
	private String parse168(String response, String issueNum) {
		ObjectMapper mapper = new ObjectMapper();
		Map  winningNumMap = null;
		Map responseMap = null;
		Map resultMap = null;
		Map dataMap = null;
		List retItems = null;
		try {
			responseMap = mapper.readValue(response, Map.class);
			resultMap = (Map)responseMap.get("result");
			retItems = (List)resultMap.get("data");
			
			//resultMap = (Map)responseMap.get("result");
			//retItems = (List)responseMap.get("data");
			if(retItems == null || retItems.size() == 0) {
				return null;
			}
			
			for(Object temp : retItems) {
				winningNumMap = (Map)temp;
				String winningNumber = (String)winningNumMap.get("preDrawCode");
				Long winningIssueNum = (Long)winningNumMap.get("preDrawIssue");
				
				if(issueNum.equals(String.valueOf(winningIssueNum))) {
					return winningNumber;
				}
			}
			
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
