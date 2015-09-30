package com.spring.redis;

import com.spring.redis.annotation.CacheKeyMethod;

import java.io.Serializable;

public class User implements Serializable {


	private static final long serialVersionUID = 3202011883696763443L;


	private String userId;
	
	private String name ;
	
	private String sex;
	
	private int age;
	
	private String iphone;

	public final String getUserId() {
		return userId;
	}

	public final void setUserId(String userId) {
		this.userId = userId;
	}

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final String getSex() {
		return sex;
	}

	public final void setSex(String sex) {
		this.sex = sex;
	}

	public final int getAge() {
		return age;
	}

	public final void setAge(int age) {
		this.age = age;
	}

	public final String getIphone() {
		return iphone;
	}

	public final void setIphone(String iphone) {
		this.iphone = iphone;
	}

	@Override
	public String toString() {
		return "User{" +
				"userId='" + userId + '\'' +
				", name='" + name + '\'' +
				", sex='" + sex + '\'' +
				", age=" + age +
				", iphone='" + iphone + '\'' +
				'}';
	}

	@CacheKeyMethod
	public String cacheKey() {
		return this.userId +"" ;
	}
}
