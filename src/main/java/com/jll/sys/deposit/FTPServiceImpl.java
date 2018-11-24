package com.jll.sys.deposit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.jll.common.constants.Message;  
@Service
@Transactional
@PropertySource("classpath:selfPay-setting.properties")
public class FTPServiceImpl implements FTPService {    
	@Value("${self_pay_img_address}")
	String address;
    private  FTPClient ftp;    
    /** 
     *  
     * @param path 上传到ftp服务器哪个路径下    
     * @param addr 地址 
     * @param port 端口号 
     * @param username 用户名 
     * @param password 密码 
     * @return 
     * @throws Exception 
     */  
    @Override
    public  boolean connect(String path,String addr,int port,String username,String password) throws Exception{    
        boolean result = false;    
        ftp = new FTPClient();    
        int reply; 
    	ftp.connect(addr,port);    
        ftp.login(username,password);    
        ftp.setFileType(FTPClient.BINARY_FILE_TYPE);    
        reply = ftp.getReplyCode();    
        if (!FTPReply.isPositiveCompletion(reply)) {    
            ftp.disconnect();    
            return result;    
        }    
        ftp.changeWorkingDirectory(path);
        result = true;    
        return result;    
    }    
    /** 
     *  
     * @param file 上传的文件或文件夹 
     * @throws Exception 
     */  
    @Override
    public void upload(File file) throws Exception{    
        if(file.isDirectory()){  
    		ftp.makeDirectory(file.getName());              
            ftp.changeWorkingDirectory(file.getName());    
            String[] files = file.list();           
            for (int i = 0; i < files.length; i++) {    
                File file1 = new File(file.getPath()+"\\"+files[i] );    
                if(file1.isDirectory()){    
                    upload(file1);    
                    ftp.changeToParentDirectory();    
                }else{                  
                    File file2 = new File(file.getPath()+"\\"+files[i]);    
                    FileInputStream input = new FileInputStream(file2);    
                    ftp.storeFile(file2.getName(), input);    
                    input.close();                          
                }               
            }
        }else{   
            File file2 = new File(file.getPath());    
            FileInputStream input = new FileInputStream(file2);    
            ftp.storeFile(file2.getName(), input);    
            input.close(); 
        }    
    }    
//    @Override
//    public Map<String,Object> upload(String imgName) throws Exception{
//    	String ip="110.92.64.70";
//    	String imgNameNew="D:\\img\\"+imgName;
//    	String location=imgNameNew.replace("\\\\", "/");
////    	String ip=address;
//		Map<String,Object> map=new HashMap<String,Object>();
//		this.connect("/home/jinlilai", ip, 21, "jinlilai", "jinlilai");
//		File file = new File(location);
//		this.upload(file); 
//		map.clear();
//		map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
//		return map;
//   }  
    @Override
    public Map<String,Object>  springUpload(HttpServletRequest request,HttpServletResponse response) throws Exception
    {
    	 Map<String,Object> map=new HashMap<String,Object>();
         //将当前上下文初始化给  CommonsMutipartResolver （多部分解析器）
        CommonsMultipartResolver multipartResolver=new CommonsMultipartResolver(
                request.getSession().getServletContext());
        //检查form中是否有enctype="multipart/form-data"
        if(multipartResolver.isMultipart(request))
        {
            //将request变成多部分request
            MultipartHttpServletRequest multiRequest=(MultipartHttpServletRequest)request;
           //获取multiRequest 中所有的文件名
            Iterator iter=multiRequest.getFileNames();
             
            while(iter.hasNext())
            {
                //一次遍历所有文件
                MultipartFile file=multiRequest.getFile(iter.next().toString());
                if(file!=null)
                {
                	String ip="110.92.64.70";
//                	String ip=address;
                	map=new HashMap<String,Object>();
                    //上传
            		this.connect("/home/jinlilai", ip, 21, "jinlilai", "jinlilai");
					InputStream ins = file.getInputStream();
					File f =new File(file.getOriginalFilename());
				    inputStreamToFile(ins, f);
                    this.upload(f);
				    File del = new File(f.toURI());
			   		del.delete();
            		map.clear();
            		map.put(Message.KEY_DATA, file.getOriginalFilename());
            		map.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
                }
            }
        }
		return map;
    }
	public static void inputStreamToFile(InputStream ins,File file) {
		  try {
		   OutputStream os = new FileOutputStream(file);
		   int bytesRead = 0;
		   byte[] buffer = new byte[8192];
		   while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
		    os.write(buffer, 0, bytesRead);
		   }
		   os.close();
		   ins.close();
		  } catch (Exception e) {
		   e.printStackTrace();
		  }
	}
}  