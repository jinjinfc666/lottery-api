package com.jll.sys.deposit;

import java.io.File;
import java.util.Map;


public interface FTPService
{	
	boolean connect(String path,String addr,int port,String username,String password) throws Exception;
	void upload(File file) throws Exception;
	public Map<String,Object> upload(String imgName) throws Exception;
}
