package com.jll.game;

public class LotteryTypeFactory {
	
	private static LotteryTypeFactory factory;
	
	private LotteryTypeFactory() {
		
	}
	
	public static LotteryTypeFactory getInstance() {
		if(factory == null) {
			factory = new LotteryTypeFactory();
		}
		
		return factory;
	}
	
	public LotteryTypeService createLotteryType(String clazzName) {
		try {
			Class<?> newClass = Class.forName(clazzName);
			
			return (LotteryTypeService)newClass.newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}
