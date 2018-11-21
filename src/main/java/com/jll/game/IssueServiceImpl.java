package com.jll.game;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.aspectj.apache.bcel.generic.RET;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//import com.alibaba.druid.util.StringUtils;
import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Constants;
import com.jll.common.constants.Constants.AccOperationType;
import com.jll.common.constants.Constants.IssueState;
import com.jll.common.constants.Constants.OrderDelayState;
import com.jll.common.constants.Constants.OrderState;
import com.jll.common.constants.Constants.PrizeMode;
import com.jll.common.constants.Constants.SysCodeTypes;
import com.jll.common.constants.Constants.UserType;
import com.jll.common.constants.Message;
import com.jll.common.utils.Utils;
import com.jll.dao.PageBean;
import com.jll.dao.SupserDao;
import com.jll.entity.Issue;
import com.jll.entity.OrderInfo;
import com.jll.entity.PlayType;
import com.jll.entity.SysCode;
import com.jll.entity.UserAccount;
import com.jll.entity.UserAccountDetails;
import com.jll.entity.UserInfo;
import com.jll.game.order.OrderDao;
import com.jll.game.order.OrderService;
import com.jll.game.playtype.PlayTypeDao;
import com.jll.game.playtype.PlayTypeFacade;
import com.jll.game.playtype.PlayTypeService;
import com.jll.game.playtypefacade.PlayTypeFactory;
import com.jll.user.UserInfoService;
import com.jll.user.details.UserAccountDetailsService;
import com.jll.user.wallet.WalletService;

@Configuration
@PropertySource("classpath:sys-setting.properties")
@Service
@Transactional
public class IssueServiceImpl implements IssueService
{
	private Logger logger = Logger.getLogger(IssueServiceImpl.class);

	@Resource
	IssueDao issueDao;
	
	@Resource
	SupserDao supserDao;
	
	@Resource
	OrderDao orderDao;
	
	@Resource
	PlayTypeDao playTypeDao;
	
	@Resource
	CacheRedisService cacheServ;
	
	@Resource
	IssueService issueService;
	
	@Resource
	UserAccountDetailsService userAccountDetailsService;
	
	@Resource
	OrderService orderInfoServ;
	
	@Resource
	UserInfoService userServ;
	
	@Resource
	WalletService walletServ;
	
	@Resource
	PlayTypeService playTypeServ;
	
	@Resource
	UserAccountDetailsService accDetailsServ;
	
	@Value("${sys_lottery_type_impl}")
	private String lotteryTypeImpl;
	
	@Override
	public void savePlan(List<Issue> issues) {
		if(issues == null || issues.size() == 0) {
			return ;
		}
		
		issueDao.savePlan(issues);
	}

	@Override
	public void saveIssue(Issue currIssue) {
		issueDao.saveIssue(currIssue);
	}

	@Override
	public Issue getIssueById(Integer id) {
		return issueDao.getIssueById(id);
	}

	@Override
	public Issue getIssueByIssueNum(String lottoType, String issueNum) {
		return issueDao.getIssueByIssueNum(lottoType, issueNum);
	}
	
	//通过彩种和期次数量来查找
	@Override
	public List<Issue> queryByLTNumber(String lotteryType, Integer number) {
		List<Issue> issue=issueDao.queryByLTNumeber(lotteryType);
		if(issue!=null&&issue.size()>0) {
			List<Issue> issueList=issueDao.queryByLTNumber(lotteryType, issue.get(0).getStartTime(), number);
		  if(issueList!=null&& issueList.size()>0) {
			  return issueList;
		  }
		}
		return null;
	}

	@Override
	public Map<String, Object> updateIssueOpenNum(String issueNum, Map<String, String> params) {
		Map<String, Object> ret = new HashMap<String, Object>();
		String openNum = Utils.toString(params.get("openNum"));
		String lottoType = Utils.toString(params.get("lottoType"));
		if(StringUtils.isEmpty(openNum)
				|| StringUtils.isEmpty(lottoType)){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return ret;
		}
		
		Issue curIssue = issueService.getIssueByIssueNum(Utils.toString(params.get("lottoType")),issueNum);
		if(null == curIssue){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_ISSUE_INVALID_STATUS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_ISSUE_INVALID_STATUS.getErrorMes());
			return ret;
		}
		if(openNum.equals(curIssue.getRetNum())){
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			return ret;
		}
		
