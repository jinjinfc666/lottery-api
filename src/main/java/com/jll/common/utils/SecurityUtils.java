package com.jll.common.utils;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class SecurityUtils {
	
	public final static String PERMISSION_ROLE_USER_INFO = "ROLE_USER_INFO";
	public final static String PERMISSION_ROLE_ADMIN = "ROLE_ADMIN";
	public final static String PERMISSION_ROLE_AGENT ="ROLE_AGENT";
	public final static String PERMISSION_ROLE_MANAGER ="ROLE_MANAGER";
	
	public static boolean checkPermissionIsOK(List<String> auth,String permssion){
		for(String s : auth) {  
            if(permssion.toUpperCase().equals(s.toUpperCase())) {  
                return true;  
            }  
        } 
		return false;
	}
	public static boolean checkViewPermissionIsOK(List<String> auth){
		for(String s : auth) {  
            if(PERMISSION_ROLE_AGENT.toUpperCase().equals(s.toUpperCase())) {  
                return true;  
            }
            if(PERMISSION_ROLE_MANAGER.toUpperCase().equals(s.toUpperCase())) {  
                return true;  
            }   
        } 
		return false;
	}
	
}
