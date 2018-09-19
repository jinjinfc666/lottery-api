package com.jll.game;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import com.jll.common.constants.Message;
import com.jll.common.utils.Utils;
import com.jll.dao.SupserDao;
import com.jll.entity.Issue;
import com.jll.entity.OrderInfo;
import com.jll.entity.PlayType;
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
		if(StringUtils.isEmpty(openNum)){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return ret;
		}
		
		Issue curIssue = issueService.getIssueByIssueNum(Utils.toString(params.get("lottoType")),issueNum);
		if(null == curIssue){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_GAME_EXPIRED_ISSUE.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_GAME_EXPIRED_ISSUE.getErrorMes());
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
		calcelOrderWinAmtAndAccRecord(winLists,false);
		supserDao.updateList(winLists);
		
		cacheServ.publishMessage(Constants.TOPIC_PAY_OUT, issueNum);
		
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return ret;
	}

	private void calcelOrderWinAmtAndAccRecord(List<OrderInfo> winLists,boolean backPoint){
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
		
				DetachedCriteria criteria = DetachedCriteria.forClass(UserAccountDetails.class);
				criteria.add(Restrictions.eq("userId",order.getUserId()));
				criteria.add(Restrictions.eq("orderId",order.getId()));
				criteria.add(Restrictions.eq("walletId",order.getWalletId()));
				boolean isOk = true;
				if(!backPoint){
					if(order.getState() != OrderState.WINNING.getCode()){
						isOk = false;
					}
//					criteria.add(Restrictions.eq("dataItemType",Constants.DataItemType.BALANCE.getCode()));
					criteria.add(Restrictions.eq("operationType",Constants.AccOperationType.PAYOUT.getDesc()));
				}else{
					criteria.add(Restrictions.eq("operationType",Constants.AccOperationType.BETTING.getDesc()));
				}
				//收回盈利金额 或本金
				if(isOk){
					List<UserAccountDetails> ret = supserDao.findByCriteria(criteria);
					double prize = Utils.toDouble(ret.get(0).getAmount());
					UserAccountDetails addDtail1 = userAccountDetailsService.initCreidrRecord(curAcc.getUserId(),curAcc, curAcc.getBalance().doubleValue(), -prize, AccOperationType.RECOVERY_PAYOUT.getCode());
					if(addDtail1.getPostAmount() < 0){
						break;
					}else{
						order.setState(backPoint?OrderState.DISABLE.getCode():OrderState.RE_PAYOUT.getCode());
						dtlLists.add(addDtail1);
						curAcc.setBalance(new BigDecimal(addDtail1.getPostAmount()));
					}
				}
				
				//查找出返点
				if(backPoint){
					DetachedCriteria criteria2 = DetachedCriteria.forClass(UserAccountDetails.class);
					criteria2.add(Restrictions.eq("orderId",order.getId()));
//					criteria2.add(Restrictions.eq("dataItemType",Constants.DataItemType.BALANCE.getCode()));
					criteria2.add(Restrictions.eq("operationType",Constants.AccOperationType.REBATE.getDesc()));
					criteria2.add(Restrictions.eq("walletId",order.getWalletId()));
					
					List<UserAccountDetails> qryAccDtl = supserDao.findByCriteria(criteria2);
					for (UserAccountDetails qDtl : qryAccDtl) {
						UserAccount qAcc = accMaps.get(qDtl.getWalletId());
						if(null == qAcc){
							UserAccount acc = (UserAccount) supserDao.get(UserAccount.class, qDtl.getWalletId());
							accMaps.put(qDtl.getWalletId(), acc);
							qAcc = accMaps.get(qDtl.getWalletId());
						}
						//收回返点
						UserAccountDetails addDtail2 = userAccountDetailsService.initCreidrRecord(qAcc.getUserId(),qAcc, qAcc.getBalance().doubleValue(), -qDtl.getAmount().doubleValue(), AccOperationType.CANCEL_REBATE.getCode());
						if(addDtail2.getPostAmount() < 0){
							break;
						}else{
							dtlLists.add(addDtail2);
							qAcc.setBalance(new BigDecimal(addDtail2.getPostAmount()));
						}
					}
				}
			}
			supserDao.saveList(dtlLists);
			List<UserAccount> accList =  new ArrayList<>(accMaps.values());
			supserDao.updateList(accList);
		}
	}
	@Override
	public Map<String, Object> betOrderPayout(String issueNum, Map<String, String> params) {
		Map<String, Object> ret = new HashMap<String, Object>();
		Issue curIssue = issueService.getIssueByIssueNum(Utils.toString(params.get("lottoType")),issueNum);
		if(null == curIssue){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_GAME_EXPIRED_ISSUE.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_GAME_EXPIRED_ISSUE.getErrorMes());
			return ret;
		}
		cacheServ.publishMessage(Constants.TOPIC_PAY_OUT, issueNum);
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return ret;
	}

	@Override
	public Map<String, Object> calcelIssuePayout(String issueNum, Map<String, String> params) {
		Map<String, Object> ret = new HashMap<String, Object>();
		Issue curIssue = issueService.getIssueByIssueNum(Utils.toString(params.get("lottoType")),issueNum);
		if(null == curIssue){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_GAME_EXPIRED_ISSUE.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_GAME_EXPIRED_ISSUE.getErrorMes());
			return ret;
		}
		List<OrderInfo> winLists = orderDao.queryWinOrdersByIssue(curIssue.getId());
		calcelOrderWinAmtAndAccRecord(winLists,false);
		supserDao.updateList(winLists);
		
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return ret;
	}

	@Override
	public Map<String, Object> betOrderRePayout(String issueNum, Map<String, String> params) {
		Map<String, Object> ret = new HashMap<String, Object>();
		Issue curIssue = issueService.getIssueByIssueNum(Utils.toString(params.get("lottoType")),issueNum);
		if(null == curIssue){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_GAME_EXPIRED_ISSUE.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_GAME_EXPIRED_ISSUE.getErrorMes());
			return ret;
		}
		
		curIssue.setState(IssueState.RE_PAYOUT.getCode());
		supserDao.update(curIssue);
		
		cacheServ.publishMessage(Constants.TOPIC_PAY_OUT, issueNum);
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return ret;
	}

	@Override
	public Map<String, Object> issueDisbale(String issueNum, Map<String, String> params) {
		Map<String, Object> ret = new HashMap<String, Object>();
		Issue curIssue = issueService.getIssueByIssueNum(Utils.toString(params.get("lottoType")),issueNum);
		if(null == curIssue){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_GAME_EXPIRED_ISSUE.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_GAME_EXPIRED_ISSUE.getErrorMes());
			return ret;
		}
		
		curIssue.setState(IssueState.DISABLE.getCode());
		supserDao.update(curIssue);
		
		List<OrderInfo> winLists = orderDao.queryOrdersByIssue(curIssue.getId());
		calcelOrderWinAmtAndAccRecord(winLists,true);
		supserDao.updateList(winLists);
		
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return ret;
	}

	@Override
	public Map<String, Object> issueDelayePayout(String issueNum, Map<String, String> params) {
		Map<String, Object> ret = new HashMap<String, Object>();
		Issue curIssue = issueService.getIssueByIssueNum(Utils.toString(params.get("lottoType")),issueNum);
		if(null == curIssue){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_GAME_EXPIRED_ISSUE.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_GAME_EXPIRED_ISSUE.getErrorMes());
			return ret;
		}
		String userName = Utils.toString(params.get("userName"));
		String orderNum = Utils.toString(params.get("orderNum"));
		
		List<OrderInfo> orders = orderDao.getOrderInfoByPrams(curIssue.getId(), userName, orderNum,OrderDelayState.DEPLAY.getCode());
		
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
	public Map<String, Object> orderCancel(String orderNum) {
		Map<String, Object> ret = new HashMap<String, Object>();
		OrderInfo order = orderDao.getOrderInfo(orderNum);
		if(null == order){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_GAME_EXPIRED_ISSUE.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_GAME_EXPIRED_ISSUE.getErrorMes());
			return ret;
		}
		
		List<OrderInfo> winLists = new ArrayList<>();
		winLists.add(order);
		calcelOrderWinAmtAndAccRecord(winLists,false);
		calcelOrderWinAmtAndAccRecord(winLists,true);
		
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
	
	
}
