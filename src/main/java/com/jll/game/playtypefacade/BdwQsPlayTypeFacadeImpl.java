package com.jll.game.playtypefacade;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jll.common.utils.MathUtil;
import com.jll.common.utils.StringUtils;
import com.jll.common.utils.Utils;
import com.jll.entity.Issue;
import com.jll.entity.OrderInfo;
import com.jll.entity.UserInfo;

public class BdwQsPlayTypeFacadeImpl extends DefaultPlayTypeFacadeImpl {
	
	private Logger logger = Logger.getLogger(QszxPlayTypeFacadeImpl.class);
	
	protected String playTypeDesc = "bdw|不定位/qsbdwfs";
	
	@Override
	public boolean isMatchWinningNum(Issue issue, OrderInfo order) {
		//开奖号码的每一位
		String[] winNumSet = null;
		//投注号码的每个位的号码，可能多个号码
		String[] betNumSet = new String[3];
		//每次点击选号按钮所选号码，多个所选号码以;分割
		String[] betNumMul= null;
		String betNum = null;
		String winNum = null;
		
		winNum = issue.getRetNum();
		betNum = order.getBetNum();
		winNum = winNum.substring(0, 5);
		winNumSet = winNum.split(",");
		//betNumSet = betNum.split(",");
		betNumMul = betNum.split(";");
		
		logger.debug("proced bet number is :: " + Arrays.asList(betNumSet));
		for(String temp : betNumMul) {
			if(StringUtils.isBlank(temp)) {
				continue;
			}
			
			if(temp.contains(winNumSet[0])
					|| temp.contains(winNumSet[1])
					|| temp.contains(winNumSet[2])) {
				return true;
			}
		}
				
		return false;
	}

	@Override
	public Map<String, Object> preProcessNumber(Map<String, Object> params, UserInfo user) {		
		Map<String, Object> ret = new HashMap<>();
		String betNum = (String)params.get("betNum");
		Integer times = (Integer)params.get("times");
		Float monUnit = (Float)params.get("monUnit");
		Integer playType = (Integer)params.get("playType");
		String lottoType = (String)params.get("lottoType");
		Float prizePattern = userServ.calPrizePattern(user, lottoType);
		BigDecimal winningRate = calWinningRate();
		BigDecimal singleBettingPrize = calSingleBettingPrize(prizePattern, winningRate);
		String[] betNumSet = null;
		int betTotal = 1;
		Float betAmount = 0F;
		Float maxWinAmount = 0F;
		
		betNumSet = betNum.split(",");
		for(String subBetNum : betNumSet) {
			int len = subBetNum.length();
			betTotal *= MathUtil.combination(1, len);
		}
		
		betAmount = MathUtil.multiply(betTotal, times, Float.class);
		betAmount = MathUtil.multiply(betAmount, monUnit, Float.class);
		maxWinAmount = MathUtil.multiply(betAmount, singleBettingPrize.floatValue(), Float.class);
		
		ret.put("playType", playType);
		ret.put("betAmount", betAmount);
		ret.put("maxWinAmount", maxWinAmount);
		ret.put("betTotal", betTotal);
		ret.put("singleBettingPrize", singleBettingPrize);
		return ret;
	}

	

	@Override
	public String getPlayTypeDesc() {
		return playTypeDesc;
	}

	@Override
	public boolean validBetNum(OrderInfo order) {
		String betNum = null;
		String[] betNumMul= null;		
		
		betNum = order.getBetNum();
		
		if(StringUtils.isBlank(betNum)) {
			return false;
		}
		
		betNumMul = betNum.split(";");
		
		for(String betNumTemp : betNumMul) {
			if(StringUtils.isBlank(betNumTemp)) {
				return false;
			}
			
			if(betNumTemp.length() > 10
					|| !Utils.validateNum(betNumTemp)) {
				return false;
			}
		}
				
		return true;
	}

	@Override
	public BigDecimal calPrize(Issue issue, OrderInfo order, UserInfo user) {
		// 开奖号码的每一位
		String[] winNumSet = null;
		// 投注号码的每个位的号码，可能多个号码
		String[] betNumSet = new String[3];
		// 每次点击选号按钮所选号码，多个所选号码以;分割
		String[] betNumMul = null;
		String betNum = null;
		String winNum = null;
		int winningBetAmount = 0;
		Float betAmount = 0F;
		Float maxWinAmount = 0F;
		Integer times = order.getTimes();
		BigDecimal monUnit = order.getPattern();
		BigDecimal singleBettingPrize = null;
		
		//1700 --- 1960
		Float prizePattern = userServ.calPrizePattern(user, issue.getLotteryType());
		BigDecimal winningRate = calWinningRate();
		singleBettingPrize =  calSingleBettingPrize(prizePattern, winningRate);
		
		winNum = issue.getRetNum();
		betNum = order.getBetNum();
		winNum = winNum.substring(0, 5);
		//winNumSet = winNum.split(",");
		betNumMul = betNum.split(";");		
		
		for(String singleSel : betNumMul) {
			for(int i = 0; i < singleSel.length(); i++) {
				String numBit = singleSel.substring(i, i + 1);
				if(winNum.contains(numBit)) {
					winningBetAmount++;
				}
			}			
		}
		
		betAmount = MathUtil.multiply(winningBetAmount, times, Float.class);
		betAmount = MathUtil.multiply(betAmount, monUnit, Float.class);
		maxWinAmount = MathUtil.multiply(betAmount, singleBettingPrize, Float.class);
		
		return new BigDecimal(maxWinAmount);
	}

	/* (non-Javadoc)
	 * @see com.jll.game.playtype.PlayTypeFacade#calWinningRate()
	 * 
	 * 1/1000
	 */
	@Override
	public BigDecimal calWinningRate() {
		BigDecimal winningRate = null;
		BigDecimal winCount = new BigDecimal(3);
		BigDecimal totalCount = null;
		Double tempVal = Double.parseDouble(Long.toString(MathUtil.combination(1, 10)));
		
		//tempVal = Math.pow(tempVal, 3);
		totalCount = new BigDecimal(tempVal);
		winningRate = winCount.divide(totalCount);
		return winningRate;
	}
}
