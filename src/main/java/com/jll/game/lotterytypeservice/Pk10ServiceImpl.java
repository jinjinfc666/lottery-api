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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Constants;
import com.jll.common.constants.Constants.OrderDelayState;
import com.jll.common.constants.Constants.OrderState;
import com.jll.common.http.HttpRemoteStub;
import com.jll.common.utils.sequence.GenSequenceService;
import com.jll.entity.GenSequence;
import com.jll.entity.Issue;
import com.jll.entity.OrderInfo;
import com.jll.entity.PlayType;
import com.jll.entity.SysCode;
import com.jll.entity.UserAccount;
import com.jll.entity.UserAccountDetails;
import com.jll.entity.UserInfo;
import com.jll.game.BulletinBoard;
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
public class Pk10ServiceImpl extends DefaultLottoTypeServiceImpl
{
	private Logger logger = Logger.getLogger(Pk10ServiceImpl.class);

	private final String lotteryType = "bjpk10";
	
	IssueService issueServ = (IssueService)SpringContextUtil.getBean("issueServiceImpl");
	
	CacheRedisService cacheServ = (CacheRedisService)SpringContextUtil.getBean("cacheRedisServiceImpl");
	
	WalletService walletServ = (WalletService)SpringContextUtil.getBean("walletServiceImpl");
	
	OrderService orderInfoServ = (OrderService)SpringContextUtil.getBean("orderServiceImpl");
	
	PlayTypeService playTypeServ = (PlayTypeService)SpringContextUtil.getBean("playTypeServiceImpl");
	
	UserAccountDetailsService accDetailsServ = (UserAccountDetailsService)SpringContextUtil.getBean("userAccountDetailsServiceImpl");
	
	UserInfoService userServ = (UserInfoService)SpringContextUtil.getBean("userInfoServiceImpl");
	
	GenSequenceService seqServ = (GenSequenceService)SpringContextUtil.getBean("genSequenceServiceImpl");
	
	@Override
	public List<Issue> makeAPlan() {
		//09:00-23:00（179期）5分钟一期，
		List<Issue> issues = new ArrayList<>();
		int maxAmount = 179;
		Calendar calendar = Calendar.getInstance();
		GenSequence pk10Seq = seqServ.queryPK10SeqVal();
		
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR_OF_DAY, 9);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		for(int i = 0; i < maxAmount; i++) {
			Long seqVal = pk10Seq.getSeqVal().longValue() + 1;
			pk10Seq.setSeqVal(seqVal);
			//if(i < 72) {
				Issue issue = new Issue();
				issue.setStartTime(calendar.getTime());
				calendar.add(Calendar.MINUTE, 5);
				issue.setEndTime(calendar.getTime());
				issue.setIssueNum(String.valueOf(pk10Seq.getSeqVal().longValue()));
				issue.setLotteryType(lotteryType);
				issue.setState(Constants.IssueState.INIT.getCode());
				
				issues.add(issue);
			/*}else{
				Issue issue = new Issue();
				issue.setStartTime(calendar.getTime());
				calendar.add(Calendar.MINUTE, 5);
				issue.setEndTime(calendar.getTime());
				issue.setIssueNum(generateLottoNumber(i + 1));
				issue.setLotteryType(lotteryType);
				issue.setState(Constants.IssueState.INIT.getCode());
				
				issues.add(issue);
			}*/
		}
		
		seqServ.saveSeq(pk10Seq);
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
		String codeTypeName = Constants.SysCodeTypes.LOTTERY_CONFIG_BJPK10.getCode();
		String codeName = Constants.LotteryAttributes.URL_WINING_NUMBER_EXTENAL.getCode();
		SysCode sysCode = cacheServ.getSysCode(codeTypeName, codeName);
		String winningNum = null;
		Issue issue = null;
		int maxCounter = 3600;
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
							//if(response.contains(issueNum.replace("-", ""))) {
								if(response.contains("preDrawCode")) {
									winningNum = parseHuiling(response, issueNum);
								}else if(response.contains("openNum")) {
									winningNum = parseCp8033(response, issueNum);
								}
								
								if(!StringUtils.isBlank(winningNum)) {
									//store into to database
									issue = issueServ.getIssueByIssueNum(lottoType, issueNum);
									issue.setRetNum(winningNum.replaceAll(" ", ","));
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
									cacheServ.publishMessage(Constants.TOPIC_PAY_OUT, issueNum);
									return;								
								}
							//}
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private String parseCp8033(String response, String issueNum) {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> retItems = null;
		List winningNumberSet = null;
		Map winningNumMap = null;
		StringBuffer winningNum = new StringBuffer();
		
		try {
			retItems = mapper.readValue(response, HashMap.class);
			winningNumberSet = (List)retItems.get("openNum");
			String winningIssueNum = (String)retItems.get("issue");
			
			if(winningNumberSet == null 
					|| winningNumberSet.size() == 0) {
				return null;
			}
			
			if(!issueNum.equals(winningIssueNum)) {
				return null;
			}
			
			for(Object bit : winningNumberSet) {
				winningNum.append(((Integer)bit).toString()).append(",");
			}
			
			if(winningNum.toString().endsWith(",")) {
				winningNum.delete(winningNum.length() - 1, winningNum.length() + 1);
			}
			
			return winningNum.toString();
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

	private String parseHuiling(String response, String issueNum) {
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
			if(retItems == null || retItems.size() == 0) {
				return null;
			}
			
			for(Object temp : retItems) {
				winningNumMap = (Map)temp;
				String winningNumber = (String)winningNumMap.get("preDrawCode");
				Integer winningIssueNum = (Integer)winningNumMap.get("preDrawIssue");
				
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

	/*@Override
	public void payout(String issueNum) {
		String userName = SecurityContextHolder.getContext().getAuthentication().getName();
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
			payout(order, issue, true);
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

	@Override
	public void payout(OrderInfo order, Issue issue, boolean isAuto) {

		if(issue == null){
			issue = issueServ.getIssueById(order.getIssueId());
		}
		 
		UserInfo user = userServ.getUserById(order.getUserId());
		//被取消的订单 或者延迟开奖的订单,或者已经开奖 跳过开奖
		if(order.getState() == Constants.OrderState.SYS_CANCEL.getCode()
				||	order.getState() == Constants.OrderState.WINNING.getCode()
				|| order.getState() == Constants.OrderState.LOSTING.getCode()
				|| order.getState() == Constants.OrderState.USER_CANCEL.getCode()
				|| (order.getDelayPayoutFlag() != null 
						&& order.getDelayPayoutFlag() == OrderDelayState.DEPLAY.getCode()
						&& isAuto)) {
			return;
		}
		
		boolean isMatch = isMatchWinningNum(issue, order);
		
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
		if(order.getState() != Constants.OrderState.RE_PAYOUT.getCode()){
			rebate(issue, user, order);
		}
	}
	
	*/
}