		curIssue.setRetNum(openNum);
		curIssue.setState(IssueState.RE_PAYOUT.getCode());
		supserDao.update(curIssue);
		
		List<OrderInfo> winLists = orderDao.queryWinOrdersByIssue(curIssue.getId());
		//processCalcelOrderWinAmtAndAccRecord(winLists,false,true,false);
		supserDao.updateList(winLists);
		
		cacheServ.publishMessage(Constants.TOPIC_PAY_OUT, issueNum);
		
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return ret;
	}
	
	@Override
	public void processCalcelOrderWinAmtAndAccRecord(List<OrderInfo> winLists,boolean backBetAmt,boolean backWinAmt,boolean backPoint,OrderState state){
		if(!winLists.isEmpty()){
			
			Map<Integer,UserAccount> accMaps = new HashMap<>();
			List<UserAccountDetails> dtlLists = new ArrayList<>();
			for (OrderInfo order : winLists) {
				
				UserAccount curAcc = accMaps.get(order.getWalletId());
				if(null == curAcc){
					UserAccount acc = (UserAccount) supserDao.get(UserAccount.class, order.getWalletId());
					accMaps.put(order.getWalletId(), acc);
					curAcc = accMaps.get(order.getWalletId());
				}
				if(OrderState.SYS_CANCEL.getCode() == order.getState().intValue()){
					continue;
				}
				//查找出返点
				if(backPoint){
					DetachedCriteria criteria2 = DetachedCriteria.forClass(UserAccountDetails.class);
					criteria2.add(Restrictions.eq("orderId",order.getId()));
					criteria2.add(Restrictions.eq("operationType",Constants.AccOperationType.REBATE.getCode()));
					
					List<UserAccountDetails> qryAccDtl = supserDao.findByCriteria(criteria2);
					for (UserAccountDetails qDtl : qryAccDtl) {
						UserAccount qAcc = accMaps.get(qDtl.getWalletId());
						if(null == qAcc){
							UserAccount acc = (UserAccount) supserDao.get(UserAccount.class, qDtl.getWalletId());
							accMaps.put(qDtl.getWalletId(), acc);
							qAcc = accMaps.get(qDtl.getWalletId());
						}
						//收回返点
						UserAccountDetails addDtail = userAccountDetailsService.initCreidrRecord(qAcc.getUserId(),qAcc, qAcc.getBalance().doubleValue(), -qDtl.getAmount().doubleValue(), AccOperationType.CANCEL_REBATE.getCode(),order.getId());
						dtlLists.add(addDtail);
						qAcc.setBalance(addDtail.getPostAmount());
					}
				}
				
				//回收本金
				if(backBetAmt){
					DetachedCriteria criteria = DetachedCriteria.forClass(UserAccountDetails.class);
					criteria.add(Restrictions.eq("userId",order.getUserId()));
					criteria.add(Restrictions.eq("orderId",order.getId()));
					criteria.add(Restrictions.eq("walletId",order.getWalletId()));
					criteria.add(Restrictions.eq("operationType",Constants.AccOperationType.BETTING.getCode()));
					
					List<UserAccountDetails> ret = supserDao.findByCriteria(criteria);
					double prize = Utils.toDouble(ret.get(0).getAmount());
					UserAccountDetails addDtail = userAccountDetailsService.initCreidrRecord(curAcc.getUserId(),curAcc, curAcc.getBalance().doubleValue(), prize, AccOperationType.REFUND.getCode(),order.getId());
					dtlLists.add(addDtail);
					curAcc.setBalance(addDtail.getPostAmount());
				}
				
				//回收盈利金额
				if(backWinAmt && order.getState().intValue() ==  OrderState.WINNING.getCode()){
					DetachedCriteria criteria = DetachedCriteria.forClass(UserAccountDetails.class);
					criteria.add(Restrictions.eq("userId",order.getUserId()));
					criteria.add(Restrictions.eq("orderId",order.getId()));
					criteria.add(Restrictions.eq("walletId",order.getWalletId()));
					criteria.add(Restrictions.eq("operationType",Constants.AccOperationType.PAYOUT.getCode()));
					
					List<UserAccountDetails> ret = supserDao.findByCriteria(criteria);
					double prize = Utils.toDouble(ret.get(0).getAmount());
					UserAccountDetails addDtail = userAccountDetailsService.initCreidrRecord(curAcc.getUserId(),curAcc, curAcc.getBalance().doubleValue(), -prize, AccOperationType.RECOVERY_PAYOUT.getCode(),order.getId());
					dtlLists.add(addDtail);
					curAcc.setBalance(addDtail.getPostAmount());
				}
				order.setState(state.getCode());
			}
			supserDao.saveList(dtlLists);
			List<UserAccount> accList =  new ArrayList<>(accMaps.values());
			supserDao.updateList(accList);
		}
	}
	@Override
	public Map<String, Object> processBetOrderPayout(String lottoType, String issueNum, Map<String, String> params) {
		Map<String, Object> ret = new HashMap<String, Object>();
		Issue curIssue = issueService.getIssueByIssueNum(Utils.toString(params.get("lottoType")),issueNum);
		if(null == curIssue){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_ISSUE_INVALID_STATUS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_ISSUE_INVALID_STATUS.getErrorMes());
			return ret;
		}
		final String message = Utils.toString(params.get("lottoType")) + "|" + issueNum + "|" + curIssue.getRetNum();
		cacheServ.publishMessage(Constants.TOPIC_PAY_OUT, message);
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return ret;
	}

	@Override
	public Map<String, Object> processCalcelIssuePayout(String lottoType, String issueNum, Map<String, String> params) {
		Map<String, Object> ret = new HashMap<String, Object>();
		Issue curIssue = issueService.getIssueByIssueNum(Utils.toString(params.get("lottoType")),issueNum);
		if(null == curIssue
				|| (IssueState.PAYOUT.getCode() != curIssue.getState() 
				&& IssueState.RE_PAYOUT.getCode() != curIssue.getState())){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_ISSUE_INVALID_STATUS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_ISSUE_INVALID_STATUS.getErrorMes());
			return ret;
		}
		List<OrderInfo> winLists = orderDao.queryOrdersByIssue(curIssue.getId());
		processCalcelOrderWinAmtAndAccRecord(winLists,false,true,false,OrderState.RE_PAYOUT);
		supserDao.updateList(winLists);
		curIssue.setState(IssueState.LOTTO_DARW.getCode());
		
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return ret;
	}

	@Override
	public Map<String, Object> processBetOrderRePayout(String lottoType, String issueNum, Map<String, String> params) {
		Map<String, Object> ret = new HashMap<String, Object>();
		Map<String, Object> retCancel = processCalcelIssuePayout(lottoType, issueNum, params);
		if(!retCancel.get(Message.KEY_STATUS).equals( Message.status.SUCCESS.getCode())){
			return retCancel;
		}
		Issue curIssue = issueService.getIssueByIssueNum(Utils.toString(params.get("lottoType")),issueNum);
		curIssue.setState(IssueState.RE_PAYOUT.getCode());
		supserDao.update(curIssue);
		
		final String message = Utils.toString(params.get("lottoType")) + "|" + issueNum + "|" + curIssue.getRetNum();
		cacheServ.publishMessage(Constants.TOPIC_PAY_OUT, message);
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return ret;
	}

	@Override
	public Map<String, Object> updateIssueDisbale(String lottoType, String issueNum, Map<String, String> params) {
		Map<String, Object> ret = new HashMap<String, Object>();
		Issue curIssue = issueService.getIssueByIssueNum(Utils.toString(params.get("lottoType")),issueNum);
		if(null == curIssue
				|| IssueState.DISABLE.getCode() == curIssue.getState()){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_ISSUE_INVALID_STATUS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_ISSUE_INVALID_STATUS.getErrorMes());
			return ret;
		}
		
		curIssue.setState(IssueState.DISABLE.getCode());
		supserDao.update(curIssue);
		
		List<OrderInfo> winLists = orderDao.queryOrdersByIssue(curIssue.getId());
		processCalcelOrderWinAmtAndAccRecord(winLists,true,true,true,OrderState.SYS_CANCEL);
		supserDao.updateList(winLists);
		
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return ret;
	}

	@Override
	public Map<String, Object> processIssueDelayePayout(String issueNum, Map<String, String> params) {
		Map<String, Object> ret = new HashMap<String, Object>();

		Issue curIssue = issueService.getIssueByIssueNum(Utils.toString(params.get("lottoType")),issueNum);
		if(null == curIssue){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_ISSUE_INVALID_STATUS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_ISSUE_INVALID_STATUS.getErrorMes());
			return ret;
		}
		String userName = Utils.toString(params.get("userName"));
		String orderNum = Utils.toString(params.get("orderNum"));
		
		List<OrderInfo> orders = orderDao.getOrderInfoByPrams(curIssue.getId(), userName, orderNum,0);
		for (OrderInfo orderInfo : orders) {
			orderInfo.setDelayPayoutFlag(OrderDelayState.DEPLAY.getCode());
		}
		
		supserDao.updateList(orders);
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return ret;
	}
	
	private boolean isOKAdminCancelOrder(OrderInfo order){
		if(OrderState.WINNING.getCode() == order.getState().intValue()
				|| OrderState.LOSTING.getCode() == order.getState().intValue()
				|| OrderState.WAITTING_PAYOUT.getCode() == order.getState().intValue()){
			return true;
		}
		return false;
	}

	@Override
	public Map<String, Object> processOrderCancel(String orderNum) {
		Map<String, Object> ret = new HashMap<String, Object>();
		OrderInfo order = orderDao.getOrderInfo(orderNum);
		if(null == order
				|| !isOKAdminCancelOrder(order)){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_PAYMENT_TLCLOUD_FAILED_CANCEL_ORDER.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_PAYMENT_TLCLOUD_FAILED_CANCEL_ORDER.getErrorMes());
			return ret;
		}
		
		List<OrderInfo> winLists = new ArrayList<>();
		winLists.add(order);
		processCalcelOrderWinAmtAndAccRecord(winLists,true,true,true,OrderState.SYS_CANCEL);
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return ret;
	}

	@Override
	public Map<String, Object> manualPayoutOrder(String orderNum) {
		Map<String, Object> ret = new HashMap<String, Object>();
		
		List<OrderInfo> orders = orderDao.getOrderInfoByPrams(-1, null, orderNum,-1);
		Map<String,LotteryTypeService> curSer = new HashMap<>();
		String[] impls = lotteryTypeImpl.split(",");
		for(String impl : impls) {
			LotteryTypeService lotteryTypeServ = LotteryTypeFactory
					.getInstance().createLotteryType(impl);
			if(lotteryTypeServ != null){
				curSer.put(lotteryTypeServ.getLotteryType(), lotteryTypeServ);
			}
		}
		for (OrderInfo order : orders) {
			PlayType play = playTypeDao.queryById(order.getPlayType());
			LotteryTypeService runSer = curSer.get(play.getLotteryType());
			if(runSer != null){
				runSer.payout(order, null, false);
			}
		}
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return ret;
	}

	@Override
	public String manualDrawResult(String lottoType, 
			String issueNum, 
			String winningNum) {
		
		final String message = lottoType + "|" + issueNum + "|" + winningNum;
		
		cacheServ.publishMessage(Constants.TOPIC_WINNING_NUMBER, 
				message);
		
		return Integer.toString(Message.status.SUCCESS.getCode());
	}

	@Override
	public boolean isManualPrieModel(String lottoType, String issueNum) {
		String codeNamePrizeMode = Constants.LotteryAttributes.PRIZE_MODE.getCode();
		SysCodeTypes lottoAttri = Constants.SysCodeTypes.getLottoAttriFromLottoTye(lottoType);
		SysCode sysCodePrizeMode = null;
		Constants.LottoType type = null;
		Issue issue = null;
		Date currDate = new Date();
		
		if(lottoAttri == null) {
			return false;
		}
		
		sysCodePrizeMode = cacheServ.getSysCode(lottoAttri.getCode(), 
				codeNamePrizeMode);
		
		type = Constants.LottoType.getFromCode(lottoType);
		if(type == null) {
			return false;
		}
		
		issue = issueDao.getIssueByIssueNum(lottoType, issueNum);
		
		if(Constants.LottoType.CQSSC.getCode().equals(lottoType)
				|| Constants.LottoType.GD11X5.getCode().equals(lottoType)
				|| Constants.LottoType.BJPK10.getCode().equals(lottoType)) {
			long inernerl = (currDate.getTime() - issue.getEndTime().getTime())/1000;
			//×××××××××××××××××××××××××××××××××/
			//TODO 需要取消，这里为了测试
			//抓取数据会持续30分钟，所以等待30分钟之后才可以人工开奖
			/*if(inernerl > 1800) {
				return true;				
			}*/
			
			//TODO 需要取消，这里为了测试
			return true;
			
			//×××××××××××××××××××××××××××××××××/
		}
		
		//对于私彩需要验证开奖模式
		if(sysCodePrizeMode == null
				|| (!sysCodePrizeMode.getCodeVal()
					.equals(PrizeMode.MANUAL.getCode()))) {
			return false;
		}
		
		
		return true;
	}

	@Override
	public Map<String, Object> queryAllByIssue(String lotteryType, Integer state,String startTime, String endTime, Integer pageIndex,
			Integer pageSize, String issueNum) {
		String codeTypeName=Constants.SysCodeTypes.LOTTERY_TYPES.getCode();
		Map<String, SysCode> sysCodes=cacheServ.getSysCode(codeTypeName);
		return issueDao.queryAllByIssue(lotteryType, state,startTime, endTime, pageIndex, pageSize, issueNum,sysCodes);
	}
	//追号需要的期号信息
	@Override
	public Map<String, Object> queryIsZhIssue(String lotteryType) {
		Map<String,Object> map=new HashMap<String,Object>();
		Calendar ca = Calendar.getInstance();// 得到一个Calendar的实例  
	    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");  
	    ca.setTime(new Date()); // 设置时间为当前时间  
	    Date resultDate = ca.getTime(); // 结果   
	    
	    String endTime=sdf1.format(resultDate)+" 23:59:59"; 
	    
	    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
	    String startTime=sdf2.format(resultDate);//当前时间  时分秒
	    Date beginDate =null;
		Date endDate=null;
		try {
			beginDate = (Date) sdf2.parse(startTime);
			endDate = (Date) sdf2.parse(endTime); 
		}catch(ParseException  ex) {
			
		}
	    List<Issue> issueList=issueDao.queryIsZhIssue(lotteryType, beginDate, endDate);
	    map.put(Message.KEY_DATA, issueList);
		return map;
	}

	@Override
	public void payOutIssue(Issue issue) {
		List<OrderInfo> orders = null;
		List<Object> params = new ArrayList<>();
		PageBean<OrderInfo> page = new PageBean<>();
		page.setPageIndex(0);
		page.setPageSize(1000);
		
		params.add(issue.getId());
		page.setParams(params);
		
		page = orderInfoServ.queryOrdersByPage(page);
		orders = page.getContent();
		
		if(orders == null || orders.size() == 0) {
			modifyIssueState(issue);
			return ;
		}
		
		while(page.getPageIndex() < page.getTotalPages()) {
			orders = page.getContent();
			
			for(OrderInfo order : orders) {
				payoutOrder(order, issue, true);
			}
			
			page.setPageIndex(page.getPageIndex()+1);
			page = orderInfoServ.queryOrdersByPage(page);
		}
		
		modifyIssueState(issue);
	}
	
	private void modifyIssueState(Issue issue) {
		if(issue.getState().intValue() != Constants.IssueState.RE_PAYOUT.getCode()){
			issue.setState(Constants.IssueState.PAYOUT.getCode());
		}
		saveIssue(issue);
	}
	
	
	public void payoutOrder(OrderInfo order, Issue issue, boolean isAuto) {
		if(issue == null){
			issue = getIssueById(order.getIssueId());
		}
		 
		UserInfo user = userServ.getUserById(order.getUserId());
		Integer walletId = order.getWalletId();
		UserAccount wallet = walletServ.queryById(walletId);
		List<OrderInfo> zhOrders = null;
		
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
		
		//试玩用户或者重新派奖的状态下跳过返点 
		if(wallet.getAccType().intValue() != Constants.WalletType.RED_PACKET_WALLET.getCode()
				&& UserType.DEMO_PLAYER.getCode() != user.getUserType().intValue()
				&& order.getState().intValue() != Constants.OrderState.RE_PAYOUT.getCode()){
			rebate(issue, user, order);
		}
		
		boolean isMatch = isMatchWinningNum(issue, order);
		
		if(isMatch) {//赢
			//TODO 发奖金
			Map<String, Object> ret = calPrize(issue, order, user);
			BigDecimal prize = new BigDecimal((Float)ret.get(Constants.KEY_WIN_AMOUNT));
			//试玩用户跳过派奖
			if(UserType.DEMO_PLAYER.getCode() != user.getUserType()){
				//TODO 增加账户流水
				addUserAccountDetails(order, user, issue, prize, Constants.AccOperationType.PAYOUT);
				//TODO 修改用户余额
				modifyBal(order, user, prize);
				
				//追号是否停止
				if(order.getIsZh().intValue() == Constants.ZhState.ZH.getCode()
						&& order.getIsZhBlock().intValue() 
								== Constants.ZhBlockState.BLOCK.getCode()) {
					zhOrders = orderInfoServ.queryZhOrder(order.getZhTrasactionNum());
					if(zhOrders != null) {
						for(OrderInfo temp : zhOrders) {
							if(temp.getState().intValue() == Constants.OrderState.WAITTING_PAYOUT.getCode()
									&& temp.getId().intValue() != order.getId().intValue()) {
								processOrderCancel(temp.getOrderNum());
							}
						}
					}
				}
			}
			
			//TODO 修改订单状态
			modifyOrderState(order, Constants.OrderState.WINNING, ret);
		}else {
			//TODO 修改订单状态
			modifyOrderState(order, Constants.OrderState.LOSTING, null);
		}
		
	}
	
	private boolean isMatchWinningNum(Issue issue, OrderInfo order) {
		PlayType playType = null;
		String playTypeName = null;
		PlayTypeFacade playTypeFacade = null;
		
		Integer playTypeId = order.getPlayType();
		playType = playTypeServ.queryById(playTypeId);
		if(playType == null) {
			return false;
		}
				
		if(playType.getPtName().equals("fs")) {
			playTypeName = playType.getClassification() + "/fs";
		}else if(playType.getPtName().equals("ds")){
			playTypeName = playType.getClassification() + "/ds";
		}else {
			playTypeName = playType.getClassification() + "/" + playType.getPtName();
		}
		playTypeFacade = PlayTypeFactory.getInstance().getPlayTypeFacade(playTypeName);
		
		if(playTypeFacade == null) {
			return false;
		}
		
		return playTypeFacade.isMatchWinningNum(issue, order);
	}
	
	private Map<String, Object> calPrize(Issue issue, OrderInfo order, UserInfo user) {
		String playTypeName = null;
		PlayTypeFacade playTypeFacade = null;
		PlayType playType = null;
		Integer playTypeId = order.getPlayType();
		playType = playTypeServ.queryById(playTypeId);
		if(playType == null) {
			return null;
		}
		
		if(playType.getPtName().equals("fs")) {
			playTypeName = playType.getClassification() + "/fs";
		}else if(playType.getPtName().equals("ds")){
			playTypeName = playType.getClassification() + "/ds";
		}else {
			playTypeName = playType.getClassification() + "/" + playType.getPtName();
		}
		playTypeFacade = PlayTypeFactory.getInstance().getPlayTypeFacade(playTypeName);
		
		if(playTypeFacade == null) {
			return null;
		}
		
		Map<String, Object> ret = playTypeFacade.calPrize(issue, order, user);
		
		return ret;
	}
	
	private void modifyBal(OrderInfo order, UserInfo user, BigDecimal prize) {
		BigDecimal bal = null;
		Integer walletType = null;
		UserAccount wallet = walletServ.queryById(order.getWalletId());
		walletType = wallet.getAccType();
		wallet = walletServ.queryUserAccount(user.getId(), walletType);
		bal = new BigDecimal(wallet.getBalance()).add(prize);
		wallet.setBalance(bal.doubleValue());
		
		walletServ.updateWallet(wallet);
	}

	private void addUserAccountDetails(OrderInfo order, UserInfo user, 
			Issue issue, BigDecimal prize, 
			Constants.AccOperationType opeType) {
		UserAccount wallet = walletServ.queryById(order.getWalletId());
		wallet = walletServ.queryUserAccount(user.getId(), wallet.getAccType());
		UserAccountDetails accDetails = new UserAccountDetails();
		BigDecimal preAmount = null;
		BigDecimal postAmount = null;
		preAmount = new BigDecimal(wallet.getBalance());
		logger.debug(String.format("prize %s,  preAmount   %s", 
				(prize == null?"":prize.floatValue()), 
				(preAmount == null?"":preAmount.floatValue())));
		postAmount = preAmount.add(prize);
		accDetails.setAmount(prize.floatValue());
		accDetails.setCreateTime(new Date());
		accDetails.setDataItemType(Constants.DataItemType.BALANCE.getCode());
		accDetails.setOperationType(opeType.getCode());
		accDetails.setOrderId(order.getId());
		accDetails.setPostAmount(postAmount.doubleValue());
		accDetails.setPreAmount(preAmount.doubleValue());
		accDetails.setUserId(user.getId());
		accDetails.setWalletId(wallet.getId());
		accDetailsServ.saveAccDetails(accDetails);
	}
	
	private void modifyOrderState(OrderInfo order, OrderState orderState, Map<String, Object> payOutInfo) {
		order.setState(orderState.getCode());
		if(payOutInfo != null && payOutInfo.size() > 0) {
			Float winAmount = (Float)payOutInfo.get(Constants.KEY_WIN_AMOUNT);
			Integer winningBetTotal = (Integer)payOutInfo.get(Constants.KEY_WINNING_BET_TOTAL);
			
			order.setWinAmount(winAmount);
			order.setWinBetTotal(winningBetTotal.floatValue());
		}
		orderInfoServ.saveOrder(order);
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
		
		if(prize == null 
				|| prize.compareTo(new BigDecimal(0)) == 0) {
			return ;
		}
		
		addUserAccountDetails(order, superiorUser, issue, prize, 
				Constants.AccOperationType.REBATE);
		
		modifyBal(order, superiorUser, prize);
		
		rebate(issue, superiorUser, order);
	}

	private BigDecimal calRebate(UserInfo user, OrderInfo order) {
		BigDecimal rebate = null;
		BigDecimal rebateRate = user.getRebate();
		rebateRate = rebateRate.multiply(new BigDecimal(0.01F));
		BigDecimal betAmount = new BigDecimal(order.getBetAmount());
		
		rebate = betAmount.multiply(rebateRate);
		return rebate;
	}
	//近期注单--------------会查询出近30个订单
	@Override
	public Map<String, Object> queryNear(String lotteryType, String userName) {
		Map<String,Object> map=new HashMap();
		UserInfo userInfo=userServ.getUserByUserName(userName);
		if(userInfo==null) {
			map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_NO_VALID_USER.getCode());
			map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_NO_VALID_USER.getErrorMes());
		}else {
			String codeTypeName=Constants.SysCodeTypes.LOTTERY_TYPES.getCode();
			SysCode sysCode=cacheServ.getSysCode(codeTypeName,codeTypeName);
			Integer codeTypeNameId=sysCode.getId();
			Integer userId=userInfo.getId();
			map=issueDao.queryNear(lotteryType,codeTypeNameId,userId);
		}
		return map;
	}
	//未结算的注单 --------------会查询出近30个订单
	@Override
	public Map<String, Object> queryUnsettlement(String lotteryType, String userName) {
		Map<String,Object> map=new HashMap();
		UserInfo userInfo=userServ.getUserByUserName(userName);
		if(userInfo==null) {
			map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_NO_VALID_USER.getCode());
			map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_NO_VALID_USER.getErrorMes());
		}else {
			String codeTypeName=Constants.SysCodeTypes.LOTTERY_TYPES.getCode();
			SysCode sysCode=cacheServ.getSysCode(codeTypeName,codeTypeName);
			Integer codeTypeNameId=sysCode.getId();
			Integer userId=userInfo.getId();
			map=issueDao.queryUnsettlement(lotteryType,codeTypeNameId,userId);
		}
		return map;
	}
	
}
