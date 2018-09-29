package com.jll.sys.promo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Constants.AccOperationType;
import com.jll.common.constants.Constants.PromoMultipleType;
import com.jll.common.constants.Constants.PromoValueType;
import com.jll.common.constants.Constants.SysCodeTypes;
import com.jll.common.constants.Constants.WalletType;
import com.jll.common.constants.Message;
import com.jll.common.utils.BigDecimalUtil;
import com.jll.common.utils.PageQuery;
import com.jll.common.utils.StringUtils;
import com.jll.dao.PageQueryDao;
import com.jll.dao.SupserDao;
import com.jll.entity.DepositApplication;
import com.jll.entity.Promo;
import com.jll.entity.PromoClaim;
import com.jll.entity.SysCode;
import com.jll.entity.UserAccount;
import com.jll.entity.UserAccountDetails;
import com.jll.entity.UserInfo;
import com.jll.user.UserInfoDao;
import com.jll.user.UserInfoService;


@Service
@Transactional
public class PromoServiceImpl implements PromoService
{
	private Logger logger = Logger.getLogger(PromoServiceImpl.class);
	
	@Resource
	CacheRedisService cacheRedisService;
	
	@Resource
	UserInfoService userInfoService;
	
	@Resource
	SupserDao  supserDao; 
	
	@Resource
	UserInfoDao userDao;

	
	@Override
	public Map<String, Object> getPromoLists(Promo po, PageQueryDao page) {
		 Map<String, Object> ret = new HashMap<String, Object>(); 
		 DetachedCriteria criteria = DetachedCriteria.forClass(Promo.class);
	     
		 if(null != page.getEndDate()){
			 criteria.add(Restrictions.le("createTime",page.getEndDate()));
		 }
		 if(null != page.getStartDate()){
			 criteria.add(Restrictions.ge("createTime",page.getStartDate()));
		 }
		 if(!StringUtils.isEmpty(po.getPromoType())){
			 criteria.add(Restrictions.eq("promoType",po.getPromoType()));
		}
		 if(null != PromoMultipleType.getValueByCode(po.getIsMultiple())){
			 criteria.add(Restrictions.eq("isMultiple",po.getIsMultiple()));
		}
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put(Message.KEY_DATA,PageQuery.queryForPagenation(supserDao.getHibernateTemplate(), criteria, page.getPageIndex(), page.getPageSize()));
		return ret;
	}

