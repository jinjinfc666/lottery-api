//package com.ehome.stock.datasync.service;
//
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.support.ClassPathXmlApplicationContext;
//import org.springframework.context.support.FileSystemXmlApplicationContext;
//
//import com.ehome.stock.beans.Page;
//import com.ehome.stock.beans.StockExchange;
//import com.ehome.stock.common.constants.Constants;
//import com.ehome.test.ControllerJunitBase;
//import com.ehome.test.ServiceJunitBase;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.meterware.httpunit.GetMethodWebRequest;
//import com.meterware.httpunit.WebConversation;
//import com.meterware.httpunit.WebRequest;
//import com.meterware.httpunit.WebResponse;
//
//import junit.framework.Assert;
//
//public class StockExchangeServiceTest  extends ServiceJunitBase{
//
//	@Resource
//	StockExcSyncService stockExcSyncService;
//		
//	public StockExchangeServiceTest(String name) {
//		super(name);
//		// TODO Auto-generated constructor stub
//	}
//	
//	@Override
//	protected void setUp() throws Exception {
//		super.setUp();
//		stockExcSyncService = (StockExcSyncService)ctx.getBean("stockExcSyncService");
//	}
//
//	@Override
//	protected void tearDown() throws Exception {
//		//super.tearDown();
//	}
//	
//	public void ItestAddStockExchange(){
//		StockExchange stockExchange = new StockExchange();
//		stockExchange.setAmount(1804D);
//		stockExchange.setCurrentPrice(4.73D);
//		stockExchange.setExchangeTime(new Date());
//		stockExchange.setPbRatio(1.27D);
//		stockExchange.setPERatio(17.44D);
//		stockExchange.setStockId("0010");
//		stockExchange.setTurnover(38200.00D);
//		
//		int ret = stockExcSyncService.addStockExc(stockExchange);
//		Assert.assertEquals(Constants.Status.SUCCESS.getValue(), ret);
//	}
//	
//	public void ItestAddStockExchange_noStock(){
//		StockExchange stockExchange = new StockExchange();
//		stockExchange.setAmount(1804D);
//		stockExchange.setCurrentPrice(4.73D);
//		stockExchange.setExchangeTime(new Date());
//		stockExchange.setPbRatio(1.27D);
//		stockExchange.setPERatio(17.44D);
//		stockExchange.setStockId("9010");
//		stockExchange.setTurnover(38200.00D);
//		
//		int ret = stockExcSyncService.addStockExc(stockExchange);
//		Assert.assertEquals(Constants.ErrorCode.STOCK_NONEXISTING.getErrorId(), ret);
//	}
//	
//	public void ItestAddStockExchange_StockExchangeExisting(){
//		Date exchangeTime = new Date();
//		
//		StockExchange stockExchange = new StockExchange();
//		stockExchange.setAmount(1804D);
//		stockExchange.setCurrentPrice(4.73D);
//		stockExchange.setExchangeTime(exchangeTime);
//		stockExchange.setPbRatio(1.27D);
//		stockExchange.setPERatio(17.44D);
//		stockExchange.setStockId("0010");
//		stockExchange.setTurnover(38200.00D);
//		
//		int ret = stockExcSyncService.addStockExc(stockExchange);
//		Assert.assertEquals(Constants.Status.SUCCESS.getValue(), ret);
//		
//		//update
//		stockExchange = new StockExchange();
//		stockExchange.setAmount(1804D);
//		stockExchange.setCurrentPrice(4.73D);
//		stockExchange.setExchangeTime(exchangeTime);
//		stockExchange.setPbRatio(1.27D);
//		stockExchange.setPERatio(17.44D);
//		stockExchange.setStockId("0010");
//		stockExchange.setTurnover(38200.00D);
//		
//		ret = stockExcSyncService.addStockExc(stockExchange);
//		Assert.assertEquals(Constants.Status.SUCCESS.getValue(), ret);
//	}
//	
//	public void ItestQueryStockExcById(){		
//		String stockExchangeId = "00000000";
//		
//		StockExchange stockExchange = stockExcSyncService.queryStockExcById(stockExchangeId);
//		Assert.assertNotNull(stockExchange);
//		
//	}
//	
//	public void ItestQueryPage(){		
//		Map<String, Object> queryParams = new HashMap<String, Object>();
//		queryParams.put(Constants.REQ_PARAM_STOCK_PAGE_SIZE, "10");
//		queryParams.put(Constants.REQ_PARAM_STOCK_PAGE_INDEX, "1");
//		queryParams.put("stockId", "0010");
//		Page<StockExchange> page = stockExcSyncService.queryPage(queryParams);
//		Assert.assertNotNull(page);
//		
//	}
//	
//	public void testLessAndEqual() throws Exception{		
//		String stockCode = "002038";
//		Map<String, Object> queryParams = new HashMap<String, Object>();
//		try {
//			queryParams.put(Constants.REQ_PARAM_STOCK_PAGE_INDEX, String.valueOf(0));
//			queryParams.put(Constants.REQ_PARAM_STOCK_PAGE_SIZE, String.valueOf(10000));
//			queryParams.put(Constants.REQ_PARAM_STOCK_INFO_CODE, stockCode);
//			queryParams.put(Constants.REQ_PARAM_STOCK_TIME_RANGE_TYPE,
//					Constants.TimeRangeType.ONE_MONTH.getValue());
//			
//			Map<String, Long> retItems = stockExcSyncService.lessAndEqualCurrentPrice(queryParams);
//			
//			
//			Assert.assertNotNull(retItems);
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw e;
//		}
//		
//	}
//}
