package com.jll.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;


public class MathUtilsTest {

	//@Test
	public void testCombinationSelect() {
		String[] dataList = {"0","1","2","3","4","5","6","7","8","9"};
		List<String[]> results = new ArrayList<>();
		MathUtil.combinationSelect(dataList, 2, results);
		
		for(String[] result : results) {
			System.out.println(Arrays.asList(result));
		}
	}

	@Test
	public void testArrangementSelect() {
		String[] dataList = {"0","1"};
		List<String[]> results = new ArrayList<>();
		List<String[]> finalResults = new ArrayList<>();
		MathUtil.arrangementSelect(dataList, 2, results);
		
		for(String[] result : results) {
			String[] extendsArray = new String[3];
			extendsArray[0] = "1";
			extendsArray[1] = result[0];
			extendsArray[2] = result[1];
			
			finalResults.add(extendsArray);
			
			String[] extendsArray2 = new String[3];
			extendsArray2[0] = result[0];
			extendsArray2[1] = result[1];
			extendsArray2[2] = "1";
			
			finalResults.add(extendsArray2);
		}
		
		for(String[] result : finalResults) {
			System.out.println(Arrays.asList(result));
		}
	}
}
