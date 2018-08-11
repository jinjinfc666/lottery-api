package com.jll.sys.siteMsg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
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
import com.jll.sysSettings.syscode.SysCodeService;
import com.jll.user.UserInfoDao;
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
	
	
	private void getAllSiteMessageFeedback(List<SiteMessFeedback> backList , int userId, int msgId){
		DetachedCriteria dc = DetachedCriteria.forClass(SiteMessFeedback.class);
		dc.add(Restrictions.eq("mesId",msgId));
		dc.add(Restrictions.eq("fbUserId",userId));
		List<SiteMessFeedback> dbBacks = supserDao.findByCriteria(dc);
		if(null!= dbBacks && !dbBacks.isEmpty()){
			backList.add(dbBacks.get(0));
			getAllSiteMessageFeedback(backList, dbBacks.get(0).getFbUserId(), dbBacks.get(0).getId());
		}
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
		
		DetachedCriteria dc = DetachedCriteria.forClass(SiteMessFeedback.class);
		dc.add(Restrictions.eq("mesId",msgId));
		dc.add(Restrictions.eq("fbUserId",dbMsg.getUserId()));
		
		List<SiteMessFeedback> retList = new ArrayList<>();
		
		getAllSiteMessageFeedback(retList, dbMsg.getUserId(), msgId);
		Collections.sort(retList, new Comparator<SiteMessFeedback>() {
			@Override
			public int compare(SiteMessFeedback b1, SiteMessFeedback b2) {
				return b2.getId()-b1.getId();
			}
		});
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
		 DetachedCriteria criteria = DetachedCriteria.forClass(SiteMessage.class);
	     
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
	
}
