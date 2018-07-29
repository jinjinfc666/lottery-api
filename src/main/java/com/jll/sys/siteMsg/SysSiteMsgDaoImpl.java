package com.jll.sys.siteMsg;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.jll.common.constants.Constants.UserType;
import com.jll.common.utils.PageQuery;
import com.jll.common.utils.PagenationUtil;
import com.jll.common.utils.StringUtils;
import com.jll.dao.DefaultGenericDaoImpl;
import com.jll.dao.PageQueryDao;
import com.jll.entity.SiteMessage;
import com.jll.entity.UserInfo;

@Repository
public class SysSiteMsgDaoImpl extends DefaultGenericDaoImpl<SiteMessage> implements SysSiteMsgDao
{

	@Override
	public List<SiteMessage> getSiteMessageLists(String userName, PageQueryDao page) {
		
		String sql = "from SiteMessage sit,UserInfo u where u.id = sit.userId ";
		List<Object> list=new ArrayList<Object>();
		if(!StringUtils.isEmpty(userName)){
			sql += " and u.userName = ?";
			list.add(userName);
		}
		List<SiteMessage> generalAgencys  = query(sql, list, SiteMessage.class);
		if(generalAgencys == null || generalAgencys.size() == 0) {
			return null;
		}
		return generalAgencys;
	}
  
  
}
