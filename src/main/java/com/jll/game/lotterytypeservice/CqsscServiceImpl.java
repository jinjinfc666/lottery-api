package com.jll.game.lotterytypeservice;

import java.io.IOException;
import java.math.BigDecimal;
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
import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Constants;
import com.jll.common.constants.Constants.OrderDelayState;
import com.jll.common.constants.Constants.OrderState;
import com.jll.common.http.HttpRemoteStub;
import com.jll.entity.Issue;
import com.jll.entity.OrderInfo;
import com.jll.entity.PlayType;
import com.jll.entity.SysCode;
import com.jll.entity.UserAccount;
import com.jll.entity.UserAccountDetails;
import com.jll.entity.UserInfo;
import com.jll.game.IssueService;
import com.jll.game.LotteryTypeService;
import com.jll.game.order.OrderService;
import com.jll.game.playtype.PlayTypeFacade;
import com.jll.game.playtype.PlayTypeService;
import com.jll.game.playtypefacade.PlayTypeFactory;
import com.jll.spring.extend.SpringContextUtil;
import com.jll.user.UserInfoService;
import com.jll.user.details.UserAccountDetailsService;
import com.jll.user.wallet.WalletService;

/*@Configuration
@PropertySource("classpath:sys-setting.properties")*/
//@Service
//@Transactional
public class CqsscServiceImpl implements LotteryTypeService
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
								}
								
								if(!StringUtils.isBlank(winningNum)) {
									//store into to database
									issue = issueServ.getIssueByIssueNum(issueNum);
									issue.setRetNum(winningNum.replaceAll(" ", ","));
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
			
			//Map awardNumberInfoList = (Map)retItems.get("code");
			/*String winningNumber = (String)retItems.get("code");*/
			/*String winningNumber = null;
			return winningNumber;*/
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

	@Override
	public void payout(String issueNum) {
		/*String userName = SecurityContextHolder.getContext().getAuthentication().getName();*/
		UserInfo user = null;
		
		Issue issue = issueServ.getIssueByIssueNum(issueNum);
		List<OrderInfo> orders = null;
		boolean isMatch = false;
				
		if(issue == null) {
			return ;
		}
		
		if(issue.getState() != Constants.IssueState.END_ISSUE.getCode()) {
			return ;
		}
		
		if(StringUtils.isBlank(issue.getRetNum())) {
			return ;
		}
		
		orders = orderInfoServ.queryOrdersByIssue(issue.getId());
		
		if(orders == null || orders.size() == 0) {
			modifyIssueState(issue);
			return ;
		}
		
		for(OrderInfo order : orders) {
			user = userServ.getUserById(order.getUserId());
			//被取消的订单 或者延迟开奖的订单 跳过开奖
			if(order.getState() == Constants.OrderState.SYS_CANCEL.getCode()
					|| order.getState() == Constants.OrderState.USER_CANCEL.getCode()
					|| (order.getDelayPayoutFlag() != null 
							&& order.getDelayPayoutFlag() == OrderDelayState.DEPLAY.getCode())) {
				continue;
			}
			
			isMatch = isMatchWinningNum(issue, order);
			
			if(isMatch) {//赢
				//TODO 发奖金
				BigDecimal prize = calPrize(issue, order, user);
				//TODO 增加账户流水
				addUserAccountDetails(order, issue, prize);
				//TODO 修改用户余额
				modifyBal(order, user, prize);
				
				//TODO 修改订单状态
				modifyOrderState(order, Constants.OrderState.WINNING);
			}else {
				//TODO 修改订单状态
				modifyOrderState(order, Constants.OrderState.LOSTING);
			}
			
			//TODO
			rebate(issue, user, order);
			
		}
		
		modifyIssueState(issue);
	}

	private void modifyIssueState(Issue issue) {
		issue.setState(Constants.IssueState.PAYOUT.getCode());
		issueServ.saveIssue(issue);
	}

	private void rebate(Issue issue, UserInfo user, OrderInfo order) {
		String superior = user.getSuperior();
		BigDecimal prize = null;
		
		if(StringUtils.isBlank(superior) || "0".equals(superior)) {
			return ;
		}
		
		String[] superiors = superior.split(",");
		
		superior = superiors[0];
		UserInfo superiorUser = userServ.getUserById(Integer.parseInt(superior));
		prize = calRebate(user, order);
		addUserAccountDetails(order, issue, prize);
		
		modifyBal(order, user, prize);
		
		rebate(issue, superiorUser, order);
	}

	private BigDecimal calRebate(UserInfo user, OrderInfo order) {
		BigDecimal prize = null;
		BigDecimal prizeRate = user.getRebate();
		BigDecimal betAmount = new BigDecimal(order.getBetAmount());
		
		prize = betAmount.multiply(prizeRate);
		return prize;
	}

	private void modifyOrderState(OrderInfo order, OrderState orderState) {
		order.setState(orderState.getCode());
		
		orderInfoServ.saveOrder(order);
	}

	private void modifyBal(OrderInfo order, UserInfo user, BigDecimal prize) {
		BigDecimal bal = null;
		UserAccount wallet = walletServ.queryById(order.getWalletId());
		bal = wallet.getBalance().add(prize);
		wallet.setBalance(bal);
		
		walletServ.updateWallet(wallet);
	}

	private void addUserAccountDetails(OrderInfo order, Issue issue, BigDecimal prize) {
		UserAccount wallet = walletServ.queryById(order.getWalletId());
		UserAccountDetails accDetails = new UserAccountDetails();
		BigDecimal preAmount = null;
		BigDecimal postAmount = null;
		
		preAmount = wallet.getBalance();
		postAmount = preAmount.add(prize);
		accDetails.setAmount(prize.floatValue());
		accDetails.setCreateTime(new Date());
		accDetails.setDataItemType(Constants.DataItemType.BALANCE.getCode());
		accDetails.setOperationType(Constants.AccOperationType.PAYOUT.getDesc());
		accDetails.setOrderId(order.getId());
		accDetails.setPostAmount(postAmount.floatValue());
		accDetails.setPreAmount(preAmount.floatValue());
		accDetails.setUserId(order.getUserId());
		accDetails.setWalletId(order.getWalletId());
		accDetailsServ.saveAccDetails(accDetails);
	}

	private BigDecimal calPrize(Issue issue, OrderInfo order, UserInfo user) {
		PlayType playType = null;
		String facadeName = null;
		Integer playTypeId = order.getPlayType();
		playType = playTypeServ.queryById(playTypeId);
		if(playType == null) {
			return null;
		}
		
		//playType = playTypes.get(0);
		facadeName = playType.getClassification() +"/" + playType.getPtName();
		PlayTypeFacade playTypeFacade = PlayTypeFactory.getInstance().getPlayTypeFacade(facadeName);
		
		if(playTypeFacade == null) {
			return null;
		}
		
		return playTypeFacade.calPrize(issue, order, user);
	}

	private boolean isMatchWinningNum(Issue issue, OrderInfo order) {
		PlayType playType = null;
		String facadeName = null;
		Integer playTypeId = order.getPlayType();
		playType = playTypeServ.queryById(playTypeId);
		if(playType == null) {
			return false;
		}
		
		
		facadeName = playType.getClassification() +"/" + playType.getPtName();
		PlayTypeFacade playTypeFacade = PlayTypeFactory.getInstance().getPlayTypeFacade(facadeName);
		
		if(playTypeFacade == null) {
			return false;
		}
		
		return playTypeFacade.isMatchWinningNum(issue, order);
	}
	
	
	
}
