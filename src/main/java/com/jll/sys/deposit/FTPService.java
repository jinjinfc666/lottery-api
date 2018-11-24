package com.jll.sys.deposit;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public interface FTPService
{	
	boolean connect(String path,String addr,int port,String username,String password) throws Exception;
	void upload(File file) throws Exception;
//	public Map<String,Object> upload(String imgName) throws Exception;
	public Map<String,Object>  springUpload(HttpServletRequest request,HttpServletResponse response) throws Exception;
}
