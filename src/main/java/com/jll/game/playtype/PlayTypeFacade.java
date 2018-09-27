package com.jll.game.playtype;

import java.math.BigDecimal;
import java.util.Map;

import com.jll.entity.Issue;
import com.jll.entity.OrderInfo;
import com.jll.entity.UserInfo;

public interface PlayTypeFacade
{	
	String getPlayTypeDesc();
	
	/**
	 * 指定的订单是否和期次的开奖号码匹配
	 * 匹配即中奖，否则不中奖
	 * @param issue  期次，保护中奖号码
	 * @param order  用户订单
	 * @return       中奖则返回true
	 */
	boolean isMatchWinningNum(Issue issue, OrderInfo order);
	
	/**
	 * 预处理选号
	 * @param params
	 * @return
	 */
	Map<String, Object> preProcessNumber(Map<String, Object> params, UserInfo user);
	
	/**
	 * verify if the bet number is valid for specified play type
	 * @param order
	 * @return
	 */
	boolean validBetNum(OrderInfo order);
	
	/**
	 * 计算赢的概率
	 * @return
	 */
	BigDecimal calWinningRate();
	
	/**
	 * 计算1注奖金
	 * @return
	 */
	BigDecimal calSingleBettingPrize(Float prizePattern, BigDecimal winningRate);
	
	/**
	 * 计算中奖的投注数
	 * @param issue
	 * @param order
	 * @return
	 */
	BigDecimal calPrize(Issue issue, OrderInfo order, UserInfo user);
	
	
	/**
	 * 计算匹配投注号码的中奖号码
	 * @param issue
	 * @param order
	 * @return
	 */
	String produceWinningNumber(String betNUm);
	
	/**
	 * 计算不匹配投注号码的中奖号码
	 * @param issue
	 * @param order
	 * @return
	 */
	String produceLostNumber(String betNUm);
}
