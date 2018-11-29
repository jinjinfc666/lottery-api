package com.jll.sys.siteMsg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.constants.Constants.SiteMessageReadType;
import com.jll.common.constants.Message;
import com.jll.common.utils.PageQuery;
import com.jll.common.utils.StringUtils;
import com.jll.dao.PageQueryDao;
import com.jll.dao.SupserDao;
import com.jll.entity.SiteMessFeedback;
import com.jll.entity.SiteMessage;
import com.jll.entity.UserInfo;
import com.jll.sysSettings.syscode.SysCodeService;
import com.jll.user.UserInfoDao;
import com.jll.user.UserInfoService;
import com.jll.user.wallet.WalletService;


@Service
@Transactional
public class SysSiteMsgServiceImpl implements SysSiteMsgService
{
	private Logger logger = Logger.getLogger(SysSiteMsgServiceImpl.class);
	
	@Resource
	UserInfoDao userDao;
	
	@Resource
	SupserDao  supserDao; 

	@Resource
	WalletService walletServ;
	
	@Resource
	SysCodeService sysCodeService;
	
	@Resource
	UserInfoService userInfoService;
	@Resource
	SysSiteMsgDao sysSiteMsgDao;
	
	
	private void getAllSiteMessageFeedback(List<SiteMessFeedback> backList , int userId, int msgId){
		DetachedCriteria dc = DetachedCriteria.forClass(SiteMessFeedback.class);
		dc.add(Restrictions.eq("mesId",msgId));
		dc.add(Restrictions.eq("fbUserId",userId));
		dc.addOrder(Order.desc("id"));
		backList = supserDao.findByCriteria(dc);
	}

	@Override
	public Map<String, Object> showSiteMessageFeedback(int msgId) {
		Map<String, Object> ret = new HashMap<String, Object>();
		SiteMessage dbMsg = (SiteMessage) supserDao.get(SiteMessage.class,msgId);
		if(null == dbMsg){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return ret;
		}
		
		List<SiteMessFeedback> retList = new ArrayList<>();
		
		getAllSiteMessageFeedback(retList, dbMsg.getUserId(), msgId);
		
		if(!retList.isEmpty()){
			SiteMessFeedback lastBack = retList.get(retList.size()-1);
			if(lastBack.getIsRead() == SiteMessageReadType.UN_READING.getCode()
					&& dbMsg.getReceiver() ==0 ){
				lastBack.setIsRead(SiteMessageReadType.READING.getCode());
				dbMsg.setIsRead(SiteMessageReadType.READING.getCode());
				supserDao.update(lastBack);
				supserDao.update(dbMsg);
			}
		}
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put(Message.KEY_DATA,dbMsg);
		ret.put(Message.KEY_REMAKE,retList);
		return ret;
	}

	@Override
	public Map<String, Object> getSiteMessageLists(String userName, PageQueryDao page) {
		Map<String, Object> ret = new HashMap<String, Object>(); 
		StringBuffer querySql = new StringBuffer("SELECT snt FROM  SiteMessage snt WHERE 1=1 ");
		List<Object> parmsList = new ArrayList<>();
		
		 if(null != page.getEndDate()){
			querySql.append(" and snt.createTime <= ? ");
			parmsList.add(page.getEndDate());
		 }
		 if(null != page.getStartDate()){
			 querySql.append(" and snt.createTime >= ? ");
			 parmsList.add(page.getStartDate());
		 }
		 if(!StringUtils.isEmpty(userName)){
			 querySql.append(" and snt.userId = (SELECT u.id FROM  UserInfo u WHERE u.userName = ?) ");
			 parmsList.add(userName);
		 }
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put(Message.KEY_DATA,PageQuery.queryForPagenationByHql(supserDao, querySql.toString(),SiteMessage.class,parmsList,page.getPageIndex(), page.getPageSize()));
		return ret;
	}

	@Override
	public Map<String, Object> getUserSiteMessageLists(Map<String, String> params) {
		Map<String, Object> ret = new HashMap<String, Object>();
		UserInfo dbInfo = userInfoService.getCurLoginInfo();
		if(null == dbInfo){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return ret;
		}
		params.put("id", dbInfo.getId().toString());
		return sysSiteMsgDao.querySiteMessage(params);
	}

	@Override
	public Map<String, Object> updateUserSiteMessageRead(Map<String, String> params) {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret=sysSiteMsgDao.updateUserSiteMessageRead(params);
		return ret;
	}
	@Override
	public Map<String, Object> showSiteMessageFeedbackTop(Integer msgId) {
		Map<String, Object> ret = new HashMap<String, Object>();
		UserInfo dbInfo = userInfoService.getCurLoginInfo();
		List<?> dbMsg = sysSiteMsgDao.querySiteMessageById(msgId);
		if(null == dbInfo
				|| null == dbMsg){
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
			return ret;
		}
		List<?> retList=sysSiteMsgDao.querySiteMessFeedback(msgId);
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put(Message.KEY_DATA,dbMsg);
		ret.put(Message.KEY_REMAKE,retList);
		return ret;
	}

	@Override
	public Map<String, Object> getUserSiteMessageListsB(Map<String, Object> params) {
		return sysSiteMsgDao.querySiteMessageB(params);
	}
}
