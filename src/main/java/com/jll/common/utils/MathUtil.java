package com.jll.common.utils;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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
	
	
	/**
	 * 计算阶乘
	 * n! = n * (n-1) * ... * 2 * 1 
	 * @param n
	 * @return
	 */
	public static long factorial(int n) {
        long sum = 1;
        while( n > 0 ) {
            sum = sum * n--;
        }
        return sum;
    }
	
	/**
	 * 排列
	 * A(m,n) = n!/(n-m)!
	 * @param m
	 * @param n
	 * @return
	 */
	public static long arrangement(int m, int n) {
        return m <= n ? factorial(n) / factorial(n - m) : 0;
    }
	
	/**
	 * 组合
	 * C(m,n) = n!/(m!*(n - m)!)
	 * @param m
	 * @param n
	 * @return
	 */
	public static long combination(int m, int n) {
        return m <= n ? factorial(n) / (factorial(m) * factorial((n - m))) : 0;
    }
	
	/**
     * 组合选择（从列表中选择n个组合）
     * @param dataList 待选列表
     * @param n 选择个数
     */
    public static void combinationSelect(String[] dataList, int n) {
        System.out.println(String.format("C(%d, %d) = %d", 
                dataList.length, n, combination(n, dataList.length)));
        combinationSelect(dataList, 0, new String[n], 0);
    }

    /**
     * 组合选择
     * @param dataList 待选列表
     * @param dataIndex 待选开始索引
     * @param resultList 前面（resultIndex-1）个的组合结果
     * @param resultIndex 选择索引，从0开始
     */
    private static void combinationSelect(String[] dataList, int dataIndex, String[] resultList, int resultIndex) {  
        int resultLen = resultList.length;
        int resultCount = resultIndex + 1;
        if (resultCount > resultLen) { // 全部选择完时，输出组合结果
            System.out.println(Arrays.asList(resultList));
            return;
        }

        // 递归选择下一个
        for (int i = dataIndex; i < dataList.length + resultCount - resultLen; i++) {
        	//System.out.println(String.format("dataIndex = %s, resultCount= %s, resultLen=%s, maxVal=%s", dataIndex, resultCount, resultLen, (dataList.length + resultCount - resultLen)));
            resultList[resultIndex] = dataList[i];
            combinationSelect(dataList, i + 1, resultList, resultIndex + 1);
        }
    }
    
    /**
     * 排列选择（从列表中选择n个排列） 
     * @param dataList 待选列表 
     * @param n 选择个数 
     */
    public static void arrangementSelect(String[] dataList, int n) {
        System.out.println(String.format("A(%d, %d) = %d", dataList.length, n, 
                arrangement(n, dataList.length)));
        arrangementSelect(dataList, new String[n], 0);
    }

    /** 
     * 排列选择 
     * @param dataList 待选列表 
     * @param resultList 前面（resultIndex-1）个的排列结果 
     * @param resultIndex 选择索引，从0开始 
     */  
    private static void arrangementSelect(String[] dataList, String[] resultList, int resultIndex) {
        int resultLen = resultList.length;
        if (resultIndex >= resultLen) { // 全部选择完时，输出排列结果 
            System.out.println(Arrays.asList(resultList));
            return;
        }

        // 递归选择下一个
        for (int i = 0; i < dataList.length; i++) {
            // 判断待选项是否存在于排列结果中
            boolean exists = false;
            for (int j = 0; j < resultIndex; j++) {
                if (dataList[i].equals(resultList[j])) {
                    exists = true;
                    break;
                }
            }
            if (!exists) { // 排列结果不存在该项，才可选择
                resultList[resultIndex] = dataList[i];
                arrangementSelect(dataList, resultList, resultIndex + 1);
            }
        }
    }
}