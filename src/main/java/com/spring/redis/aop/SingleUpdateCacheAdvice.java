package com.spring.redis.aop;


import com.spring.redis.annotation.UpdateThroughAssignCache;
import com.spring.redis.cache.RedisCacheService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 添加 或者 更新 都走这个类
 *
 */
abstract class SingleUpdateCacheAdvice extends CacheAdvice {

	@Autowired
	private RedisCacheService redisCacheService;
	
    protected Object update(final ProceedingJoinPoint pjp) throws Throwable {

        
        Method method = getMethod(pjp);
        UpdateThroughAssignCache cacheable=method.getAnnotation(UpdateThroughAssignCache.class);
        
        final Signature sig = pjp.getSignature();
        final MethodSignature msig = (MethodSignature) sig;


		// 获取 KEY规则
		String namespace = cacheable.namespace();
		String assignedKey = cacheable.assignedKey();

		Annotation[][] anns = method.getParameterAnnotations();

		String key = getCacheKey(namespace, assignedKey, anns, pjp.getArgs());
		Object value = null;
		value = pjp.proceed();
		if (value != null) {
			redisCacheService.set(key, value, cacheable.expireTime());
		}
		return value;

	}
    
}
