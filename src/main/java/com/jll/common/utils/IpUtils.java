package com.jll.common.utils;

import java.math.BigInteger;

public class IpUtils {
	
	public static long ipToLong(String strIp) {
		String[] ip=strIp.split("\\.");
		return (Long.parseLong(ip[0])<<24)+(Long.parseLong(ip[1])<<16)+(Long.parseLong(ip[2])<<8)+Long.parseLong(ip[3]);
	}
	public static String longToIp(long longIp) {
		StringBuffer sb=new StringBuffer("");
		//直接右移24位
		sb.append(String.valueOf((longIp>>>24)));
		sb.append(".");
		// 将高8位置0，然后右移16位
		sb.append(String.valueOf((longIp&0x00FFFFFF)>>>16));
		sb.append(".");
		// 将高16位置0，然后右移8位
		sb.append(String.valueOf((longIp&0x0000FFFF)>>>8));
		sb.append(".");
		// 将高24位置0
		sb.append(String.valueOf((longIp&0x000000FF)));
		return sb.toString();
	}
	
	public static BigInteger ipv6toInt(String ipv6){
		int compressIndex = ipv6.indexOf("::");
		if (compressIndex != -1)
		{
			String part1s = ipv6.substring(0, compressIndex);
			String part2s = ipv6.substring(compressIndex + 1);
			BigInteger part1 = ipv6toInt(part1s);
			BigInteger part2 = ipv6toInt(part2s);
			int part1hasDot = 0;
			char ch[] = part1s.toCharArray();
			for (char c : ch)
			{
				if (c == ':')
				{
					part1hasDot++;
				}
			}
			// ipv6 has most 7 dot
			return part1.shiftLeft(16 * (7 - part1hasDot )).add(part2);
		}
		String[] str = ipv6.split(":");
		BigInteger big = BigInteger.ZERO;
		for (int i = 0; i < str.length; i++)
		{
			//::1
			if (str[i].isEmpty())
			{
				str[i] = "0";
			}
			big = big.add(BigInteger.valueOf(Long.valueOf(str[i], 16))
			        .shiftLeft(16 * (str.length - i - 1)));
		}
		return big;
	}
	public static String intIpv6(BigInteger big){
		String str = "";
		BigInteger ff = BigInteger.valueOf(0xffff);
		for (int i = 0; i < 8 ; i++)
		{
			str = big.and(ff).toString(16) + ":" + str;
			
			big = big.shiftRight(16);
		}
		//the last :
		str = str.substring(0, str.length() - 1);
		
		return str.replaceFirst("(^|:)(0+(:|$)){2,8}", "::");
	}
}
