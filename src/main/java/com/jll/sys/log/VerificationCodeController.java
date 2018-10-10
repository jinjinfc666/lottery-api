package com.jll.sys.log;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jll.common.constants.Message;

@RestController
@RequestMapping({"/captcha"})
public class VerificationCodeController {
	private Logger logger = Logger.getLogger(VerificationCodeController.class);
	//只查询当前表
	@RequestMapping(value={"/VerificationCodeImg"}, method={RequestMethod.GET}, produces={"application/json"})
	public void getVerificationCodeImg(HttpServletRequest request, HttpServletResponse response) {
		try {
	        response.setContentType("image/jpeg");//设置相应类型,告诉浏览器输出的内容为图片
	        response.setHeader("Pragma", "No-cache");//设置响应头信息，告诉浏览器不要缓存此内容
	        response.setHeader("Cache-Control", "no-cache");
	        response.setDateHeader("Expire", 0);
	        RandomValidateCodeUtil randomValidateCode = new RandomValidateCodeUtil();
	        randomValidateCode.getRandcode(request, response);//输出验证码图片方法
	    } catch (Exception e) {
	        logger.error("获取验证码失败>>>>   ", e);
	    }
	}
}
