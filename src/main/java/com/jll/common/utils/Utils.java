package com.jll.common.utils;

import java.util.Random;

public class Utils {
	public static final char[] alphabet = {'a','b','c','d','e','f','g','h','i',
											'j','k','l','m','n','o','p','q','r',
											's','t','u','v','w','x','y','z','0',
											'1','2','3','4','5','6','7','8','9'};
	
	public static String produce6DigitsCaptchaCode() {
		String ret = "";
		Random random = new Random();
		for(int i = 0; i < 6; i++) {
			int currIndex = random.nextInt(alphabet.length);
			ret += alphabet[currIndex];
		}
		return ret;
				
	}
}
