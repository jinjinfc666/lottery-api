package com.jll.common.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;


/**
 * @date 2016/12/12
 * @time 17:38
 */
public class MD5Signature {
	 
	/**
	 * 创建md5摘要,规则是:按参数名称a-z排序,遇到空值的参数不参加签名。
	 * @param packageParams
	 * @return
	 */
	@SuppressWarnings("rawtypes")

	public static String createSign(SortedMap<String, Object> packageParams, String partnerKey) {
		StringBuffer sb = new StringBuffer();
		Set es = packageParams.entrySet();
		Iterator it = es.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String k = (String) entry.getKey();
			Object valObj = entry.getValue();
			String v = null;
			if(valObj.getClass().getName().equals("java.lang.String")) {
				v = (String) entry.getValue();
			}else if(valObj.getClass().getName().equals("java.lang.Integer")) {
				v = String.valueOf(((Integer) entry.getValue()));
			}else if(valObj.getClass().getName().equals("java.lang.Float")) {
				v = String.valueOf(((Float) entry.getValue()));
			}else if(valObj.getClass().getName().equals("java.lang.Long")) {
				v = String.valueOf(((Long) entry.getValue()));
			}else if(valObj.getClass().getName().equals("java.lang.Boolean")) {
				v = ((Boolean) entry.getValue()).toString();
			}
			//String v = (String) entry.getValue();
			if (StringUtils.isNotEmpty(v) && !"sign".equals(k) && !"key".equals(k)) {
				sb.append(k + "=" + v + "&");
			}
		}
		sb.append("key=" + partnerKey);
		String sign = null;
		try {
			sign = DigestUtils.md5Hex(sb.toString().getBytes("UTF-8")).toUpperCase();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return sign;

	}

	public static String signByRSA(SortedMap<String, Object> packageParams, String partnerKey) {
		StringBuffer sb = new StringBuffer();
		Set es = packageParams.entrySet();
		Iterator it = es.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String k = (String) entry.getKey();
			Object valObj = entry.getValue();
			String v = null;
			if(valObj.getClass().getName().equals("java.lang.String")) {
				v = (String) entry.getValue();
			}else if(valObj.getClass().getName().equals("java.lang.Integer")) {
				v = String.valueOf(((Integer) entry.getValue()));
			}else if(valObj.getClass().getName().equals("java.lang.Float")) {
				v = String.valueOf(((Float) entry.getValue()));
			}else if(valObj.getClass().getName().equals("java.lang.Long")) {
				v = String.valueOf(((Long) entry.getValue()));
			}else if(valObj.getClass().getName().equals("java.lang.Boolean")) {
				v = ((Boolean) entry.getValue()).toString();
			}
			//String v = (String) entry.getValue();
			if (StringUtils.isNotEmpty(v) && !"sign".equals(k) && !"key".equals(k)) {
				sb.append(k + "=" + v + "&");
			}
		}
		sb.append("key=" + partnerKey);
		String sign = null;
		try {
			sign = DigestUtils.md5Hex(sb.toString().getBytes("UTF-8")).toUpperCase();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return sign;

	}
}
