package com.jll.common.utils;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MathUtil {

	public static final int DEFAULT_DIV_SCALE = 10;
	
	private static final List<String> SUPPORTED_DATA_TYPE =  new ArrayList<String>();
	
	static{
		SUPPORTED_DATA_TYPE.add("java.lang.Integer");
		SUPPORTED_DATA_TYPE.add("java.lang.Long");
		SUPPORTED_DATA_TYPE.add("java.lang.Float");
		SUPPORTED_DATA_TYPE.add("java.lang.Double");
	}
	/**
	 * performing the add operation
	 * @param v1 number
	 * @param v2 number
	 * @param clazz number
	 * @return
	 */
	public static <T extends Number, T2 extends Number, T3 extends Number> T3  add(T v1, T2 v2, Class<T3> clazz){
		if(v1 == null || v2 == null || clazz == null){
			throw new IllegalArgumentException("The T v1, T2 v2, Class<T3> clazz must not be null");
		}
		
		if(!SUPPORTED_DATA_TYPE.contains(clazz.getName())
				|| !SUPPORTED_DATA_TYPE.contains(v1.getClass().getName())
						|| !SUPPORTED_DATA_TYPE.contains(v2.getClass().getName())){
			throw new IllegalArgumentException("T or T2 or T3 is not supported");
		}
		
		try {

			BigDecimal b1 = new BigDecimal(v1.toString());

			BigDecimal b2 = new BigDecimal(v2.toString());

			BigDecimal ret = b1.add(b2);
			
			return clazz.getConstructor(String.class).newInstance(ret.toString());
			
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {

		}

		return null;

	}

	/**
	 * 相减
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static <T extends Number, T2 extends Number, T3 extends Number> T3 subtract(T v1, T2 v2, Class<T3> clazz){

		if(v1 == null || v2 == null || clazz == null){
			throw new IllegalArgumentException("The T v1, T2 v2, Class<T3> clazz must not be null");
		}
		
		if(!SUPPORTED_DATA_TYPE.contains(clazz.getName())
				|| !SUPPORTED_DATA_TYPE.contains(v1.getClass().getName())
						|| !SUPPORTED_DATA_TYPE.contains(v2.getClass().getName())){
			throw new IllegalArgumentException("T or T2 or T3 is not supported");
		}
		
		try {

			BigDecimal b1 = new BigDecimal(v1.toString());

			BigDecimal b2 = new BigDecimal(v2.toString());
			
			BigDecimal ret = b1.subtract(b2);
			
			return clazz.getConstructor(String.class).newInstance(ret.toString());
			
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {

		}

		return null;

	}

	/**
	 * 相乘
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static <T extends Number, T2 extends Number, T3 extends Number> T3 multiply(T v1, T2 v2, Class<T3> clazz){

		if(v1 == null || v2 == null || clazz == null){
			throw new IllegalArgumentException("The T v1, T2 v2, Class<T3> clazz must not be null");
		}
		
		if(!SUPPORTED_DATA_TYPE.contains(clazz.getName())
				|| !SUPPORTED_DATA_TYPE.contains(v1.getClass().getName())
						|| !SUPPORTED_DATA_TYPE.contains(v2.getClass().getName())){
			throw new IllegalArgumentException("T or T2 or T3 is not supported");
		}
		
		try {
			BigDecimal b1 = new BigDecimal(v1.toString());

			BigDecimal b2 = new BigDecimal(v2.toString());
			
			BigDecimal ret = b1.multiply(b2);

			return clazz.getConstructor(String.class).newInstance(ret.toString());
			
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {

		}

		return null;

	}
	
	public static <T extends Number, T2 extends Number> Double divide(T v1, T2 v2, int scale){

		if (scale < 0){

			throw new IllegalArgumentException("The scale must be a positive integer or zero");

		}

		if(v1 == null){
			throw new IllegalArgumentException("The T v1 must not be null");
		}
		
		if(v2 == null || "0".equals(v2.toString())){
			throw new IllegalArgumentException("The T2 v2 must not be null or 0");
		}
		
		if(!SUPPORTED_DATA_TYPE.contains(v1.getClass().getName())
						|| !SUPPORTED_DATA_TYPE.contains(v2.getClass().getName())){
			throw new IllegalArgumentException("T or T2 or T3 is not supported");
		}
		
		
		
		try {

			BigDecimal b1 = new BigDecimal(v1.toString());

			BigDecimal b2 = new BigDecimal(v2.toString());

			BigDecimal ret = b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP);
			
			return ret.doubleValue();
		} catch (IllegalArgumentException | SecurityException e) {

		}

		return null;

	}
}