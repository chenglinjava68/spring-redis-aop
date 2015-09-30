package com.spring.redis;

import java.util.List;

public interface UserBiz {

	public User find(String uid);

	public User find(String uid,String name);

	public User insert(User vo);
	
	public boolean delete(String uid);
	
	public User update( User vo);

	public List<User> list(String uid);
}
