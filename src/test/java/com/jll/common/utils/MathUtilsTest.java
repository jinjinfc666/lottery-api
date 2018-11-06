package com.jll.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

	//@Test
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
	
	
	@Test
	public void testCombination() {
		Date startDate = new Date();
		Map<String, Long> ret = new HashMap<String, Long>();
		StringBuffer buffer = new StringBuffer();
		StringBuffer bufferI = new StringBuffer();
		StringBuffer bufferII = new StringBuffer();
		StringBuffer bufferIII = new StringBuffer();
		String constants = "1";
		Long counter = 0L;
		
		for(int i = 0; i < 10; i++) {
			for(int ii = 0; ii < 10;ii++){
				for(int iii = 0; iii < 10;iii++){
					for(int iiii = 0; iiii < 10;iiii++){
						buffer.delete(0, buffer.length());
						bufferI.delete(0, bufferI.length());
						bufferII.delete(0, bufferII.length());
						bufferIII.delete(0, bufferIII.length());
						
						/*bufferIII.append(i).append(ii).append(iii).append(iiii);
						Long counter = ret.get(bufferIII.toString());
						if(counter == null) {
							counter = 1L;
							ret.put(bufferIII.toString(), counter);
						}else {
							counter++;
							ret.put(bufferIII.toString(), counter);
						}*/
						
						
						buffer.append(i).append(ii).append(iii).append(iiii).append(constants);
						bufferI.append(i).append(ii).append(iii).append(constants).append(iiii);
						bufferII.append(i).append(ii).append(constants).append(iii).append(iiii);
						counter = ret.get(buffer.toString());
						if(counter == null) {
							counter = 1L;
							ret.put(buffer.toString(), counter);
						}else {
							counter++;
							ret.put(buffer.toString(), counter);
						}
						
						counter = ret.get(bufferI.toString());
						if(counter == null) {
							counter = 1L;
							ret.put(bufferI.toString(), counter);
						}else {
							counter++;
							ret.put(bufferI.toString(), counter);
						}
						
						counter = ret.get(bufferII.toString());
						if(counter == null) {
							counter = 1L;
							ret.put(bufferII.toString(), counter);
						}else {
							counter++;
							ret.put(bufferII.toString(), counter);
						}
					}
				}
			}
		}
		
		Date endDate = new Date();
		
		System.out.println(String.format("create Arragnge %s , take over %s ms", 
				ret.size(),
				endDate.getTime() - startDate.getTime()));
		
		/*startDate = new Date();
		
		
		Iterator<String> keys = ret.keySet().iterator();
		Map<String, Long> retI = new HashMap<String, Long>();
		while(keys.hasNext()) {
			String key = keys.next();
			
			buffer.delete(0, buffer.length());
			bufferI.delete(0, bufferI.length());
			bufferII.delete(0, bufferII.length());
			bufferIII.delete(0, bufferIII.length());
			
			buffer.append(key).append(constants);
			bufferI.append(key.substring(0, key.length() - 1)).append(constants).append(key.substring(key.length() - 1, key.length()));
			bufferII.append(key.substring(0, key.length() - 2)).append(constants).append(key.substring(key.length() - 2, key.length()));
			
			Long counter = retI.get(buffer.toString());
			if(counter == null) {
				counter = 1L;
				retI.put(buffer.toString(), counter);
			}else {
				counter++;
				retI.put(buffer.toString(), counter);
			}
			
			counter = retI.get(bufferI.toString());
			if(counter == null) {
				counter = 1L;
				retI.put(bufferI.toString(), counter);
			}else {
				counter++;
				retI.put(bufferI.toString(), counter);
			}
			
			counter = retI.get(bufferII.toString());
			if(counter == null) {
				counter = 1L;
				retI.put(bufferII.toString(), counter);
			}else {
				counter++;
				retI.put(bufferII.toString(), counter);
			}
		}
		
		
		endDate = new Date();
		
		System.out.println(String.format("create Arragnge %s , take over %s ms", 
				retI.size(),
				endDate.getTime() - startDate.getTime()));*/
		
	}
}
