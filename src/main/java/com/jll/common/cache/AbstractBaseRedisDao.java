package com.jll.common.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import com.jll.dao.GenericDaoIf;  
  
/**  
 * AbstractBaseRedisDao 
 * 
 * 
 */   
public abstract class AbstractBaseRedisDao implements GenericDaoIf<CacheObject>{  
      
	@Resource
    protected RedisTemplate<String, CacheObject> redisTemplate;  
  
    /** 
     * 设置redisTemplate 
     * @param redisTemplate the redisTemplate to set 
     */  
    public void setRedisTemplate(RedisTemplate<String, CacheObject> redisTemplate) {  
        this.redisTemplate = redisTemplate;  
    }  
      
    /** 
     * 获取 RedisSerializer 
     * <br>------------------------------<br> 
     */  
    protected RedisSerializer<String> getRedisSerializer() {  
        return redisTemplate.getStringSerializer();  
    }  
    
    
    @Override
	public void saveOrUpdate(CacheObject entity) {		
		String key = entity.getKey();
		/*redisTemplate.opsForHash().
		if(entity.getExpired() != null || entity.getExpired().intValue() != 0) {
			redisTemplate.expire(key, entity.getExpired().intValue(), TimeUnit.SECONDS);
		}*/
		
		boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				boolean ret = false;

				RedisSerializer<String> serializer = getRedisSerializer();
				
				ByteArrayOutputStream boos = null;
				ObjectOutputStream oos = null;
				try {
					boos = new ByteArrayOutputStream();
					oos = new ObjectOutputStream(boos);
					oos.writeObject(entity);
					connection.set(serializer.serialize(key), boos.toByteArray());
					
					if(entity.getExpired() != null && entity.getExpired().intValue() != 0) {
						redisTemplate.expire(key, entity.getExpired().intValue(), TimeUnit.SECONDS);
					}
					
					ret = true;
				} catch (IOException e) {
					e.printStackTrace();
					//return false;
				}finally {
					if(oos != null) {
						try {
							oos.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					if(boos != null) {
						try {
							boos.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

				return ret;
			}
		});
		
		if(!result) {
			throw new RuntimeException();
		}
	}

	@Override
	public boolean add(List<CacheObject> entities) {
		
		return false;
	}

	@Override
	public boolean delete(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void delete(CacheObject entity) {
		String key = entity.getKey();
		redisTemplate.execute(new RedisCallback<Boolean>() {
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				boolean ret = false;

				RedisSerializer<String> serializer = getRedisSerializer();
				
				ByteArrayOutputStream boos = null;
				ObjectOutputStream oos = null;
				try {
					boos = new ByteArrayOutputStream();
					oos = new ObjectOutputStream(boos);
					oos.writeObject(entity);
					connection.del(serializer.serialize(key));
										
					ret = true;
				} catch (IOException e) {
					e.printStackTrace();
					//return false;
				}finally {
					if(oos != null) {
						try {
							oos.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					if(boos != null) {
						try {
							boos.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

				return ret;
			}
		});
	}

	@SuppressWarnings("rawtypes")
	@Override
	public CacheObject get(String key) {
		CacheObject result = redisTemplate.execute(new RedisCallback<CacheObject>() {
			public CacheObject doInRedis(RedisConnection connection) throws DataAccessException {
				CacheObject ret = null;

				RedisSerializer<String> serializer = getRedisSerializer();
				
				ByteArrayInputStream boos = null;
				ObjectInputStream oos = null;
				try {
					byte[] cacheObj = connection.get(serializer.serialize(key));
					if(cacheObj == null || cacheObj.length == 0) {
						return null;
					}
					
					boos = new ByteArrayInputStream(cacheObj);
					oos = new ObjectInputStream(boos);
					CacheObject<String> cache = (CacheObject)oos.readObject();
					
					ret = cache;
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
					//return false;
				}finally {
					if(oos != null) {
						try {
							oos.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					if(boos != null) {
						try {
							boos.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

				return ret;
			}
		});
		
		return result;
	}

	@Override
	public List<CacheObject> query(String HQL, List<Object> params, Class<CacheObject> clazz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long queryCount(String HQL, List<Object> params) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public boolean lock(CacheObject entity) {	
		String key = entity.getKey();
		boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				boolean ret = false;

				RedisSerializer<String> serializer = getRedisSerializer();
				
				ByteArrayOutputStream boos = null;
				ObjectOutputStream oos = null;
				try {
					boos = new ByteArrayOutputStream();
					oos = new ObjectOutputStream(boos);
					oos.writeObject(entity);
					ret = connection.setNX(serializer.serialize(key), boos.toByteArray());
					
					if(ret && entity.getExpired() != null && entity.getExpired().intValue() != 0) {
						redisTemplate.expire(key, entity.getExpired().intValue(), TimeUnit.SECONDS);
					}
					
					//ret = true;
				} catch (IOException e) {
					e.printStackTrace();
					//return false;
				}finally {
					if(oos != null) {
						try {
							oos.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					if(boos != null) {
						try {
							boos.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

				return ret;
			}
		});
		
		return result;
	}
	
	public void releaseLock(CacheObject entity) {
		delete(entity);
	}
}  