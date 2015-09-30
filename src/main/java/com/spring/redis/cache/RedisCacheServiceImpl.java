package com.spring.redis.cache;


import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.util.*;

@Service("redisCacheService")
public class RedisCacheServiceImpl implements RedisCacheService {

	private static final Logger LOGGER =
			LoggerFactory.getLogger(RedisCacheServiceImpl.class);

	@Autowired
	private ShardedJedisPool shardedJedisPool;

	private Gson gson = new Gson();


	//-------------------String--------------------------
	@Override
	public void set(String key, Object value,int expire)  {


		String serStr = ObjectConverter.objectSerialization(value);

		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			jedis.set(key, serStr);
			if(expire >0 )
				jedis.expire(key, expire);
		} catch (Exception e) {
			LOGGER.warn("redis set exception ",e);
		} finally {
			shardedJedisPool.returnResourceObject(jedis);
		}

	}

	@Override
	public <T> T get(String key, Class<T> cls)  {

 		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			String deSerStr = jedis.get(key);
			return deSerStr==null||deSerStr==""?null :
					(T)ObjectConverter.objectDeserialization(deSerStr);
		} catch (Exception e) {
			LOGGER.warn("redis get exception ",e);
		} finally {
			shardedJedisPool.returnResourceObject(jedis);
		}

		return null;
	}

	//-------------------set--------------------------
	@Override
	public Long scard(String key)  {
		Long count = 0l;
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			count = jedis.scard(key);
		} catch (Exception e) {
			LOGGER.warn("redis scard exception ",e);
		} finally {
			shardedJedisPool.returnResourceObject(jedis);
		}
		return count;
	}

	@Override
	public Long sadd(String key, Object... members)  {
		Long rows = 0l;
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			String[] item = new String[members.length];
			int i = 0;
			for(Object obj :members){
				item[i] = gson.toJson(obj);
				i++ ;
			}
			rows = jedis.sadd(key,item);
		} catch (Exception e) {
			LOGGER.warn("redis sadd exception ",e);
		} finally {
			shardedJedisPool.returnResourceObject(jedis);
		}
		return rows;
	}

	@Override
	public Long srem(String key, Object... members)  {
		Long rows = 0l;
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			String[] item = new String[members.length];
			int i = 0;
			for(Object obj :members){
				item[i] = gson.toJson(obj);
				i++ ;
			}
			rows = jedis.srem(key,item);
		} catch (Exception e) {
			LOGGER.warn("redis srem exception ",e);
		} finally {
			shardedJedisPool.returnResourceObject(jedis);
		}
		return rows;
	}

	@Override
	public <T> Set<T> smembers(String key,Class<T> t)  {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			Set<String> sItem  = jedis.smembers(key);

			Set<T> result = new HashSet<T>();
			 Iterator<String> rt = sItem.iterator();
			while(rt.hasNext()){
				result.add(gson.fromJson(rt.next(), t));
			}
			return result;
		} catch (Exception e) {
			LOGGER.warn("redis smembers exception ",e);
		} finally {
			shardedJedisPool.returnResourceObject(jedis);
		}

		return null;
	}

	@Override
	public Long sremove(String key)  {
		Long result = 0L;
    	ShardedJedis jedis = null;
        try
        {
        	jedis = shardedJedisPool.getResource();
            result = jedis.del(key);
        } catch (Exception e) {
			LOGGER.warn("redis sremove exception ",e);
		} finally {
			shardedJedisPool.returnResourceObject(jedis);
		}
        return result;
	}

	//-------------------map--------------------------
	@Override
	public void setMap(String key, String mapKey, Object value) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			String jsonValue = gson.toJson(value);
			jedis.hset(key, mapKey, jsonValue);
		} catch (Exception e) {
			LOGGER.warn("redis setMap exception ",e);
		} finally {
			shardedJedisPool.returnResourceObject(jedis);
		}
	}

	@Override
	public <T> T getMapValue(String key, String mapKey, Class<T> t) {
		Gson gson = new Gson();
		List<String> list = null;
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			list = jedis.hmget(key, mapKey);
		} catch (Exception e) {
			LOGGER.warn("redis getMapValue exception ",e);
		} finally {
			shardedJedisPool.returnResourceObject(jedis);
		}

		if (list == null || list.size() == 0) {
			return null;
		}

		String value = list.get(0);
		if (value != null) {
			return gson.fromJson(value, t);
		}
		return null;
	}

	@Override
	public <T> List<T> getMapValues(String key, Class<T> t)  {
		ShardedJedis jedis = null;
        Gson gson = new Gson();
        List<String> list = new ArrayList<String>();
        List<T> rList = new ArrayList<T>();

        try
        {
        	jedis = shardedJedisPool.getResource();
            list = jedis.hvals(key);
        } catch (Exception e) {
			LOGGER.warn("redis getMapValues exception ",e);
		} finally {
			shardedJedisPool.returnResourceObject(jedis);
		}

        if (list != null)
        {
            for (int i = 0; i < list.size(); i++)
            {
                rList.add(gson.fromJson(list.get(i), t));
            }
        }

        return rList;
	}

	@Override
	public Long removeMap(String key, String valueKey)  {
		Long result = 0L;
		ShardedJedis jedis = null;
        try
        {
        	jedis = shardedJedisPool.getResource();
            result = jedis.hdel(key, valueKey);
        } catch (Exception e) {
			LOGGER.warn("redis removeMap exception ",e);
		} finally {
			shardedJedisPool.returnResourceObject(jedis);
		}
        return result;
	}

	@Override
	public Map<String, String> getMaps(String key)  {
		ShardedJedis jedis = null;
        Map<String,String> rList = new HashMap<String,String>();
        try
        {
        	jedis = shardedJedisPool.getResource();
            rList = jedis.hgetAll(key);
        } catch (Exception e) {
			LOGGER.warn("redis getMaps exception ",e);
		} finally {
			shardedJedisPool.returnResourceObject(jedis);
		}
        return rList;
	}

	//-------------------common--------------------------
	@Override
	public Long remove(String name)  {
		Long result = 0L;
		ShardedJedis jedis = null;
        try
        {
        	jedis = shardedJedisPool.getResource();
            result = jedis.del(name);
        } catch (Exception e) {
			LOGGER.warn("redis remove exception ",e);
		} finally {
			shardedJedisPool.returnResourceObject(jedis);
		}
        return result;
	}

	@Override
	public boolean exists(String key)  {
		ShardedJedis jedis = null;
        try
        {
        	jedis = shardedJedisPool.getResource();
            return jedis.exists(key);
        } catch (Exception e) {
			LOGGER.warn("redis exists exception ",e);
		} finally {
			shardedJedisPool.returnResourceObject(jedis);
		}

		return false;

	}

	@Override
	public Long expire(String key,int seconds)  {
		ShardedJedis jedis = null;
        try
        {
        	jedis = shardedJedisPool.getResource();
            return  jedis.expire(key, seconds);
        } catch (Exception e) {
			LOGGER.warn("redis exists exception ",e);
		} finally {
			shardedJedisPool.returnResourceObject(jedis);
		}
		return null;
	}
	
}
