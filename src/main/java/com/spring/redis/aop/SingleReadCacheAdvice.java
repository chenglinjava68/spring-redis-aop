package com.spring.redis.aop;


import com.spring.redis.annotation.ReadThroughAssignCache;
import com.spring.redis.cache.RedisCacheService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;


abstract class SingleReadCacheAdvice extends CacheAdvice {

	@Autowired
	private RedisCacheService redisCacheService;
	
    protected Object cache(final ProceedingJoinPoint pjp) throws Throwable {

        
        Method method = getMethod(pjp);
        ReadThroughAssignCache cacheable=method.getAnnotation(ReadThroughAssignCache.class);
        
        final Signature sig = pjp.getSignature();
        final MethodSignature msig = (MethodSignature) sig;


		// 获取 KEY规则
		String namespace = cacheable.namespace();
		String assignedKey = cacheable.assignedKey();

		Annotation[][] anns = method.getParameterAnnotations();

		String key = getCacheKey(namespace, assignedKey, anns, pjp.getArgs());
		Object value = null;
		value = redisCacheService.get(key, cacheable.valueClass());
		if (value == null) {
			value = pjp.proceed();
			if (value != null) {
				redisCacheService.set(key, value, cacheable.expireTime());
			}
		}
		return value;

	}
    
}
