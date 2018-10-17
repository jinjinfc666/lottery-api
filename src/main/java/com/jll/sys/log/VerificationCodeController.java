package com.jll.sys.log;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Message;
import com.jll.common.constants.Constants.UserType;
import com.jll.entity.UserInfo;

@RestController
@RequestMapping({"/captchas"})
public class VerificationCodeController {
	private Logger logger = Logger.getLogger(VerificationCodeController.class);
	@Resource
	CacheRedisService cacheRedisService;
	//只查询当前表
	@RequestMapping(value={"/verification-code-Img"}, method={RequestMethod.GET}, produces={"image/jpeg"})
	public void getVerificationCodeImg(HttpServletRequest request, HttpServletResponse response) {
		try {
//	        response.setContentType("image/jpeg");//设置相应类型,告诉浏览器输出的内容为图片
	        response.setHeader("Pragma", "No-cache");//设置响应头信息，告诉浏览器不要缓存此内容
	        response.setHeader("Cache-Control", "no-cache");
	        response.setDateHeader("Expire", 0);
	        RandomValidateCodeUtil randomValidateCode = new RandomValidateCodeUtil(cacheRedisService);
	        randomValidateCode.getRandcode(request, response);//输出验证码图片方法
	    } catch (Exception e) {
	        logger.error("获取验证码失败>>>>   ", e);
	    }
	}
	@RequestMapping(value={"/query-sesionid"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> getSesionid(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> ret = new HashMap<>();
		Map<String, Object> map = new HashMap<>();
		try {
			HttpSession session = request.getSession();
			String sessionId=session.getId();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			map.put("sessionId", sessionId);
			ret.put("data", map);
			return ret;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
	@RequestMapping(method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> getCapcha(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> ret = new HashMap<>();
		Map<String, Object> map = new HashMap<>();
		try {
			HttpSession session = request.getSession();
			String sessionId = session.getId();
			//String sessionId = request.getParameter("jsSessionId");
			String key=sessionId;
			String saveCaptcha = cacheRedisService.getSessionIdCaptcha(key);
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			map.put("captcha", saveCaptcha);
			ret.put("data", map);
			return ret;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
	
}
