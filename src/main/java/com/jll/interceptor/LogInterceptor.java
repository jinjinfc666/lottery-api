package com.jll.interceptor;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.jll.common.annotation.LogsInfo;
import com.jll.common.utils.StringUtils;
import com.jll.dao.SupserDao;
import com.jll.entity.SysLog;
import com.jll.entity.UserAccountDetails;
import com.jll.entity.UserInfo;
import com.jll.sys.log.SysLogService;
import com.jll.user.UserInfoService;



public class LogInterceptor implements HandlerInterceptor{
	
   @Resource
   UserInfoService userInfoService;
   @Resource
   SysLogService sysLogService;
   
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		try{
			HandlerMethod  method = (HandlerMethod)arg2;
			
			LogsInfo logs = method.getMethod().getAnnotation(LogsInfo.class);
			if(null != logs){
				UserInfo curUser = 	userInfoService.getCurLoginInfo();
				if(null != curUser){
					SysLog log = new SysLog();
					log.setLogOpeType(logs.logType());
					log.setLogType(logs.logType());
					log.setUserId(curUser.getId());
					JSONObject json = new JSONObject(StringUtils.getBodyString(request).replaceAll("\t", "").replaceAll("\r", "").replaceAll("\n", ""));
					json.put("reqUrl", request.getRequestURI());
					log.setLogData(json.toString());
					sysLogService.saveOrUpdate(log);
				}
			}
		}catch (Exception e) {
				// TODO: handle exception
		}
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse arg1, Object arg2) throws Exception {
		return true;
	}

}
