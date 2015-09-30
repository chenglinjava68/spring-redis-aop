package com.spring.redis.aop;


import com.spring.redis.annotation.DeleteThroughAssignCache;
import com.spring.redis.cache.RedisCacheService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;


abstract class SingleDeleteCacheAdvice extends CacheAdvice {

	@Autowired
	private RedisCacheService redisCacheService;
	
    protected Object delete(final ProceedingJoinPoint pjp) throws Throwable {

        
        Method method = getMethod(pjp);
        DeleteThroughAssignCache cacheable=method.getAnnotation(com.spring.redis.annotation.DeleteThroughAssignCache.class);
        
        final Signature sig = pjp.getSignature();
        final MethodSignature msig = (MethodSignature) sig;


		// 获取 KEY规则
		String namespace = cacheable.namespace();
		String assignedKey = cacheable.assignedKey();

		Annotation[][] anns = method.getParameterAnnotations();

		String key = getCacheKey(namespace, assignedKey, anns, pjp.getArgs());
		redisCacheService.remove(key);
		return pjp.proceed();

    }
    
}
