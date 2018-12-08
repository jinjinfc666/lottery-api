package com.jll.common.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

public class DateUtil extends DateUtils {
	
	private static SimpleDateFormat fmtYmdHis = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat fmtYmdHisEmp = new SimpleDateFormat("yyyyMMddHHmmss");
	private static SimpleDateFormat fmtYmd = new SimpleDateFormat("yyyy-MM-dd");
	
	
	
	public static Date getDateDayStart(Date date) {
		try {
			String str = fmtYmd.format(date)+" 00:00:00";
			return fmtYmdHis.parse(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}
	
	public static Date getDateDayEnd(Date date) {
		try {
			String str = fmtYmd.format(date)+" 23:59:59";
			return fmtYmdHis.parse(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}
	
	
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
	
	public static Date fmtYmdHisToDate(String dateStr) {
		try {
			return  fmtYmdHis.parse(dateStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
	
	/**
	 * 返回间隔天数
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public static long intervalDays(Date beginDate, Date endDate){
		Long milliSeconds = endDate.getTime() - beginDate.getTime();
		
		 milliSeconds = milliSeconds/(1000*60*60*24);
		return milliSeconds;
	}
	public static boolean isValidDate(String str){
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //这里的时间格式根据自己需求更改（注意：格式区分大小写、格式区分大小写、格式区分大小写） 
		try{ 
			Date date = (Date)formatter.parse(str); 
			return str.equals(formatter.format(date)); 
		}catch(Exception e){ 
			return false; 
		} 
	}
}
