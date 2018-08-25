package com.jll.common.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

public class DateUtil extends DateUtils {
	
	private static SimpleDateFormat fmtYmdHis = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat fmtYmdHisEmp = new SimpleDateFormat("yyyyMMddHHmmss");
	
	public static String fmtYmdHis(Date date) {
		String str = null;
		try {
			str = fmtYmdHis.format(date);
		} catch (Exception e) {
			e.printStackTrace();
			str = null;
		}
		return str;
	}
	
	public static String fmtYmdHisEmp(Date date) {
		String str = null;
		try {
			str = fmtYmdHisEmp.format(date);
		} catch (Exception e) {
			e.printStackTrace();
			str = null;
		}
		return str;
	}
	
	public static Date addDay(Date sourceDate, int days){
		Calendar calendar = Calendar.getInstance();
		
		calendar.setTime(sourceDate);
		calendar.add(Calendar.DAY_OF_MONTH, days);
		
		return calendar.getTime();
	}
	
	public static Date addMonth(Date sourceDate, int months){
		Calendar calendar = Calendar.getInstance();
		
		calendar.setTime(sourceDate);
		calendar.add(Calendar.MONTH, months);
		
		return calendar.getTime();
	}
	
	public static Date addYear(Date sourceDate, int years){
		Calendar calendar = Calendar.getInstance();
		
		calendar.setTime(sourceDate);
		calendar.add(Calendar.YEAR, years);
		
		return calendar.getTime();
	}
}
