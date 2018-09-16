package com.jll.report;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.constants.Constants;
import com.jll.common.constants.Constants.DepositOrderState;
import com.jll.common.constants.Constants.WalletType;
import com.jll.common.constants.Message;
import com.jll.common.utils.Utils;
import com.jll.dao.SupserDao;
import com.jll.entity.DepositApplication;
import com.jll.entity.UserAccount;
import com.jll.entity.UserAccountDetails;
import com.jll.user.details.UserAccountDetailsService;




@Service
@Transactional
public class DepositApplicationServiceImpl implements DepositApplicationService {
	@Resource
	UserAccountDetailsService userAccountDetailsService;
	
	@Resource
	DepositApplicationDao depositApplicationDao;
	
	@Resource
	SupserDao supserDao;
//	@Override
//	public List<?> queryDetails(Map<String,Object> ret){
//		String userName=(String) ret.get("userName");
//		String orderNum=(String) ret.get("orderNum");
//		String startTime=(String) ret.get("startTime");
//		String endTime=(String) ret.get("endTime");
//		return depositApplicationDao.queryDetails(userName,orderNum,startTime,endTime);
//	}
	@Override
	public Map<String,Object> updateState(Map<String, Object> ret) {
		Map<String,Object> map=new HashMap<String,Object>();
		Integer id=(Integer) ret.get("id");
		Integer state=(Integer) ret.get("state");
		boolean isNo=this.isNullById(id);
		if(isNo) {
			depositApplicationDao.updateState(id, state);
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		}else {
			map.clear();
			map.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			map.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			map.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return map;
	}
	//判断是否存在
	@Override
	public boolean isNullById(Integer id) {
		List<DepositApplication> list=depositApplicationDao.queryById(id);
		if(list!=null&&list.size()>0) {
			return true;
		}
		return false;
	}
	@Override
	public Map<String, Object> getDepositInfoByOrderNum(String orderNum) {
		Map<String, Object> ret = new HashMap<>();
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put("data",depositApplicationDao.getDepositInfoByOrderNum(orderNum));
		return ret;
	}
	@Override
	public Map<String, Object> addAmountByDepositOrder(DepositApplication dep) {
		Map<String, Object> ret = new HashMap<>();
		DepositApplication dbDep = depositApplicationDao.getDepositInfoByOrderNum(dep.getOrderNum());
		
		if(null == dbDep
				|| Utils.toDouble(dep.getAmount()) > Utils.toDouble(dbDep.getAmount())){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_ORDER_ERROR.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_ORDER_ERROR.getErrorMes());
			return ret;
		}
		
		dbDep.setUpdateTime(new Date());
		dbDep.setState(DepositOrderState.END_ORDER.getCode());
		dbDep.setRemark(Utils.toString(dbDep.getRemark())+","+Utils.toString(dep.getRemark()));
		supserDao.update(dbDep);
		
		UserAccount mainAcc = (UserAccount) supserDao.findByName(UserAccount.class, "userId",dbDep.getUserId(), "accType", WalletType.MAIN_WALLET.getCode()).get(0);
		
		UserAccountDetails accDtal1 = userAccountDetailsService.initCreidrRecord(dbDep.getUserId(), mainAcc, mainAcc.getBalance().doubleValue(), dep.getAmount(), Constants.AccOperationType.DEPOSIT.getCode());
		mainAcc.setBalance(new BigDecimal(accDtal1.getPostAmount()));
		supserDao.save(accDtal1);
		supserDao.update(mainAcc);
		
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return ret;
	}
}
