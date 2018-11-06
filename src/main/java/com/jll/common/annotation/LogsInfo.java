package com.jll.common.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jll.common.utils.StringUtils;


@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({
	ElementType.TYPE,
	ElementType.METHOD,
	ElementType.PARAMETER,
	ElementType.FIELD,
})
public @interface LogsInfo {
    String logType() default StringUtils.EMPTY;
    
}
