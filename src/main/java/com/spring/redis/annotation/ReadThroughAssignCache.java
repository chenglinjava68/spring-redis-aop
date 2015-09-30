package com.spring.redis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ReadThroughAssignCache {  
	
	/**
	 * 命名空间,也是KEY的前缀
	 * @return
	 */
	String namespace() default AnnotationConstants.DEFAULT_STRING;
	/**
	 * 存储的KEY
	 * @return
	 */
	String assignedKey() default AnnotationConstants.DEFAULT_STRING;

	/**
	 * 有效时间,默认永久有效
	 * @return
	 */
	int expireTime() default 0;  
	

	/**
	 * 默认String类型 存储类型
	 * @return
	 */
	Class<?> valueClass() default String.class;
}
