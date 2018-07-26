package com.ehome.test;


import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.meterware.servletunit.ServletTestCase;

public class ServiceJunitBase extends ServletTestCase {
	
	protected ApplicationContext ctx = null;
	
	public ServiceJunitBase(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		// 通过代码设置并启动一个服务器，该服务器是servlet的测试容器
		super.setUp();
		
		String[] paths = {"file:/home/nick/workspace/stock/src/main/resources/applicationContext.xml"};
		ctx = new FileSystemXmlApplicationContext(paths);
	}

	@Override
	protected void tearDown() throws Exception {
		
	}
}