	@Override
	public Map<String, Object> processAccedeToPromo(Promo po) {
		String userName=SecurityContextHolder.getContext().getAuthentication().getName();//当前登录的用户
		UserInfo userInfo=userDao.getUserByUserName(userName);
		Integer userId=userInfo.getId();
		Map<String, Object> ret = new HashMap<String, Object>(); 
		UserInfo dbInfo = (UserInfo) supserDao.get(UserInfo.class,userId);
		
		if(null == dbInfo){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return ret;
		}
		
		Promo dbPro = (Promo) supserDao.get(Promo.class, po.getId());
		if(null == dbPro
				|| dbPro.getExpiredTime().after(new Date())){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return ret;
		}
		
		PromoClaim dbPci = null;
		int calcTimes = 0;	
		//Date calcStartDate = dbPro.getCreateTime(),calcEndDate = dbPro.getExpiredTime();
		 
		DetachedCriteria criteria = DetachedCriteria.forClass(DepositApplication.class);
		criteria.add(Restrictions.ge("createTime",dbPro.getCreateTime()));
		criteria.add(Restrictions.le("createTime",new Date()));
		criteria.add(Restrictions.eq("userId",dbInfo.getId()));
		criteria.add(Restrictions.eq("promoId", dbPro.getId()));
		List<?>	dbPcis = supserDao.findByCriteria(criteria);
		if(null != dbPcis && !dbPcis.isEmpty()){
			dbPci = (PromoClaim) dbPcis.get(dbPcis.size() -1);
			calcTimes = dbPcis.size();
			//calcStartDate = dbPci.getClaimTime();
		}
		
		if(PromoMultipleType.ONCE.getCode() ==
				dbPro.getIsMultiple()){
			if(null != dbPci){
				ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
				ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_PROMS_ONLY_ONCE_DISSATISFY.getCode());
				ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_PROMS_ONLY_ONCE_DISSATISFY.getErrorMes());
				return ret;
			}
			if(SysCodeTypes.LUCKY_DRAW.getCode().equals(po.getPromoType())){
				return processAccedeToLuckyDrwPromo(dbPro,dbInfo);
			}else if(SysCodeTypes.SIGN_IN_DAY.getCode().equals(po.getPromoType())){
				return processAccedeTodaySingInDayPromo(dbPro,dbInfo);
			}
		}
		
		//判断流水
		double curDepAmt = userInfoService.getUserTotalDepostAmt(dbPro.getCreateTime(),dbPro.getExpiredTime(),dbInfo);
		curDepAmt = curDepAmt - dbPro.getMinDepositAmount()*calcTimes;
		if(dbPro.getMinDepositAmount() > curDepAmt){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_PROMS_OTHER_CONDITION_DEPOSIT_DISSATISFY.getCode());
			ret.put(Message.KEY_ERROR_MES,String.format(Message.Error.ERROR_PROMS_OTHER_CONDITION_DEPOSIT_DISSATISFY.getErrorMes(), ""+dbPro.getMinDepositAmount(),""+curDepAmt) );
			return ret;
		}
		double curBetAmt = userInfoService.getUserTotalBetAmt(dbPro.getCreateTime(),dbPro.getExpiredTime(),dbInfo);
		curBetAmt = curBetAmt - dbPro.getFlowTimes()*calcTimes;
		int curMrate =(int)(curBetAmt/curDepAmt);
		if(dbPro.getFlowTimes() > curMrate){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_PROMS_OTHER_CONDITION_FLOWING_DISSATISFY.getCode());
			ret.put(Message.KEY_ERROR_MES,String.format(Message.Error.ERROR_PROMS_OTHER_CONDITION_FLOWING_DISSATISFY.getErrorMes(),""+dbPro.getFlowTimes(),""+curMrate) );
			return ret;
		}
		UserAccount dbAcc = (UserAccount) supserDao.findByName(UserAccount.class, "userId", dbInfo.getId(), "accType", WalletType.MAIN_WALLET.getCode()).get(0);
		UserAccountDetails addDtl = new UserAccountDetails();
		addDtl.setOrderId(dbPro.getId());
		addDtl.setUserId(dbInfo.getId());
		addDtl.setCreateTime(new Date());
		addDtl.setAmount(Double.valueOf(dbPro.getValue()).floatValue());
		if(PromoValueType.CASH.getCode() == dbPro.getValueType()){
			addDtl.setPreAmount(dbAcc.getBalance().floatValue());
			addDtl.setWalletId(dbAcc.getId());
			addDtl.setOperationType(AccOperationType.PROMO_CASH.getCode());
			dbAcc.setBalance(new BigDecimal(addDtl.getPostAmount()));
		}else{
			addDtl.setPreAmount(dbAcc.getRewardPoints().floatValue());
			addDtl.setWalletId(dbAcc.getId());
			addDtl.setOperationType(AccOperationType.PROMO_POINTS.getCode());
			dbAcc.setBalance(new BigDecimal(addDtl.getPostAmount()));
		}
		addDtl.setPostAmount(Double.valueOf(BigDecimalUtil.add(addDtl.getAmount(),addDtl.getPreAmount())).floatValue());
		supserDao.save(addDtl);
		supserDao.update(dbAcc);
		PromoClaim addCla= new PromoClaim();
		addCla.setUserId(dbInfo.getId());
		addCla.setPromoId(dbPro.getId());
		addCla.setClaimTime(new Date());
		supserDao.save(addCla);
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return ret;
	}

	@Override
	public Map<String, Object> getUserPromoLists(PageQueryDao page) {
		Map<String, Object> ret = new HashMap<String, Object>(); 
		DetachedCriteria criteria = DetachedCriteria.forClass(Promo.class);
		criteria.add(Restrictions.le("expiredTime",new Date()));
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put(Message.KEY_DATA,PageQuery.queryForPagenation(supserDao.getHibernateTemplate(), criteria, page.getPageIndex(), page.getPageSize()));
		return ret;
	}

	@Override
	public Map<String, Object> processAccedeToLuckyDrwPromo(Promo dbPro,UserInfo userInfo) {
		Map<String, Object> ret = new HashMap<String, Object>(); 
		Map<String,SysCode> maps =  cacheRedisService.getSysCode(SysCodeTypes.LOTTERY_TYPES.getCode());
		double curDepAmt = userInfoService.getUserTotalDepostAmt(dbPro.getCreateTime(),new Date(),userInfo),
				checkDepAmt = Double.valueOf(maps.get("minimum_recharge").getCodeVal());
		
		if(curDepAmt< checkDepAmt){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_PROMS_OTHER_CONDITION_DEPOSIT_DISSATISFY.getCode());
			ret.put(Message.KEY_ERROR_MES,String.format(Message.Error.ERROR_PROMS_OTHER_CONDITION_DEPOSIT_DISSATISFY.getErrorMes(), ""+checkDepAmt,""+curDepAmt) );
			return ret;
		}
		double curBetAmt = userInfoService.getUserTotalBetAmt(dbPro.getCreateTime(),new Date(),userInfo);
		double checkBetAmt = Double.valueOf(maps.get("minimum_amount_of_water").getCodeVal());
		if(curBetAmt < checkBetAmt){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_PROMS_OTHER_CONDITION_BET_AMT_DISSATISFY.getCode());
			ret.put(Message.KEY_ERROR_MES,String.format(Message.Error.ERROR_PROMS_OTHER_CONDITION_BET_AMT_DISSATISFY.getErrorMes(),""+checkBetAmt,""+curBetAmt) );
			return ret;
		}
		Random random = new Random();
		String unPrizeStr="A",prizeStr="B";
		int totalRate = 10000,checkIndex = random.nextInt(10000);
		int prizeSize = (int) (totalRate*Double.valueOf(maps.get("winning_probability").getCodeVal()));
		int unPrizeSize = totalRate-prizeSize;
		String checkPrize = StringUtils.generateStingByLength(unPrizeStr,unPrizeSize,prizeStr,prizeSize);

		//send prize amt
		if(prizeStr.equals(String.valueOf(checkPrize.toCharArray()[checkIndex]))){
			String[] prizeAmtStr = maps.get("winning_range").getCodeVal().split(StringUtils.COMMA);
			int prizeIndex = random.nextInt(prizeAmtStr.length);
			double prizeAmt = BigDecimalUtil.mul(BigDecimalUtil.div(curBetAmt, curDepAmt), Double.valueOf(prizeAmtStr[prizeIndex]));
			UserAccount dbAcc = (UserAccount) supserDao.findByName(UserAccount.class, "userId", userInfo.getId(), "accType", WalletType.RED_PACKET_WALLET.getCode()).get(0);
			UserAccountDetails addDtl = new UserAccountDetails();
			addDtl.setOrderId(dbPro.getId());
			addDtl.setUserId(userInfo.getId());
			addDtl.setCreateTime(new Date());
			addDtl.setAmount(Double.valueOf(prizeAmt).floatValue());
			addDtl.setPreAmount(dbAcc.getBalance().floatValue());
			addDtl.setPostAmount(Double.valueOf(BigDecimalUtil.add(addDtl.getAmount(),addDtl.getPreAmount())).floatValue());
			addDtl.setWalletId(dbAcc.getId());
			addDtl.setOperationType(AccOperationType.ACTIVITY_GIFT_RED.getCode());
			supserDao.save(addDtl);
			dbAcc.setBalance(new BigDecimal(addDtl.getPostAmount()));
			supserDao.update(dbAcc);
		}
		PromoClaim addCla= new PromoClaim();
		addCla.setUserId(userInfo.getId());
		addCla.setPromoId(dbPro.getId());
		addCla.setClaimTime(new Date());
		supserDao.save(addCla);
		
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put(Message.KEY_DATA, Message.status.SUCCESS.getCode());
		
		return ret;
	}

	@Override
	public Map<String, Object> processAccedeTodaySingInDayPromo(Promo dbPro, UserInfo userInfo) {
		Map<String, Object> ret = new HashMap<String, Object>(); 
		Map<String,SysCode> maps =  cacheRedisService.getSysCode(SysCodeTypes.SIGN_IN_DAY.getCode());
		Random random = new Random();
		String[] prizeAmtStr = maps.get("sign_in_point_range").getCodeVal().split(StringUtils.COMMA);
		int prizeIndex = random.nextInt(prizeAmtStr.length);
		double prizeAmt = BigDecimalUtil.toDouble(prizeAmtStr[prizeIndex]);
		
		UserAccount dbAcc = (UserAccount) supserDao.findByName(UserAccount.class, "userId", userInfo.getId(), "accType", WalletType.MAIN_WALLET.getCode()).get(0);
		UserAccountDetails addDtl = new UserAccountDetails();
		addDtl.setOrderId(dbPro.getId());
		addDtl.setUserId(userInfo.getId());
		addDtl.setCreateTime(new Date());
		addDtl.setAmount(Double.valueOf(prizeAmt).floatValue());
		addDtl.setPreAmount(dbAcc.getRewardPoints().floatValue());
		addDtl.setPostAmount(Double.valueOf(BigDecimalUtil.add(addDtl.getAmount(),addDtl.getPreAmount())).floatValue());
		addDtl.setWalletId(dbAcc.getId());
		addDtl.setOperationType(AccOperationType.DAILY_SIGN_IN.getCode());
		supserDao.save(addDtl);
		dbAcc.setRewardPoints(new BigDecimal(addDtl.getPostAmount()).longValue());
		supserDao.update(dbAcc);
		
		PromoClaim addCla= new PromoClaim();
		addCla.setUserId(userInfo.getId());
		addCla.setPromoId(dbPro.getId());
		addCla.setClaimTime(new Date());
		supserDao.save(addCla);
		
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put(Message.KEY_DATA, Message.status.SUCCESS.getCode());
		return ret;
	}

	@Override
	public Promo getPromoByCode(String pCode) {
		List<?>	dbPcis = supserDao.findByName(Promo.class, "promoType", pCode);
		Promo ret = new Promo();
		if(null != dbPcis && !dbPcis.isEmpty()){
			ret = (Promo) dbPcis.get(0);
		}
		return ret;
	}

}
