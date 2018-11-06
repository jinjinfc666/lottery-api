package com.jll.common.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.jll.entity.OrderInfo;
import com.jll.entity.UserAccount;
import com.jll.entity.UserInfo;


public class UtilsTest {

	//@Test
	public void testValidUserName_tooShort() {
		String userName = "t";
		boolean isValid = Utils.validUserName(userName);
		Assert.assertFalse(isValid);
	}

	//@Test
	public void testValidUserName_tooLong() {
		String userName = "test1111111111111111111111111111111111111111111111111111111111111111111111111111111111111test";
		boolean isValid = Utils.validUserName(userName);
		Assert.assertFalse(isValid);
	}
	
	//@Test
	public void testValidUserName_illegalCharacter() {
		String userName = "test111+";
		boolean isValid = Utils.validUserName(userName);
		Assert.assertFalse(isValid);
	}
	
	//@Test
	public void testValidUserName_pass() {
		String userName = "test11122_";
		boolean isValid = Utils.validUserName(userName);
		Assert.assertTrue(isValid);
	}
	
	//@Test
	public void testValidPwd() {
		String pwd = "test11122_";
		boolean isValid = Utils.validPwd(pwd);
		Assert.assertTrue(isValid);
	}
	
	//@Test
	public void testValidPwd_tooShort() {
		String pwd = "t2_";
		boolean isValid = Utils.validPwd(pwd);
		Assert.assertFalse(isValid);
	}
	
	//@Test
	public void testValidPwd_tooLong() {
		String pwd = "t21111111111111111111111111111111111111111111111111111111_";
		boolean isValid = Utils.validPwd(pwd);
		Assert.assertFalse(isValid);
	}
	
	//@Test
	public void testValidPwd_illegalCharacter() {
		String pwd = "t2222222_+";
		boolean isValid = Utils.validPwd(pwd);
		Assert.assertFalse(isValid);
	}

	//@Test
	public void testValidEmail() {
		String pwd = "test//@Test.com";
		boolean isValid = Utils.validEmail(pwd);
		Assert.assertTrue(isValid);
	}

	//@Test
	public void testValidEmail_noAtSign() {
		String pwd = "testtest.com";
		boolean isValid = Utils.validEmail(pwd);
		Assert.assertFalse(isValid);
	}
	
	//@Test
	public void testValidEmail_noDot() {
		String pwd = "test//@Testcom";
		boolean isValid = Utils.validEmail(pwd);
		Assert.assertFalse(isValid);
	}
	
	//@Test
	public void testValidEmail_() {
		String pwd = "//@Testcom";
		boolean isValid = Utils.validEmail(pwd);
		Assert.assertFalse(isValid);
	}
	
	//@Test
	public void testValidPhone() {
		String phone = "15927228888";
		boolean isValid = Utils.validPhone(phone);
		Assert.assertTrue(isValid);
	}
	
	//@Test
	public void testValidPhone_tooShort() {
		String phone = "111";
		boolean isValid = Utils.validPhone(phone);
		Assert.assertFalse(isValid);
	}
	
	//@Test
	public void testValidPhone_tooLong() {
		String phone = "111111111111111";
		boolean isValid = Utils.validPhone(phone);
		Assert.assertFalse(isValid);
	}
	
	//@Test
	public void testValidPhone_wrongNum() {
		String phone = "11111111111";
		boolean isValid = Utils.validPhone(phone);
		Assert.assertFalse(isValid);
	}

	//@Test
	public void testValidRealName() {
		String realName = "张三";
		boolean isValid = Utils.validRealName(realName);
		Assert.assertTrue(isValid);
	}

	//@Test
	public void testValidRealName_dot() {
		String realName = "张三·丰";
		boolean isValid = Utils.validRealName(realName);
		Assert.assertTrue(isValid);
	}
	
	//@Test
	public void testValidRealName_tooShort() {
		String realName = "张";
		boolean isValid = Utils.validRealName(realName);
		Assert.assertFalse(isValid);
	}
	
	//@Test
	public void testValidRealName_tooLong() {
		String realName = "张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张张";
		boolean isValid = Utils.validRealName(realName);
		Assert.assertFalse(isValid);
	}
	
