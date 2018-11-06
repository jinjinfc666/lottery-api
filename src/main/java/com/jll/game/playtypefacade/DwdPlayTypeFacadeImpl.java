package com.jll.game.playtypefacade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;

import com.jll.common.constants.Constants;
import com.jll.common.utils.MathUtil;
import com.jll.common.utils.StringUtils;
import com.jll.common.utils.Utils;
import com.jll.entity.Issue;
import com.jll.entity.OrderInfo;
import com.jll.entity.UserInfo;

public class DwdPlayTypeFacadeImpl extends DefaultPlayTypeFacadeImpl {
	
	private Logger logger = Logger.getLogger(QszxPlayTypeFacadeImpl.class);
	
	protected String playTypeDesc = "dwd|定位胆/dwdfs";
	
	@Override
	public boolean isMatchWinningNum(Issue issue, OrderInfo order) {
		//开奖号码的每一位
		String[] winNumSet = null;
		//投注号码的每个位的号码，可能多个号码
		String[] betNumSet = new String[5];
		//每次点击选号按钮所选号码，多个所选号码以;分割
		String[] betNumMul= null;
		String betNum = null;
		String winNum = null;
		
		winNum = issue.getRetNum();
		betNum = order.getBetNum();
		//winNum = winNum.substring(4, 9);
		winNumSet = winNum.split(",");
		//betNumSet = betNum.split(",");
		betNumMul = betNum.split(";");
		
		logger.debug("proced bet number is :: " + Arrays.asList(betNumSet));
		for(String temp : betNumMul) {
			if(StringUtils.isBlank(temp)) {
				continue;
			}
			
			String[] tempSet = splitBit(temp, 1);
			for(int i = 0; i < tempSet.length; i++) {
				String tempBit = tempSet[i];
				String winNumBit = winNumSet[i];
				if(StringUtils.isBlank(tempBit)) {
					continue;
				}
				
				if(tempBit.contains(winNumBit)) {
					return true;
				}
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
		int betTotal = 1;
		Float betAmount = 0F;
		Float maxWinAmount = 0F;
		
		betNum = betNum.replaceAll(",", "");
		int len = betNum.length();
		betTotal = (int)MathUtil.combination(1, len);
		
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
		String[] betNumMul = null;
		
		betNum = order.getBetNum();
		
		if(StringUtils.isBlank(betNum)) {
			return false;
		}
		
		betNumMul = betNum.split(";");
		
		for(String betNumTemp : betNumMul) {
			if(StringUtils.isBlank(betNumTemp)) {
				return false;
			}
			
			String[] betNumTempSet = betNumTemp.split(",");
			
			if(betNumTempSet.length > 5) {
				return false;
			}
			
			for(String betNumTempBit : betNumTempSet) {
				if(StringUtils.isBlank(betNumTempBit)) {
					continue;
				}
				
				if(betNumTempBit.length() > 10) {
					return false;
				}
			}
		}
				
		return true;
	}

	@Override
	public Map<String, Object> calPrize(Issue issue, OrderInfo order, UserInfo user) {
		Map<String, Object> ret = new HashMap<String, Object>();
		// 开奖号码的每一位
		String[] winNumSet = null;
		// 投注号码的每个位的号码，可能多个号码
		//String[] betNumSet = new String[5];
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
		//winNum = winNum.substring(4, 9);
		winNumSet = winNum.split(",");
		betNumMul = betNum.split(";");		
		
		for(String singleSel : betNumMul) {			
			if(StringUtils.isBlank(singleSel)) {
				continue;
			}
			String[] singleSelSet = splitBit(singleSel, 1);
			for(int i = 0; i< singleSelSet.length; i++) {
				if(StringUtils.isBlank(singleSelSet[i])) {
					continue;
				}
				if(singleSelSet[i].contains(winNumSet[i])) {
					winningBetAmount++;
				}
			}
		}
		
		betAmount = MathUtil.multiply(winningBetAmount, times, Float.class);
		betAmount = MathUtil.multiply(betAmount, monUnit.floatValue(), Float.class);
		maxWinAmount = MathUtil.multiply(betAmount, singleBettingPrize.floatValue(), Float.class);
		
		ret.put(Constants.KEY_WINNING_BET_TOTAL, winningBetAmount);
		ret.put(Constants.KEY_WIN_AMOUNT, maxWinAmount);
		ret.put(Constants.KEY_SINGLE_BETTING_PRIZE, singleBettingPrize);
		
		return ret;
	}

	/* (non-Javadoc)
	 * @see com.jll.game.playtype.PlayTypeFacade#calWinningRate()
	 * 
	 * 1/1000
	 */
	@Override
	public BigDecimal calWinningRate() {
		BigDecimal winningRate = null;
		BigDecimal winCount = new BigDecimal(1);
		BigDecimal totalCount = null;
		Double tempVal = Double.parseDouble(Long.toString(MathUtil.combination(1, 10)));
		
		//tempVal = Math.pow(tempVal, 3);
		totalCount = new BigDecimal(tempVal);
		winningRate = winCount.divide(totalCount);
		return winningRate;
	}
	
	
	@Override
	public List<Map<String, String>> parseBetNumber(String betNum){
		List<Map<String, String>> betNumList = new ArrayList<>();
		String[] betNumArray = betNum.split(";");
		StringBuffer buffer = new StringBuffer();
		boolean isMatch1 = false;
		boolean isMatch2 = false;
		boolean isMatch3 = false;
		boolean isMatch4 = false;
		boolean isMatch5 = false;
		
		for(String singleBetNumArray : betNumArray) {
			String[] betNumBits = splitBit(singleBetNumArray, 1);
			
			for(int i = 0; i < 10; i++) {				
				if(betNumBits[0].contains(String.valueOf(i))) {
					isMatch1 = true;
				}
				
				for(int ii = 0; ii < 10;ii++){
					if(betNumBits[1].contains(String.valueOf(ii))) {
						isMatch2 = true;
					}
					
					for(int iii = 0; iii < 10;iii++){
						if(betNumBits[2].contains(String.valueOf(iii))) {
							isMatch3 = true;
						}
						
						for(int iiii = 0; iiii < 10;iiii++){
							if(betNumBits[3].contains(String.valueOf(iiii))) {
								isMatch4 = true;
							}
							for(int iiiii = 0; iiiii < 10;iiiii++){
								if(betNumBits[4].contains(String.valueOf(iiiii))) {
									isMatch5 = true;
								}
								
								buffer.delete(0, buffer.length());
								
								
								buffer.append(i).append(ii).append(iii).append(iiii).append(iiiii);
								
								
								if(isMatch1
										|| isMatch2
										|| isMatch3
										|| isMatch4
										|| isMatch5) {
									Map<String, String> row = new HashMap<String, String>();
									row.put(Constants.KEY_FACADE_BET_NUM, buffer.toString());
									row.put(Constants.KEY_FACADE_PATTERN, buffer.toString());
									row.put(Constants.KEY_FACADE_BET_NUM_SAMPLE, buffer.toString());
									betNumList.add(row);
								}
								
								isMatch5 = false;
							}
							
							isMatch4 = false;
						}
						
						isMatch3 = false;
					}
					isMatch2 = false;
				}
				isMatch1 = false;
			}
		}
		
		return betNumList;
	}
	
	@Override
	public String obtainSampleBetNumber(){
		Random random = new Random();
		StringBuffer betNum = new StringBuffer();
		
		int bit = random.nextInt(10);
		int bit2 = random.nextInt(10);
		int bit3 = random.nextInt(10);
		int bit4 = random.nextInt(10);
		int bit5 = random.nextInt(10);
		
		betNum.append(Integer.toString(bit)).append(",");
		betNum.append(Integer.toString(bit2)).append(",");
		betNum.append(Integer.toString(bit3)).append(",");
		betNum.append(Integer.toString(bit4)).append(",");
		betNum.append(Integer.toString(bit5));
				
		return betNum.toString();
	}
	
	
	private String[] splitBit(String singleSel, int step) {
		List<String> retList = new ArrayList<>();
		StringBuffer buffer = new StringBuffer();
		
		for(int i = 0; i < singleSel.length();) {
			String temp = singleSel.substring(i, i + step);
			if(",".equals(temp)) {
				retList.add(buffer.toString());
				buffer.delete(0, buffer.length());
			}else {
				buffer.append(temp);
			}
			
			i += step;
			
			if(i >= singleSel.length()) {
				retList.add(buffer.toString());
				buffer.delete(0, buffer.length());
			}
		}
		
		return retList.toArray(new String[0]);
	}
}
