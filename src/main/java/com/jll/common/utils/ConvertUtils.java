package com.jll.common.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
/**
 * 
 * 继承org.apache.commons.beanutils.ConvertUtils，注册更多类型; List
 * String="BYLIVE|HGLIVE|BYOTHS|OPUSLIVE|OPUSKENO|OPUSOTHS" Date
 * String="yyyy-MM-dd HH:mm:ss"
 * 
 * @author Win 2012-12-31
 * 
 */
public class ConvertUtils  {
	private final static org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ConvertUtils.class);

	static {
		LOGGER.info("Register Converters ...");
		org.apache.commons.beanutils.ConvertUtils.register(new Converter() {
			public Object convert(Class cls, Object value) {
				if (value == null)
					return null;
				List list = new ArrayList();
				String listStr = ObjectUtils.toString(value);
				String[] array = StringUtils.split(listStr, '|');
				for (String string : array) {
					list.add(string);
				}
				return list;
			}
		}, List.class);

		org.apache.commons.beanutils.ConvertUtils.register(new Converter() {
			public Object convert(Class cls, Object value) {
				if (value == null)
					return null;
				// 2012-12-12 12:12:12
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				try {
					return df.parse(ObjectUtils.toString(value));
				} catch (ParseException e) {
					LOGGER.error(null, e);
					return null;
				}
			}
		}, Date.class);
	}

	public static Object convert(Object value, Class<?> targetType) {
		return org.apache.commons.beanutils.ConvertUtils.convert(value, targetType);
	}
	
	public static Double convertDouble(Object text) {
		return (Double) ConvertUtils.convert(text, Double.class);
	}

	public static Integer convertInteger(Object text) {
		return (Integer) ConvertUtils.convert(text, Integer.class);
	}

	public static Date convertDate(Object text) {
		return (Date)ConvertUtils.convert(text, Date.class);
	}
}
