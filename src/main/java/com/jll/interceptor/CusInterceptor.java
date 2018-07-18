package com.jll.interceptor;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class CusInterceptor
  implements Filter
{
  public void destroy() {}
  
  public void doFilter(ServletRequest arg0, ServletResponse resp, FilterChain arg2)
    throws IOException, ServletException
  {
    HttpServletResponse response = (HttpServletResponse)resp;
    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS");
    response.setHeader("Access-Control-Allow-Headers", "x-requested-with,content-type");
    
    arg2.doFilter(arg0, resp);
  }
  
  public void init(FilterConfig arg0)
    throws ServletException
  {}
}
