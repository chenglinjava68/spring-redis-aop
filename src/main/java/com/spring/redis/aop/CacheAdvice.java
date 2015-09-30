package com.spring.redis.aop;


import com.spring.redis.annotation.AnnotationConstants;
import com.spring.redis.annotation.CacheKeyMethod;
import com.spring.redis.annotation.ParameterValueKeyProvider;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * aop基类
 *
 */
public abstract class CacheAdvice  {


	private static final Logger LOGGER = LoggerFactory.getLogger(CacheAdvice.class);


	private final Map<Class<?>, Method> map = new ConcurrentHashMap();

	/**
     * 获取方法 注解参数 和参数的注解
     * @param pjp
     * @return
     */
    protected Method getMethod(ProceedingJoinPoint pjp) {
		// 获取参数的类型
		Object[] args = pjp.getArgs();
		@SuppressWarnings("rawtypes")
		Class[] argTypes = new Class[pjp.getArgs().length];
		for (int i = 0; i < args.length; i++) {
			argTypes[i] = args[i].getClass();
		}
		Method method = null;
		try {
			method = pjp.getTarget().getClass().getMethod(pjp.getSignature().getName(), argTypes);
		} catch (NoSuchMethodException e) {
			LOGGER.error("CacheAdvice NoSuchMethodException",e);
		} catch (SecurityException e) {
			LOGGER.error("CacheAdvice SecurityException", e);
		}
		return method;
	}
    
    protected String getCacheKey(String namespace,String assignedKey,Annotation[][] ans,Object[] args){
    	StringBuilder sb = new StringBuilder();
    	if( namespace != null && !namespace.equals("") && !namespace.equals(AnnotationConstants.DEFAULT_STRING)) {
    		sb.append(namespace);
    		sb.append("_");
    	}
    	if( assignedKey != null && !assignedKey.equals("") && !assignedKey.equals(AnnotationConstants.DEFAULT_STRING) ) {
    		sb.append(assignedKey);
    		sb.append("_");
    	}
    	
    	if( ans != null && ans.length >0){
    		for(Annotation[] anitem : ans){
    			if(anitem.length > 0 && anitem[0] instanceof ParameterValueKeyProvider){
	            	for(Annotation an : anitem){
            			ParameterValueKeyProvider vk = (ParameterValueKeyProvider)an;
            			sb.append(generateKey(args[vk.order()]));
            			sb.append("_");
	            	}
    			}
            }
    	}
    	
    	return sb.toString();
    }


	/**
	 * 生成key
	 * @param keyObject
	 * @return
	 */
	public String generateKey(Object keyObject) {
		if(keyObject == null) {
			throw new InvalidParameterException("keyObject must be defined");
		} else {
			try {
				Method ex = this.getKeyMethod(keyObject);
				return this.generateObjectId(ex, keyObject);
			} catch (Exception var3) {
				throw new RuntimeException(var3);
			}
		}
	}


	Method getKeyMethod(Object keyObject) throws NoSuchMethodException {
		return getKeyMethod(keyObject.getClass());
	}

	String generateObjectId(Method keyMethod, Object keyObject) throws Exception {
		String objectId = (String)keyMethod.invoke(keyObject, (Object[])null);
		if(objectId != null && objectId.length() >= 1) {
			return objectId;
		} else {
			throw new RuntimeException("Got an empty key value from " + keyMethod.getName());
		}
	}



	public Method getKeyMethod(Class<?> keyClass) throws NoSuchMethodException {
		Method storedMethod = this.find(keyClass);
		if(storedMethod != null) {
			return storedMethod;
		} else {
			Method[] methods = keyClass.getDeclaredMethods();
			Method targetMethod = null;
			Method[] arr$ = methods;
			int len$ = methods.length;

			for(int i$ = 0; i$ < len$; ++i$) {
				Method method = arr$[i$];
				boolean isCacheKeyMethod = this.isCacheKeyMethod(method);
				if(isCacheKeyMethod && targetMethod != null) {
					LOGGER.error(String.format("Class [%s] should have only one method annotated with [%s]. See [%s] " +
							"and [%s]", new Object[]{keyClass.getName(), CacheKeyMethod.class.getName(), targetMethod
							.getName(), method.getName()}));
				}

				if(isCacheKeyMethod) {
					targetMethod = method;
				}
			}

			if(targetMethod == null) {
				targetMethod = keyClass.getMethod("toString", (Class[])null);
			}

			this.add(keyClass, targetMethod);
			return targetMethod;
		}
	}

	/**
	 * 判断是否存在CacheKeyMethod注解
	 * @param method
	 * @return
	 */
	private boolean isCacheKeyMethod(Method method) {
		if(method != null && method.getAnnotation(CacheKeyMethod.class) != null) {
			if(method.getParameterTypes().length > 0) {
				LOGGER.error(String.format("Method [%s] must have 0 arguments to be annotated with [%s]", new Object[]{method.toString(), CacheKeyMethod.class.getName()}));
				return false;
			} else if(!String.class.equals(method.getReturnType())) {
				LOGGER.error(String.format("Method [%s] must return a String to be annotated with [%s]", new Object[]{method.toString(), CacheKeyMethod.class.getName()}));
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	private void add(Class<?> key, Method value) {
		this.map.put(key, value);
	}

	private Method find(Class<?> key) {
		return (Method)this.map.get(key);
	}


    protected abstract Logger getLogger();

}
