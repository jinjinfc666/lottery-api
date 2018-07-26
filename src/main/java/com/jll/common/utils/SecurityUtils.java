package com.jll.common.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class SecurityUtils {
	
	public final static String PERMISSION_ROLE_USER_INFO = "ROLE_USER_INFO";
	public final static String PERMISSION_ROLE_ADMIN = "ROLE_ADMIN";
	
	public static boolean checkPermissionIsOK(Authentication auth,String permssion){
		for(GrantedAuthority ga : auth.getAuthorities()) {  
            if(permssion.toUpperCase().equals(ga.getAuthority().toUpperCase())) {  
                return true;  
            }  
        } 
		return false;
	}
	
	
}
