package com.yijiawang.web.platform.userCenter.cache;

import com.yijiawang.web.platform.userCenter.util.SerializebaleUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component("jedisPoolManager")
public class JedisPoolManager<K, V> {
	
	private static Log log = LogFactory.getLog(JedisPoolManager.class);
	
	private JedisPool jedisPool;
	/**
	 * redis的List集合 ，向key这个list添加元素
	 */
	public long rpush(String key, String string) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			long ret = jedis.rpush(key, string);
			return ret;
		} catch (Exception e) {
			log.error(e);
			if (jedis != null) {
				jedisPool.returnBrokenResource(jedis);
			}
			throw new JedisException(e);
		} finally{ 
	         if(jedis != null ) {
	        	 jedisPool.returnResource(jedis);
	         }
		}
	}
	/**
	 * 获取key这个List，从第几个元素到第几个元素 LRANGE key start
	 * stop返回列表key中指定区间内的元素，区间以偏移量start和stop指定。
	 * 下标(index)参数start和stop都以0为底，也就是说，以0表示列表的第一个元素，以1表示列表的第二个元素，以此类推。
	 * 也可以使用负数下标，以-1表示列表的最后一个元素，-2表示列表的倒数第二个元素，以此类推。
	 */
	public List<String> lrange(String key, long start, long end) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			List<String> ret = jedis.lrange(key, start, end);
			return ret;
		} catch (Exception e) {
			log.error(e);
			if (jedis != null) {
				jedisPool.returnBrokenResource(jedis);
			}
			throw new JedisException(e);
		}finally{ 
	         if(jedis != null ) {
	        	 jedisPool.returnResource(jedis);
	         }
		}
	}
	/**
	 * 将哈希表key中的域field的值设为value。
	 */
	public void hset(String key, String field, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.hset(key, field, value);
		} catch (Exception e) {
			log.error(e);
			if (jedis != null) {
				jedisPool.returnBrokenResource(jedis);
			}
			throw new JedisException(e);
		}finally{ 
	         if(jedis != null ) {
	        	 jedisPool.returnResource(jedis);
	         }
		}
	}
	/**
	 * 向key赋值
	 */
	public void set(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.set(key, value);
		} catch (Exception e) {
			log.error(e);
			if (jedis != null) {
				jedisPool.returnBrokenResource(jedis);
			}
			throw new JedisException(e);
		}finally{ 
	         if(jedis != null ) {
	        	 jedisPool.returnResource(jedis);
	         }
		}
	}
	
	/**
	 * 获取key的值
	 */
	public String get(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			String value = jedis.get(key);
			return value;
		} catch (Exception e) {
			log.error(e);
			if (jedis != null) {
				jedisPool.returnBrokenResource(jedis);
			}
			throw new JedisException(e);
		}finally{ 
	         if(jedis != null ) {
	        	 jedisPool.returnResource(jedis);
	         }
		}
	}
	
	public V getObject(K key){
		Jedis jedis = null;
		try {			
			if (key == null) {
			    return null;
			} else {
				jedis = jedisPool.getResource();
			    byte[] rawValue = jedis.get(getByteKey(key));
			    @SuppressWarnings("unchecked") V value = (V) SerializebaleUtils.deserialize(rawValue);
			    return value;
			}
		} catch (Exception e) {
			log.error(e);
			if (jedis != null) {
				jedisPool.returnBrokenResource(jedis);
			}
			throw new JedisException(e);
		}finally{ 
	         if(jedis != null ) {
	        	 jedisPool.returnResource(jedis);
	         }
		} 
    }
	
	
	/**
     * 获得byte[]型的key
     * @param key
     * @return
     */
    private byte[] getByteKey(K key) {
        if (key instanceof String) {
            String preKey = key.toString();
            return preKey.getBytes();
        }else{
            return SerializebaleUtils.serialize(key);
        }
    }
    
    public V put(K key, V value){
	  Jedis jedis = null;
	  try {
		jedis = jedisPool.getResource();
		  jedis.set(getByteKey(key), SerializebaleUtils.serialize(value));
		  return value;
		} catch (Exception e) {
			log.error(e);
			if (jedis != null) {
				jedisPool.returnBrokenResource(jedis);
			}
			throw new JedisException(e);
		}finally{ 
	        if(jedis != null ) {
	       	 jedisPool.returnResource(jedis);
	        }
		} 
        
    }
    
    public V putObject(K key, V value, long milliseconds){
  	  Jedis jedis = null;
  	  try {
		  jedis = jedisPool.getResource();
		  jedis.psetex(getByteKey(key), milliseconds, SerializebaleUtils.serialize(value));
		  return value;
		} catch (Exception e) {
			log.error(e);
			if (jedis != null) {
				jedisPool.returnBrokenResource(jedis);
			}
			throw new JedisException(e);
		}finally{ 
	        if(jedis != null ) {
		       	 jedisPool.returnResource(jedis);
		    }
		}        
     }
	/**
	 * 获取key的值
	 */
	public byte[] get(byte[] key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			byte[] value = jedis.get(key);
			return value;
		} catch (Exception e) {
			log.error(e);
			if (jedis != null) {
				jedisPool.returnBrokenResource(jedis);
			}
			throw new JedisException(e);
		}finally{ 
	         if(jedis != null ) {
	        	 jedisPool.returnResource(jedis);
	         }
		}

	}
	/**
	 * 将多个field - value(域-值)对设置到哈希表key中。
	 */
	public void hmset(String key, Map<String, String> map) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.hmset(key, map);
		} catch (Exception e) {
			log.error(e);
			if (jedis != null) {
				jedisPool.returnBrokenResource(jedis);
			}
			throw new JedisException(e);
		}finally{ 
	         if(jedis != null ) {
	        	 jedisPool.returnResource(jedis);
	         }
		}
	}
	/**
	  * 给key赋值，并生命周期设置为seconds
	 */
	public void setex(String key, int seconds, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.setex(key, seconds, value);
		 } catch (Exception e) {
			log.error(e);
			if (jedis != null) {
 				jedisPool.returnBrokenResource(jedis);
 			}
			throw new JedisException(e);
		}finally{ 
	         if(jedis != null ) {
	        	 jedisPool.returnResource(jedis);
	         }
		}
	}
	/**
	 * 给key赋值，并生命周期设置为seconds
	 */
	public byte[] setex(byte[] key, byte[] value, int seconds) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.setex(key, seconds, value);
			return value;
		} catch (Exception e) {
			log.error(e);
 			if (jedis != null) {
				jedisPool.returnBrokenResource(jedis);
			}
			throw new JedisException(e);
		}finally{ 
	         if(jedis != null ) {
	        	 jedisPool.returnResource(jedis);
	         }
		}
		

	}
	/**
	 * 为给定key设置生命周期
	 */
	public void expire(String key, int seconds) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.expire(key, seconds);
		} catch (Exception e) {
			log.error(e);
			if (jedis != null) {
				jedisPool.returnBrokenResource(jedis);
			}
			 throw new JedisException(e);
		}finally{ 
	         if(jedis != null ) {
	        	 jedisPool.returnResource(jedis);
	         }
		}
	}
	/**
	 * 检查key是否存在
	 */
	public boolean exists(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			boolean bool = jedis.exists(key);
			return bool;
		} catch (Exception e) {
			log.error(e);
			if (jedis != null) {
				jedisPool.returnBrokenResource(jedis);
			}
			throw new JedisException(e);
		}finally{ 
	         if(jedis != null ) {
	        	 jedisPool.returnResource(jedis);
	         }
		}
	}
        /**
	 * 检查key是否存在
	 */
	public boolean exists(byte[] key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
		    Set<byte[]> hashSet = jedis.keys(key);
		    if (null != hashSet && hashSet.size() >0 ){
		    	return true;
		    }else{
		    	return false;
		    }

		} catch (Exception e) {
			 log.error(e);
			if (jedis != null) {
				jedisPool.returnBrokenResource(jedis);
			}
			throw new JedisException(e);
		}finally{ 
	         if(jedis != null ) {
	        	 jedisPool.returnResource(jedis);
	         }
		}
	}
	/**
	 * 返回key值的类型 none(key不存在),string(字符串),list(列表),set(集合),zset(有序集),hash(哈希表)
	 */
	public String type(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			String type = jedis.type(key);
			return type;
 		} catch (Exception e) {
			log.error(e);
			if (jedis != null) {
				jedisPool.returnBrokenResource(jedis);
			}
			throw new JedisException(e);
		}finally{ 
	         if(jedis != null ) {
	        	 jedisPool.returnResource(jedis);
	         }
		}
	}
	/**
	 * 从哈希表key中获取field的value
	 */
	public String hget(String key, String field) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			String value = jedis.hget(key, field);
			return value;
 		} catch (Exception e) {
			log.error(e);
			if (jedis != null) {
				jedisPool.returnBrokenResource(jedis);
			}
			throw new JedisException(e);
		}finally{ 
	         if(jedis != null ) {
	        	 jedisPool.returnResource(jedis);
	         }
		}
	}
	/**
	 * 返回哈希表key中，所有的域和值
	 */
	public Map<String, String> hgetAll(String key) {
		Jedis jedis = null;
		try {
			 jedis = jedisPool.getResource();
			Map<String, String> map = jedis.hgetAll(key);
			return map;
		} catch (Exception e) {
			 log.error(e);
			if (jedis != null) {
				jedisPool.returnBrokenResource(jedis);
			}
			throw new JedisException(e);
		}finally{ 
	         if(jedis != null ) {
	        	 jedisPool.returnResource(jedis);
	         }
		}

	}
	/**
	 * 返回哈希表key中，所有的域和值
	 */
	public Set<?> smembers(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			 Set<?> set = jedis.smembers(key);
 			return set;
 		} catch (Exception e) {
			log.error(e);
			if (jedis != null) {
				jedisPool.returnBrokenResource(jedis);
			}
			 throw new JedisException(e);
		}finally{ 
	         if(jedis != null ) {
	        	 jedisPool.returnResource(jedis);
	         }
		}
	}	
	/**
	 * 返回匹配的 keys 列表
	 */
	 public Set<byte[]> keys(String pattern) {
 		Jedis jedis = null;
 		try {
			jedis = jedisPool.getResource();
			Set<byte[]> keys = jedis.keys(pattern.getBytes());
			return keys;
 		} catch (Exception e) {
			log.error(e);
			if (jedis != null) {
				jedisPool.returnBrokenResource(jedis);
			}
			throw new JedisException(e);
		}finally{ 
	         if(jedis != null ) {
	        	 jedisPool.returnResource(jedis);
	         }
		}

	}
	/**
	 * 移除set集合中的member元素
	 */
	public void delSetObj(String key, String field) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.srem(key, field);
		} catch (Exception e) {
 			log.error(e);
			if (jedis != null) {
				jedisPool.returnBrokenResource(jedis);
			}
			throw new JedisException(e);
		}finally{ 
	         if(jedis != null ) {
	        	 jedisPool.returnResource(jedis);
	         }
		}
	}
	/**
	 * 删除元素
	 */
	public void del(byte[] key) {
		 Jedis jedis = null;
 		try {
			jedis = jedisPool.getResource();
			jedis.del(key);
 		} catch (Exception e) {
			log.error(e);
			if (jedis != null) {
				jedisPool.returnBrokenResource(jedis);
			}
			throw new JedisException(e);
		}finally{ 
	         if(jedis != null ) {
	        	 jedisPool.returnResource(jedis);
	         }
		}
	}
	
	/**
	 * 删除元素
	 */
	public void del(String key) {
		 Jedis jedis = null;
 		try {
			jedis = jedisPool.getResource();
			jedis.del(key);
 		} catch (Exception e) {
			log.error(e);
			if (jedis != null) {
				jedisPool.returnBrokenResource(jedis);
			}
			throw new JedisException(e);
		}finally{ 
	         if(jedis != null ) {
	        	 jedisPool.returnResource(jedis);
	         }
		}
	}
	/**
	 * 判断member元素是否是集合key的成员。是（true），否则（false）
	 */
	public boolean isNotField(String key, String field) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			 boolean bool = jedis.sismember(key, field);
			 return bool;
		} catch (Exception e) {
			 log.error(e);
			if (jedis != null) {
				jedisPool.returnBrokenResource(jedis);
			}
			throw new JedisException(e);
		}finally{ 
	         if(jedis != null ) {
	        	 jedisPool.returnResource(jedis);
	         }
		}
	}
	/**
	 * 如果key已经存在并且是一个字符串，将value追加到key原来的值之后
	 */
	public void append(String key, String value) {
		Jedis jedis = null;
 		try {
 			jedis = jedisPool.getResource();
			jedis.append(key, value);
		} catch (Exception e) {
			log.error(e);
			if (jedis != null) {
				jedisPool.returnBrokenResource(jedis);
			}
 			throw new JedisException(e);
		}finally{ 
	         if(jedis != null ) {
	        	 jedisPool.returnResource(jedis);
	         }
		}
	}
	/**
	 * 清空当前的redis 库
	 */
	public void flushDB() {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
 			jedis.flushDB();
		} catch (Exception e) {
			log.error(e);
			if (jedis != null) {
				jedisPool.returnBrokenResource(jedis);
			}
			throw new JedisException(e);
		}finally{ 
	         if(jedis != null ) {
	        	 jedisPool.returnResource(jedis);
	         }
		}

	 }
	/**
	 * 返回当前redis库所存储数据的大小
	 */
	public Long dbSize() {
		
		Long dbSize = 0L;
		
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			 jedis.dbSize();
			return dbSize;
		} catch (Exception e) {
			log.error(e);
			if (jedis != null) {
				jedisPool.returnBrokenResource(jedis);
			}
			throw new JedisException(e);
		}finally{ 
	         if(jedis != null ) {
	        	 jedisPool.returnResource(jedis);
	         }
		}

	}
	/**
	 * 关闭 Redis
	 */
	public void destory() {
		jedisPool.destroy();
	}

       public JedisPool getJedisPool() {
		return jedisPool;
	}
  	public void setJedisPool(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

	/**
	 * 自动增加
	 */
	public Long incrKeyCount(String key, int integer, int expireSec) {
		Jedis jedis = null;
		Long count = 0L;
		try {
			jedis = jedisPool.getResource();
			count = jedis.incrBy(key, integer);
			jedis.expire(key, expireSec);
		} catch (Exception e) {
			log.error(e);
			if (jedis != null) {
				jedisPool.returnBrokenResource(jedis);
			}
			throw new JedisException(e);
		}finally{ 
	         if(jedis != null ) {
	        	 jedisPool.returnResource(jedis);
	         }
		}
		return count;
	}

    /**
     * 往缓存set中加入成员
     * @param key
     * @param members
     * @return
     */
    public Long sadd(String key, String... members) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Long result = jedis.sadd(key, members);
            return result;
        } catch (Exception e) {
            log.error(e);
            if (jedis != null) {
                jedisPool.returnBrokenResource(jedis);
            }
            throw new JedisException(e);
        }finally{
            if(jedis != null ) {
                jedisPool.returnResource(jedis);
            }
        }
    }

    /**
     * 返回redis集合中对象数量
     * @param key
     * @return
     */
    public Long scard(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.scard(key);
        } catch (Exception e) {
            log.error(e);
            if (jedis != null) {
                jedisPool.returnBrokenResource(jedis);
            }
            throw new JedisException(e);
        }finally{
            if(jedis != null ) {
                jedisPool.returnResource(jedis);
            }
        }
    }

    /**
     * 判断指定值是否存在于指定set中
     * @param key
     * @param member
     * @return
     */
    public Boolean sismember(String key, String member) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.sismember(key, member);
        } catch (Exception e) {
            log.error(e);
            if (jedis != null) {
                jedisPool.returnBrokenResource(jedis);
            }
            throw new JedisException(e);
        }finally{
            if(jedis != null ) {
                jedisPool.returnResource(jedis);
            }
        }
    }

    /**
     * 移除集合中的元素
     */
    public Long srem(String key, String... members) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.srem(key, members);
        } catch (Exception e) {
            log.error(e);
            if (jedis != null) {
                jedisPool.returnBrokenResource(jedis);
            }
            throw new JedisException(e);
        }finally{
            if(jedis != null ) {
                jedisPool.returnResource(jedis);
            }
        }
    }
}
