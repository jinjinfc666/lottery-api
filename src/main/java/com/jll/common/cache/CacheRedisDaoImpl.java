package com.jll.common.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.jll.common.constants.Constants;
import com.jll.common.utils.DateUtil;
import com.jll.dao.PageBean;
import com.jll.entity.IpBlackList;
import com.jll.entity.Issue;
import com.jll.entity.PayChannel;
import com.jll.entity.PayType;
import com.jll.entity.PlayType;
import com.jll.entity.SysCode;
import com.jll.game.BulletinBoard;
import com.jll.game.LotteryCenterServiceImpl;

@Repository
public class CacheRedisDaoImpl  extends AbstractBaseRedisDao implements CacheRedisDao{

	private Logger logger = Logger.getLogger(LotteryCenterServiceImpl.class);
	
	@Override
	public void setCaptchaCode(CacheObject<String> cacheObj) {
		this.saveOrUpdate(cacheObj);
	}

	@Override
	public CacheObject<String> getCaptchaCode(String sms) {
		CacheObject<String> cacheObject = get(sms);
		return cacheObject;
	}

	@Override
	public void setPlan(String cacheKey, List<Issue> issues) {
		List<Issue> preContent = getPlan(cacheKey);
		List<Issue> newContent = new ArrayList<>();
		CacheObject<List<Issue>> cache = new CacheObject<>();
		Date lastDay = new Date();
		lastDay = DateUtil.addDay(lastDay, -1);
		if(preContent != null
				&& preContent.size() > 0) {
			for(Issue issue : preContent) {
				if(issue.getEndTime().getTime() > lastDay.getTime()) {
					newContent.add(issue);
				}
			}
		}
		
		for(Issue issue : issues) {
			newContent.add(issue);
		}
		
		cache.setContent(newContent);
		cache.setKey(cacheKey);
		this.saveOrUpdate(cache);
	}

