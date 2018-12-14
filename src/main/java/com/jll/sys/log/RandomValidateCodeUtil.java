package com.jll.sys.log;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.druid.util.Base64;
import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Message;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class RandomValidateCodeUtil{

	CacheRedisService cacheRedisService;
	private static Validate validate = null;  
    public static final String RANDOMCODEKEY= "RANDOMVALIDATECODEKEY";//放到session中的key
//    private String randString = "0123456789";//随机产生只有数字的字符串 private String
    //private String randString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";//随机产生只有字母的字符串
    private String randString = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";//随机产生数字与字母组合的字符串  去掉1 0 i o这四个比较相近的
    private int width = 95;// 图片宽
    private int height = 25;// 图片高
    private int lineSize = 40;// 干扰线数量
    private int stringNum = 4;// 随机产生字符数量

    private static final Logger logger = Logger.getLogger(RandomValidateCodeUtil.class);

    private Random random = new Random();
    
    public RandomValidateCodeUtil(CacheRedisService cacheRedisService) {
    	this.cacheRedisService=cacheRedisService;
    }
    /**
     * 获得字体
     */
    private Font getFont() {
        return new Font("Fixedsys", Font.CENTER_BASELINE, 18);
    }

    /**
     * 获得颜色
     */
    private Color getRandColor(int fc, int bc) {
        if (fc > 255)
            fc = 255;
        if (bc > 255)
            bc = 255;
        int r = fc + random.nextInt(bc - fc - 16);
        int g = fc + random.nextInt(bc - fc - 14);
        int b = fc + random.nextInt(bc - fc - 18);
        return new Color(r, g, b);
    }

    /**
     * 生成随机图片
     */
    public Validate  getRandcode(HttpServletRequest request, HttpServletResponse response) {
    	Map<String,Object> map=new HashMap<String,Object>();
    	validate = validate==null?new Validate():validate;
        HttpSession session = request.getSession();
        // BufferedImage类是具有缓冲区的Image类,Image类是用于描述图像信息的类
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        Graphics g = image.getGraphics();// 产生Image对象的Graphics对象,改对象可以在图像上进行各种绘制操作
        g.fillRect(0, 0, width, height);//图片大小
        g.setFont(new Font("Times New Roman", Font.ROMAN_BASELINE, 18));//字体大小
        g.setColor(getRandColor(110, 133));//字体颜色
        // 绘制干扰线
        for (int i = 0; i <= lineSize; i++) {
            drowLine(g);
        }
        // 绘制随机字符
        String randomString = "";
        for (int i = 1; i <= stringNum; i++) {
            randomString = drowString(g, randomString, i);
        }
        logger.info(randomString);
        //将生成的随机字符串保存到session中
        String sessionId=session.getId();
        String key=sessionId;
        cacheRedisService.setSessionIdCaptcha(key, randomString);
//        cacheRedisService.deleteSessionIdCaptcha(key);
        logger.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+randomString+"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//        session.setAttribute(key, randomString);
        g.dispose();
        ByteArrayOutputStream bs = null;
        try {
        	bs = new ByteArrayOutputStream();
            // 将内存中的图片通过流动形式输出到客户端
            ImageIO.write(image, "jpg", bs);
        	String imgsrc = Base64.byteArrayToBase64(bs.toByteArray());
            validate.setBase64Str(imgsrc);
        } catch (Exception e) {
            logger.error("将内存中的图片通过流动形式输出到客户端失败>>>>   ", e);
            throw new RuntimeException(Message.Error.ERROR_LOGIN_FAILED_TO_GET_VERIFICATION_CODE.getErrorMes());
        }finally{
        	try {
				bs.close();
			} catch (IOException e) {
				logger.error("将内存中的图片通过流动形式输出到客户端失败>>>>   ", e);
	            throw new RuntimeException(Message.Error.ERROR_LOGIN_FAILED_TO_GET_VERIFICATION_CODE.getErrorMes());
			}finally{
				bs = null;
			}
        }
        return validate;
    }

    /**
     * 绘制字符串
     */
    private String drowString(Graphics g, String randomString, int i) {
        g.setFont(getFont());
        g.setColor(new Color(random.nextInt(101), random.nextInt(111), random
                .nextInt(121)));
        String rand = String.valueOf(getRandomString(random.nextInt(randString
                .length())));
        randomString += rand;
        g.translate(random.nextInt(3), random.nextInt(3));
        g.drawString(rand, 13 * i, 16);
        return randomString;
    }

    /**
     * 绘制干扰线
     */
    private void drowLine(Graphics g) {
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        int xl = random.nextInt(13);
        int yl = random.nextInt(15);
        g.drawLine(x, y, x + xl, y + yl);
    }

    /**
     * 获取随机的字符
     */
    public String getRandomString(int num) {
        return String.valueOf(randString.charAt(num));
    }
    public static class Validate implements Serializable{
    	private static final long serialVersionUID = 1L;
    	private String Base64Str;		//Base64 值
    	private String value;			//验证码值
    	
    	public String getBase64Str() {
    		return Base64Str;
    	}
    	public void setBase64Str(String base64Str) {
    		Base64Str = base64Str;
    	}
    	public String getValue() {
    		return value;
    	}
    	public void setValue(String value) {
    		this.value = value;
    	}
    }
}
