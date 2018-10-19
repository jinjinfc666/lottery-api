package com.jll.game.playtypefacade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;

import com.jll.common.constants.Constants;
import com.jll.common.utils.MathUtil;
import com.jll.common.utils.StringUtils;
import com.jll.entity.Issue;
import com.jll.entity.OrderInfo;
import com.jll.entity.UserInfo;

public class QszxPlayTypeFacadeImpl extends DefaultPlayTypeFacadeImpl {
	
	private Logger logger = Logger.getLogger(QszxPlayTypeFacadeImpl.class);
	
	protected String playTypeDesc = "qszx|前三直选/fs-ds";
	
	@Override
	public boolean isMatchWinningNum(Issue issue, OrderInfo order) {
		//开奖号码的每一位
		String[] winNumSet = null;
		//每次点击选号按钮所选号码，多个所选号码以;分割
		String[] betNumMul= null;
		String betNum = null;
		String winNum = null;
		
		winNum = issue.getRetNum();
		betNum = order.getBetNum();
		winNum = winNum.substring(0,5);
		winNumSet = winNum.split(",");
		betNumMul = betNum.split(";");
		
		for(String temp : betNumMul) {
			String[] tempSet = temp.split(",");
			if(tempSet[0].contains(winNumSet[0])
					&& tempSet[1].contains(winNumSet[1])
					&& tempSet[2].contains(winNumSet[2])) {
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
		int winBetTotal = 0;
		
		betNumSet = betNum.split(";");
		for(String singleBetNum : betNumSet) {
			String[] betNumBits = singleBetNum.split(",");
			for(String betNumBit : betNumBits) {
				int len = betNumBit.length();
				betTotal *= MathUtil.combination(1, len);
			}
			
			winBetTotal++;
		}
		
		betAmount = MathUtil.multiply(betTotal, times, Float.class);
		betAmount = MathUtil.multiply(betAmount, monUnit, Float.class);
		
		maxWinAmount = MathUtil.multiply(winBetTotal, 
				times, 
				Float.class);
		maxWinAmount = MathUtil.multiply(maxWinAmount, 
				monUnit, 
				Float.class);
		maxWinAmount = MathUtil.multiply(maxWinAmount, 
				singleBettingPrize.floatValue(), 
				Float.class);
		
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
		String[] betNumSet = null;
		
		betNum = order.getBetNum();
		if(StringUtils.isBlank(betNum)) {
			return false;
		}
		
		betNumSet = betNum.split(";");
		for(String singleBetNum : betNumSet) {
			if(StringUtils.isBlank(singleBetNum)) {
				return false;
			}
			
			String[] betNumBits = singleBetNum.split(",");
			
			if(betNumBits.length != 3) {
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
		winNumSet = winNum.split(",");
		betNumMul = betNum.split(";");
		
		
		for(String singleSel : betNumMul) {
			betNumSet = singleSel.split(",");
			if(betNumSet[0].contains(winNumSet[0])
					&& betNumSet[1].contains(winNumSet[1])
					&& betNumSet[2].contains(winNumSet[2])) {
				winningBetAmount++;
			}
		}
		
		betAmount = MathUtil.multiply(winningBetAmount, times, Float.class);
		betAmount = MathUtil.multiply(betAmount, monUnit.floatValue(), Float.class);
		maxWinAmount = MathUtil.multiply(betAmount, singleBettingPrize.floatValue(), Float.class);
		
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
		BigDecimal winCount = new BigDecimal(1);
		BigDecimal totalCount = null;
		Double tempVal = Double.parseDouble(Long.toString(MathUtil.combination(1, 10)));
		
		tempVal = Math.pow(tempVal, 3);
		totalCount = new BigDecimal(tempVal);
		winningRate = winCount.divide(totalCount, 4, BigDecimal.ROUND_HALF_UP);
		return winningRate;
	}

	@Override
	public String produceWinningNumber(String betNum) {
		String[] betNumSet = betNum.split(",");
		StringBuffer winningBetNum = new StringBuffer();
		Random random = new Random();
		
		for(String betNumBit : betNumSet) {
			winningBetNum.append(betNumBit.substring(0, 1));
		}
		
		for(int i = 0; i < 2; i++) {
			int currIndex = random.nextInt(10);
			winningBetNum.append(Integer.toString(currIndex));
		}
		
		return winningBetNum.toString();
		
	}
	
	@Override
	public String produceLostNumber(String betNum) {
		String[] betNumSet = betNum.split(",");
		StringBuffer winningBetNum = new StringBuffer();
		Random random = new Random();
		
		for(String betNumBit : betNumSet) {
			for(int i = 0; i < 10; i++) {
				if(betNumBit.contains(Integer.toString(i))) {
					continue;
				}
				
				winningBetNum.append(i);
				break;
			}
		}
		
		for(int i = 0; i < 2; i++) {
			int currIndex = random.nextInt(10);
			winningBetNum.append(Integer.toString(currIndex));
		}
		
		return winningBetNum.toString();
	}
	
	@Override
	public List<Map<String, String>> parseBetNumber(String betNum){
		List<Map<String, String>> betNumList = new ArrayList<>();
		String[] betNumArray = betNum.split(";");
		for(String singleBetNumArray : betNumArray) {
			String[] betNumBits = singleBetNumArray.split(",");
			
			for(int i = 0 ; i < betNumBits[0].length(); i++) {
				String a = betNumBits[0].substring(i, i + 1);
				for(int ii = 0; ii < betNumBits[1].length(); ii++) {
					String aa = betNumBits[1].substring(ii, ii + 1);
					for(int iii = 0; iii < betNumBits[2].length(); iii++) {
						String aaa = betNumBits[2].substring(iii, iii + 1);
						StringBuffer buffer = new StringBuffer();
						buffer.append(a).append(aa).append(aaa);
						
						Map<String, String> row = new HashMap<String, String>();
						row.put(Constants.KEY_FACADE_BET_NUM, buffer.toString());
						row.put(Constants.KEY_FACADE_PATTERN, buffer.toString() + "[0-9]{2}");
						row.put(Constants.KEY_FACADE_BET_NUM_SAMPLE, buffer.toString() + "00");				
						betNumList.add(row);
						//betNumList.add(buffer.toString());
					}
				}
			}
		}
		
		
		return betNumList;
	}
	
	@Override
	public String obtainSampleBetNumber(){
		Random random = new Random();
		StringBuffer betNum = new StringBuffer();
		for(int i = 0 ; i < 3; i++) {
			int bit = random.nextInt(10);
			betNum.append(Integer.toString(bit)).append(",");
		}
		
		betNum.delete(betNum.length()-1, betNum.length());
		
		return betNum.toString();
	}
}
