package com.jll.sys.notify;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Constants.SysCodeTypes;
import com.jll.common.constants.Constants.SysNotifyReceiverType;
import com.jll.common.constants.Constants.SysNotifyType;
import com.jll.common.constants.Message;
import com.jll.common.utils.PageQuery;
import com.jll.common.utils.StringUtils;
import com.jll.dao.PageQueryDao;
import com.jll.dao.SupserDao;
import com.jll.entity.SysCode;
import com.jll.entity.SysNotification;
import com.jll.entity.UserInfo;
import com.jll.user.UserInfoDao;


@Service
@Transactional
public class SysNotifyServiceImpl implements SysNotifyService
{
	private Logger logger = Logger.getLogger(SysNotifyServiceImpl.class);
	
	@Resource
	CacheRedisService cacheRedisService;
	
	@Resource
	SupserDao  supserDao; 
	
	@Resource
	UserInfoDao userDao;

	@Override
	public Map<String, Object> getSysNotifyLists(String userName, PageQueryDao page) {
		Map<String, Object> ret = new HashMap<String, Object>(); 
		 DetachedCriteria criteria = DetachedCriteria.forClass(SysNotification.class);
	     
		 if(null != page.getEndDate()){
			 criteria.add(Restrictions.le("createTime",page.getEndDate()));
		 }
		 if(null != page.getStartDate()){
			 criteria.add(Restrictions.ge("createTime",page.getStartDate()));
		 }
		
		 if(!StringUtils.isEmpty(userName)){
			 criteria = criteria.createCriteria("UserInfo");
	         criteria.add(Restrictions.eq("userName",userName));
		 }
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put(Message.KEY_DATA,PageQuery.queryForPagenation(supserDao.getHibernateTemplate(), criteria, page.getPageIndex(), page.getPageSize()));
		return ret;
	}

	@Override
	public Map<String, Object> setSysNotifyExpire(int notifyId) {
		Map<String, Object> ret = new HashMap<String, Object>();
		SysNotification dbNotify = (SysNotification) supserDao.get(SysNotification.class,notifyId);
		if(null == dbNotify){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return ret;
		}
		
		dbNotify.setExpireTime(DateUtils.addDays(dbNotify.getCreateTime(),-1));
		supserDao.update(dbNotify);
		
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return ret;
	}

	
	@Override
	public Map<String, Object> updateSysNotify(SysNotification notify) {
		Map<String, Object> ret = new HashMap<String, Object>();
		SysNotification dbNotify = (SysNotification) supserDao.get(SysNotification.class,notify.getId());
		if(null == dbNotify
				|| StringUtils.isEmpty(notify.getContent())
				|| StringUtils.isEmpty(notify.getTitle())){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return ret;
		}
		dbNotify.setContent(notify.getContent());
		dbNotify.setTitle(notify.getTitle());
		supserDao.update(dbNotify);
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return ret;
	}

	@Override
	public Map<String, Object> addSysNotify(String sendIds,SysNotification notify) {
		Map<String, Object> ret = new HashMap<String, Object>();
		if(StringUtils.isEmpty(notify.getContent())){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_MESSAGE_CONTENT_IS_EMPTY);
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_MESSAGE_CONTENT_IS_EMPTY.getErrorMes());
			return ret;
		}else if(StringUtils.isEmpty(notify.getTitle())){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_MESSAGE_TITLE_IS_EMPTY);
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_MESSAGE_TITLE_IS_EMPTY.getErrorMes());
			return ret;
			
		}else if(null == SysNotifyReceiverType.getSysNotifyReceiverTypeByCode(notify.getReceiverType())){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_NOTIFY_RECEIVER_TYPE_ERROR);
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_NOTIFY_RECEIVER_TYPE_ERROR.getErrorMes());
			return ret;
			
		}else if((SysNotifyReceiverType.LEVEL.getCode() == notify.getReceiverType()
			 && StringUtils.isEmpty(sendIds))
				|| (SysNotifyReceiverType.TYPE.getCode() == notify.getReceiverType() && null == SysNotifyType.getSysNotifyTypeByCode(notify.getReceiver()))){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_NOTIFY_RECEIVER_ERROR);
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_NOTIFY_RECEIVER_ERROR.getErrorMes());
			return ret;
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth == null) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_LOGIN);
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_LOGIN.getErrorMes());
			return ret;
		}
		
		UserInfo loginUser = userDao.getUserByUserName(auth.getName());
		
		if(loginUser == null) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_NO_VALID_USER);
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_NO_VALID_USER.getErrorMes());
			return ret;
		}
		
		notify.setCreator(loginUser.getId());
		notify.setCreateTime(new Date());
		notify.setExpireTime(DateUtils.addDays(new Date(), Integer.valueOf(cacheRedisService.getSysCode(SysCodeTypes.NOTIFY_MSG_VALID_DAY.getCode()).get(SysCodeTypes.NOTIFY_MSG_VALID_DAY.getCode()).getCodeVal())));
		
		if(SysNotifyReceiverType.LEVEL.getCode() == notify.getReceiverType() 
				&& !StringUtils.isEmpty(sendIds)){
			if(!userDao.checkUserIds(sendIds)){
				ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
				ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
				ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
				return ret;
			}
			List<SysNotification> addList = new ArrayList<>();
			for(String id:sendIds.split(StringUtils.COMMA)){
				SysNotification addMsg = new SysNotification();
				BeanUtils.copyProperties(notify, addMsg);
				addMsg.setReceiver(Integer.valueOf(id));
				addList.add(addMsg);
			}
			supserDao.saveList(addList);
			
		}else{
			supserDao.save(notify);
		}
		supserDao.update(notify);
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		return ret;
	}

	
}