	@Override
	public List<Issue> getPlan(String cacheKey) {
		CacheObject<List<Issue>> cache = this.get(cacheKey);
		if(cache == null) {
			return null;
		}
		return cache.getContent();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void upatePlan(String cacheKey, Issue issue) {
		List<Issue> content = null;
		CacheObject<List<Issue>> cache = this.get(cacheKey);
		if(cache == null 
				|| cache.getContent() == null 
				|| cache.getContent().size() == 0) {
			return ;
		}
		
		content = cache.getContent();
		for(int i = 0; i< content.size(); i++) {
			Issue temp = content.get(i);
			if(temp.getId().intValue() == issue.getId().intValue()) {
				content.set(i, issue);
				break;
			}
		}
		
		cache.setContent(content);
		cache.setKey(cacheKey);
		this.saveOrUpdate(cache);
	}
	
	@Override
	public void setSysCode(CacheObject<Map<String, SysCode>> cacheObj) {
		this.saveOrUpdate(cacheObj);
	}

	
	@Override
	public CacheObject<Map<String, SysCode>> getSysCode(String codeName) {
		CacheObject<Map<String, SysCode>> cacheObject = get(codeName);
		return cacheObject;
	}

	@Override
	public CacheObject<BulletinBoard> getBulletinBoard(String keyBulletinBoard) {
		return get(keyBulletinBoard);
	}

	@Override
	public void setBulletinBoard(CacheObject<BulletinBoard> cache) {
		this.saveOrUpdate(cache);
	}

	@Override
	public SysCode getSysCode(String codeTypeName, String codeName) {
		CacheObject<Map<String, SysCode>> cacheObject=get(codeTypeName);
		if(cacheObject==null) {
			return null;
		}
		Map<String,SysCode> map=cacheObject.getContent();
		SysCode sysCode=map.get(codeName); 
		return sysCode;
	}

	@Override
	public void setPlayType(CacheObject<List<PlayType>> cache) {
		this.saveOrUpdate(cache);
	}

	@Override
	public List<PlayType> getPlayType(String cacheKey) {
		CacheObject<List<PlayType>> cacheObject = this.get(cacheKey);
		
		if(cacheObject == null) {
			return null;
		}
		
		return cacheObject.getContent();
	}

	@Override
	public CacheObject<Map<String, Object>> getStatGroupByBettingNum(String cacheKey) {
		CacheObject<Map<String, Object>> cacheObj = this.get(cacheKey);
		
		return cacheObj;
	}

	@Override
	public void setStatGroupByBettingNum(CacheObject<Map<String, Object>> cacheObj) {
		this.saveOrUpdate(cacheObj);
	}
	//缓存ip
	@Override
	public CacheObject<Map<Integer, IpBlackList>> getIpBlackList(String codeName) {
		CacheObject<Map<Integer, IpBlackList>> cacheObject = get(codeName);
		return cacheObject;
	}
	@Override
	public IpBlackList getIpBlackList(String codeTypeName, Integer codeName) {
		CacheObject<Map<Integer, IpBlackList>> cacheObject=get(codeTypeName);
		if(cacheObject==null) {
			return null;
		}
		Map<Integer,IpBlackList> map=cacheObject.getContent();
		IpBlackList sysCode=map.get(codeName); 
		return sysCode;
	}

	@Override
	public void setIpBlackList(CacheObject<Map<Integer, IpBlackList>> cacheObj) {
		this.saveOrUpdate(cacheObj);
	}

	@Override
	public void publishMessage(String channel, Serializable mes) {
		redisTemplate.convertAndSend(channel, mes);
	}

	public void deleteIpBlackList(String codeTypeName, Integer codeName) {
		CacheObject<Map<Integer, IpBlackList>> cacheObject = get(codeTypeName);
		Map<Integer,IpBlackList> map=new HashMap<Integer,IpBlackList>();
		if(cacheObject != null){
			map=cacheObject.getContent();
			Iterator<?> iterator = map.keySet().iterator(); 
			while (iterator.hasNext()) {
			    Integer key = (Integer) iterator.next();
			    if (codeName.intValue()==key.intValue()) {
			       iterator.remove();        //添加该行代码
			        map.remove(key);    
			     }
			}
		}
		cacheObject.setContent(map);
		cacheObject.setKey(codeTypeName);
		this.setIpBlackList(cacheObject);
	}

	@Override
	public void setUserBettingFlag(CacheObject<Integer> cacheObj) {
		this.saveOrUpdate(cacheObj);
	}

	@Override
	public CacheObject<Integer> getUserBettingFlag(String cacheKey) {
		return this.get(cacheKey);
	}
	
	//充值渠道
	@Override
	public CacheObject<Map<Integer, PayChannel>> getPayChannel(String codeName) {
		CacheObject<Map<Integer, PayChannel>> cacheObject = get(codeName);
		return cacheObject;
	}
	@Override
	public List<PayType> getPayType(String cacheKey) {
		CacheObject<List<PayType>> cacheObject = this.get(cacheKey);
		if(cacheObject == null) {
			return null;
		}
		return cacheObject.getContent();
	}
	@Override
	public void setPayType(CacheObject<List<PayType>> cache) {
		this.saveOrUpdate(cache);
	}
	@Override
	public PayChannel getPayChannel(String codeTypeName, Integer codeName) {
		CacheObject<Map<Integer, PayChannel>> cacheObject=get(codeTypeName);
		if(cacheObject==null) {
			return null;
		}
		Map<Integer,PayChannel> map=cacheObject.getContent();
		PayChannel payChannel=map.get(codeName); 
		return payChannel;
	}

	@Override
	public void setPayChannel(CacheObject<Map<Integer, PayChannel>> cacheObj) {
		this.saveOrUpdate(cacheObj);
	}

	@Override
	public PageBean<CacheObject> queryByPagination(PageBean<CacheObject> page, String HQL, List<Object> params,
			Class<CacheObject> clazz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PageBean queryByPagination(PageBean page, String HQL, Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long queryCount(String HQL, Map<String, Object> params) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public PageBean queryBySqlPagination(PageBean page, String HQL, Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long querySqlCount(String HQL, Map<String, Object> params) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public CacheObject<Map<String, Object>> getPlatStat(String cacheKey) {
		return get(cacheKey);
	}

	@Override
	public void setPlatStat(CacheObject<Map<String, Object>> cacheObj) {
		saveOrUpdate(cacheObj);
	}

	@Override
	public CacheObject<Integer> getMMCIssueCount(String cacheKey) {
		return get(cacheKey);
	}

	@Override
	public void setMMCIssueCount(CacheObject<Integer> cacheObj) {
		saveOrUpdate(cacheObj);
	}

	@Override
	public PageBean<CacheObject> queryBySqlClazzPagination(PageBean<CacheObject> page, String SQL,
			Map<String, Object> params, Class<CacheObject> clazz) {
		// TODO Auto-generated method stub
		return null;
	}
	//存放图片验证码
	@Override
	public void setSessionIdCaptcha(CacheObject<Map<String,String>> cacheObj) {
		this.saveOrUpdate(cacheObj);
	}
	//获取图片验证码
	@Override
	public CacheObject<Map<String,String>> getSessionIdCaptcha(String key) {
		CacheObject<Map<String,String>> cacheObject=get(key);
		if(cacheObject==null) {
			return null;
		}
		return cacheObject;
	}
	@Override
	public String getSessionIdCaptcha(String key, String keyCaptcha) {
		CacheObject<Map<String, String>> cacheObject=get(key);
		if(cacheObject==null) {
			return null;
		}
		Map<String,String> map=cacheObject.getContent();
		String value=map.get(keyCaptcha); 
		return value;
	}
	//删除缓存中的图片验证码
	@Override
	public void deleteSessionIdCaptcha(String key,String keyCaptcha) {
		CacheObject<Map<String,String>> cacheObject=getSessionIdCaptcha(key);
		Map<String,String> map=null;
		if(cacheObject!=null) {
			map=cacheObject.getContent();
			if(map!=null) {
				Integer b=map.size();
				Iterator<String> iterator = map.keySet().iterator();// map中key（键）的迭代器对象
		        while (iterator.hasNext()){// 循环取键值进行判断
		            String keyCode = iterator.next();// 键
		            if(keyCode.startsWith(keyCaptcha)){
		                iterator.remove();// 移除map中以a字符开头的键对应的键值对
		            }
		        }
		        Integer a=map.size();
				cacheObject.setContent(map);
				cacheObject.setKey(key);
				this.setSessionIdCaptcha(cacheObject);
			}
		}
	}

	@Override
	public PageBean queryByTimePagination(PageBean page, String HQL, Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long queryTimeCount(String HQL, Map<String, Object> params) {
		// TODO Auto-generated method stub
		return 0;
	}

	/*@Override
	public boolean lock(CacheObject entity) {
		return lock(entity);
	}*/

	/*@Override
	public void releaseLock(CacheObject entity) {
		// TODO Auto-generated method stub
		
	}
*/
}