	//@Test
	public void testValidRealName_alphabet() {
		String realName = "张三丰A";
		boolean isValid = Utils.validRealName(realName);
		Assert.assertFalse(isValid);
	}
	
	//@Test
	public void testSplitBit() {
		String singleSel = "01,,03";
		int step = 1;
		
		List<String> retList = new ArrayList<>();
		StringBuffer buffer = new StringBuffer();
		
		for(int i = 0; i < singleSel.length();) {
			String temp = singleSel.substring(i, i + step);
			if(",".equals(temp)) {
				retList.add(buffer.toString());
				buffer.delete(0, buffer.length());
			}else {
				buffer.append(temp);
			}
			
			i += step;
			
			if(i >= singleSel.length()) {
				retList.add(buffer.toString());
				buffer.delete(0, buffer.length());
			}
		}
		
		Assert.assertTrue(retList.toArray(new String[0]).length == 3);
	}
	
	//@Test
	public void testCalRebate() {
		UserInfo user = new UserInfo();
		OrderInfo order = new OrderInfo();
		BigDecimal preAmount = new BigDecimal(0.93F);
		BigDecimal postAmount = new BigDecimal(0.93F);
		
		user.setRebate(new BigDecimal(0.65F));
		order.setBetAmount(10F);
		
		BigDecimal rebate = null;
		BigDecimal rebateRate = user.getRebate();
		rebateRate = rebateRate.multiply(new BigDecimal(0.01F));
		BigDecimal betAmount = new BigDecimal(order.getBetAmount());
		
		rebate = betAmount.multiply(rebateRate);
		
		postAmount = preAmount.add(rebate);
		
		Assert.assertTrue(rebate.compareTo(new BigDecimal(1)) == 0);
		
	}
	
	//@Test
	public void testCalCabinate() {
		int counter = 0;
		Map<String, Integer> betNumCombination = new HashMap<String, Integer>();
		List<Map<String, Integer>> betNumCombinations = new ArrayList<>();
		for(int i = 0; i < 10 ;i++) {
			for(int ii = 0; ii < 10; ii++) {
				for(int iii = 0; iii < 10; iii++) {
					betNumCombination = new HashMap<String, Integer>();
					StringBuffer buffer = new StringBuffer();
					buffer.append(i).append(ii).append(iii);
					//System.out.println(String.format("current combination  %s ", buffer.toString()));
					
					
					
					if(betNumCombination.get(String.valueOf(i)) == null) {
						betNumCombination.put(String.valueOf(i), 1);
					}else {
						betNumCombination.put(String.valueOf(i), betNumCombination.get(String.valueOf(i)) + 1);
					}
					
					if(betNumCombination.get(String.valueOf(ii)) == null) {
						betNumCombination.put(String.valueOf(ii), 1);
					}else {
						betNumCombination.put(String.valueOf(ii), betNumCombination.get(String.valueOf(ii)) + 1);
					}
					
					if(betNumCombination.get(String.valueOf(iii)) == null) {
						betNumCombination.put(String.valueOf(iii), 1);
					}else {
						betNumCombination.put(String.valueOf(iii), betNumCombination.get(String.valueOf(iii)) + 1);
					}
					
					if(!isBetNumCombinationExisting(betNumCombinations, betNumCombination)) {
						betNumCombinations.add(betNumCombination);
						System.out.println(String.format(" new combination  %s", 
								buffer.toString()));
					}else {
						System.out.println(String.format(" combination  %s is existing", 
								buffer.toString()));
					}
					
					
					
					buffer.delete(0, buffer.length());
					
					counter++;
				}
			}
		}
		
		System.out.println(String.format("total combination  %s ,total assignament %s", 
				betNumCombinations.size(),
				counter));
	}
		
