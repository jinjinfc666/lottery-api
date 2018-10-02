package com.jll.game.playtypefacade;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jll.common.cache.CacheRedisService;
import com.jll.common.utils.MathUtil;
import com.jll.entity.Issue;
import com.jll.entity.OrderInfo;
import com.jll.entity.UserInfo;
import com.jll.game.playtype.PlayTypeFacade;
import com.jll.spring.extend.SpringContextUtil;
import com.jll.user.UserInfoService;

/**
 * bet number like : 1[2],2,3---->万 千 百位
 * @author Administrator
 *
 */
public abstract class DefaultPlayTypeFacadeImpl implements PlayTypeFacade {
	
	private Logger logger = Logger.getLogger(DefaultPlayTypeFacadeImpl.class);
	
	protected String playTypeDesc = null;
	
	CacheRedisService cacheServ = (CacheRedisService)SpringContextUtil.getBean("cacheRedisServiceImpl");
	
	UserInfoService userServ = (UserInfoService)SpringContextUtil.getBean("userInfoServiceImpl");
	
	@Override
	public String getPlayTypeDesc() {
		return playTypeDesc;
	}
	
	@Override
	public abstract boolean isMatchWinningNum(Issue issue, OrderInfo order);

	@Override
	public abstract Map<String, Object> preProcessNumber(Map<String, Object> params, UserInfo user);

	

	

	@Override
	public abstract boolean validBetNum(OrderInfo order) ;

	@Override
	public abstract BigDecimal calPrize(Issue issue, OrderInfo order, UserInfo user);

	/* (non-Javadoc)
	 * @see com.jll.game.playtype.PlayTypeFacade#calWinningRate()
	 * 
	 * 1/1000
	 */
	@Override
	public abstract BigDecimal calWinningRate();

	@Override
	public BigDecimal calSingleBettingPrize(Float prizePattern, BigDecimal winningRate) {
		BigDecimal singleBettingPrize = null;
		BigDecimal priP = new BigDecimal(prizePattern);
		winningRate = winningRate.multiply(new BigDecimal(1000));
		singleBettingPrize = priP.divide(winningRate, 3, BigDecimal.ROUND_HALF_UP);
		
		return singleBettingPrize;
	}
	
	/**
	 * 计算中奖的投注数
	 * @param issue
	 * @param order
	 * @return
	 */
	@Override
	public String produceWinningNumber(String betNUm) {
		return null;
	}
	
	@Override
	public String produceLostNumber(String betNUm) {
		return null;
	}
	
	@Override
	public List<Map<String, String>> parseBetNumber(String betNum){
		return null;
	}
}
