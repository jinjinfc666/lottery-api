package com.jll.common.utils;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	
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