	//@Test
	public void testCalCabinate_2digits() {
		int counter = 0;
		Map<String, Integer> betNumCombination = new HashMap<String, Integer>();
		List<Map<String, Integer>> betNumCombinations = new ArrayList<>();
		for(int i = 0; i < 10 ;i++) {
			for(int ii = 0; ii < 10; ii++) {
				betNumCombination = new HashMap<String, Integer>();
				StringBuffer buffer = new StringBuffer();
				buffer.append(i).append(ii);
				//System.out.println(String.format("current combination  %s ", buffer.toString()));
				
				
				
				if(betNumCombination.get(String.valueOf(i)) == null) {
					betNumCombination.put(String.valueOf(i), 1);
				}else {
					betNumCombination.put(String.valueOf(i), betNumCombination.get(String.valueOf(i)) + 1);
				}
				
				if(betNumCombination.get(String.valueOf(ii)) == null) {
					betNumCombination.put(String.valueOf(ii), 1);
				}else {
					betNumCombination.put(String.valueOf(ii), betNumCombination.get(String.valueOf(ii)) + 1);
				}			
				
				
				if(!isBetNumCombinationExisting(betNumCombinations, betNumCombination)) {
					betNumCombinations.add(betNumCombination);
					System.out.println(String.format(" new combination  %s", 
							buffer.toString()));
				}else {
					System.out.println(String.format(" combination  %s is existing", 
							buffer.toString()));
				}
				
				
				
				buffer.delete(0, buffer.length());
				
				counter++;
				
			}
		}
		
		System.out.println(String.format("total combination  %s ,total assignament %s", 
				betNumCombinations.size(),
				counter));
	}
	
	
	//@Test
	public void testCalCabinate_5digits() {
		int counter = 0;
		Map<String, Integer> betNumCombination = new HashMap<String, Integer>();
		List<Map<String, Integer>> betNumCombinations = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			for (int ii = 0; ii < 10; ii++) {
				for (int iii = 0; iii < 10; iii++) {
					for (int iiii = 0; iiii < 10; iiii++) {
						for (int iiiii = 0; iiiii < 10; iiiii++) {
							betNumCombination = new HashMap<String, Integer>();
							StringBuffer buffer = new StringBuffer();
							buffer.append(i).append(ii).append(iii).append(iiii).append(iiiii);
							// System.out.println(String.format("current combination %s ",
							// buffer.toString()));

							if (betNumCombination.get(String.valueOf(i)) == null) {
								betNumCombination.put(String.valueOf(i), 1);
							} else {
								betNumCombination.put(String.valueOf(i), betNumCombination.get(String.valueOf(i)) + 1);
							}

							if (betNumCombination.get(String.valueOf(ii)) == null) {
								betNumCombination.put(String.valueOf(ii), 1);
							} else {
								betNumCombination.put(String.valueOf(ii),
										betNumCombination.get(String.valueOf(ii)) + 1);
							}

							if (betNumCombination.get(String.valueOf(iii)) == null) {
								betNumCombination.put(String.valueOf(iii), 1);
							} else {
								betNumCombination.put(String.valueOf(iii),
										betNumCombination.get(String.valueOf(iii)) + 1);
							}

							if (betNumCombination.get(String.valueOf(iiii)) == null) {
								betNumCombination.put(String.valueOf(iiii), 1);
							} else {
								betNumCombination.put(String.valueOf(iiii),
										betNumCombination.get(String.valueOf(iiii)) + 1);
							}

							if (betNumCombination.get(String.valueOf(iiiii)) == null) {
								betNumCombination.put(String.valueOf(iiiii), 1);
							} else {
								betNumCombination.put(String.valueOf(iiiii),
										betNumCombination.get(String.valueOf(iiiii)) + 1);
							}
							if (!isBetNumCombinationExisting(betNumCombinations, betNumCombination)) {
								betNumCombinations.add(betNumCombination);
								System.out.println(String.format(" new combination  %s", buffer.toString()));
							} else {
								/*
								 * System.out.println(String.format(" combination  %s is existing",
								 * buffer.toString()));
								 */
							}

							buffer.delete(0, buffer.length());

							counter++;

						}
					}
				}
			}
		}

