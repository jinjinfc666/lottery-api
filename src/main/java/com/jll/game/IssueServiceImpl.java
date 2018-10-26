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

import org.apache.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.druid.util.StringUtils;
import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Constants;
import com.jll.common.constants.Constants.AccOperationType;
import com.jll.common.constants.Constants.IssueState;
import com.jll.common.constants.Constants.OrderDelayState;
import com.jll.common.constants.Constants.OrderState;
import com.jll.common.constants.Constants.PrizeMode;
import com.jll.common.constants.Constants.SysCodeTypes;
import com.jll.common.constants.Message;
import com.jll.common.utils.Utils;
import com.jll.dao.SupserDao;
import com.jll.entity.Issue;
import com.jll.entity.OrderInfo;
import com.jll.entity.PlayType;
import com.jll.entity.SysCode;
import com.jll.entity.UserAccount;
import com.jll.entity.UserAccountDetails;
import com.jll.game.order.OrderDao;
import com.jll.game.playtype.PlayTypeDao;
import com.jll.user.details.UserAccountDetailsService;

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
				
				//查找出返点
				if(backPoint && OrderState.SYS_CANCEL.getCode() != order.getState().intValue()){
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
						qAcc.setBalance(new BigDecimal(addDtail.getPostAmount()));
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
					curAcc.setBalance(new BigDecimal(addDtail.getPostAmount()));
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
					UserAccountDetails addDtail = userAccountDetailsService.initCreidrRecord(curAcc.getUserId(),curAcc, curAcc.getBalance().doubleValue(), -prize, AccOperationType.REFUND.getCode(),order.getId());
					dtlLists.add(addDtail);
					curAcc.setBalance(new BigDecimal(addDtail.getPostAmount()));
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
		
		if(lottoAttri == null) {
			return false;
		}
		
		sysCodePrizeMode = cacheServ.getSysCode(lottoAttri.getCode(), 
				codeNamePrizeMode);
		
		type = Constants.LottoType.getFromCode(lottoType);
		if(type == null) {
			return false;
		}
		
		if(Constants.LottoType.CQSSC.getCode().equals(lottoType)
				|| Constants.LottoType.GD11X5.getCode().equals(lottoType)
				|| Constants.LottoType.BJPK10.getCode().equals(lottoType)) {
			return true;
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
}