		System.out.println(
				String.format("total combination  %s ,total assignament %s", betNumCombinations.size(), counter));

	}
		
		
	@Test
	public void testCalCabinate_4digits() {
		int counter = 0;
		Map<String, Integer> betNumCombination = new HashMap<String, Integer>();
		List<Map<String, Integer>> betNumCombinations = new ArrayList<>();
		int fourSame = 0;
		int threeSame = 0;
		int twoSame = 0;
		int nonSame = 0;
		
		for (int i = 0; i < 10; i++) {
			for (int ii = 0; ii < 10; ii++) {
				for (int iii = 0; iii < 10; iii++) {
					for (int iiii = 0; iiii < 10; iiii++) {
						betNumCombination = new HashMap<String, Integer>();
						StringBuffer buffer = new StringBuffer();
						buffer.append(i).append(ii).append(iii).append(iiii);
						// System.out.println(String.format("current combination %s ",
						// buffer.toString()));
						
						if (betNumCombination.get(String.valueOf(i)) == null) {
							betNumCombination.put(String.valueOf(i), 1);
						} else {
							betNumCombination.put(String.valueOf(i), betNumCombination.get(String.valueOf(i)) + 1);
						}
						
						if (betNumCombination.get(String.valueOf(ii)) == null) {
							betNumCombination.put(String.valueOf(ii), 1);
						} else {
							betNumCombination.put(String.valueOf(ii),
									betNumCombination.get(String.valueOf(ii)) + 1);
						}
						
						if (betNumCombination.get(String.valueOf(iii)) == null) {
							betNumCombination.put(String.valueOf(iii), 1);
						} else {
							betNumCombination.put(String.valueOf(iii),
									betNumCombination.get(String.valueOf(iii)) + 1);
						}
						
						if (betNumCombination.get(String.valueOf(iiii)) == null) {
							betNumCombination.put(String.valueOf(iiii), 1);
						} else {
							betNumCombination.put(String.valueOf(iiii),
									betNumCombination.get(String.valueOf(iiii)) + 1);
						}
						
						if (!isBetNumCombinationExisting(betNumCombinations, betNumCombination)) {
							betNumCombinations.add(betNumCombination);
							System.out.println(String.format(" new combination  %s", buffer.toString()));
						} else {
							
							System.out.println(String.format(" combination  %s is existing",
							buffer.toString()));
							 
						}
						
						buffer.delete(0, buffer.length());
						
						counter++;
						
					}
				}
			}
		}
		
		for(Map<String, Integer> map : betNumCombinations) {
			int maxVal = 0;
			Iterator<String> keys = map.keySet().iterator();
			StringBuffer buffer = new StringBuffer();
			while(keys.hasNext()) {
				String key = keys.next();
				int val = map.get(key).intValue();
				if(val > maxVal) {
					maxVal = val;
				}
				
				buffer.append(key).append("=").append(val).append(",");
			}
			
			if(maxVal == 1) {
				nonSame++;
			}else if(maxVal == 2) {
				twoSame++;
				System.out.println(
						String.format("row %s", buffer.toString()));
			}else if(maxVal == 3) {
				threeSame++;
			}else if(maxVal == 4) {
				fourSame++;
			}
		}
		
		

		System.out.println(
				String.format("total combination  %s ,total assignament %s", betNumCombinations.size(), counter));
		
		System.out.println(
				String.format("fourSame  %s ,threeSame %s, twoSame %s,nonSame %s", fourSame, threeSame, twoSame, nonSame));

	}
	
	private boolean isBetNumCombinationExisting(List<Map<String, Integer>> betNumCombinations,
			Map<String, Integer> betNumCombination) {
		StringBuffer bufferII = new StringBuffer();
		Iterator<String> ite = betNumCombination.keySet().iterator();
		while(ite.hasNext()) {
			String key = ite.next();
			Integer val = betNumCombination.get(key);
			bufferII.append(key).append("=").append(val.intValue()).append(",");
		}
		
		for(Map<String, Integer> temp : betNumCombinations) {
			int existingCounter = 0;
			ite = temp.keySet().iterator();
			StringBuffer buffer = new StringBuffer();
			
			while(ite.hasNext()) {
				String key = ite.next();
				Integer val = temp.get(key);
				buffer.append(key).append("=").append(val.intValue()).append(",");
				if(betNumCombination.get(key) != null 
						&& (betNumCombination.get(key).intValue() 
						== temp.get(key).intValue())) {
					existingCounter++;					
				}				
			}
			
			if(existingCounter == temp.size()) {
				System.out.println(
						String.format("existing combination  %s ,new combination %s", 
								buffer.toString(), 
								bufferII.toString()));
				return true;
			}
		}
		return false;
	}
	
}
