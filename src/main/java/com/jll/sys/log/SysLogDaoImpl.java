package com.jll.sys.log;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.jll.common.constants.Constants;
import com.jll.common.utils.DateUtil;
import com.jll.dao.DefaultGenericDaoImpl;//失败后添加登录失败次数和修改锁定时间
import com.jll.dao.PageBean;
import com.jll.entity.SysLog;

@Repository
public class SysLogDaoImpl extends DefaultGenericDaoImpl<SysLog> implements SysLogDao
{
	private Logger logger = Logger.getLogger(SysLogDaoImpl.class);

	@Override
	public void saveUpdate(SysLog sysLog) {
		this.saveOrUpdate(sysLog);
	}

	@Override
	public PageBean queryOperationlog(Integer type,Integer userId, String startTime, String endTime, Integer pageIndex,Integer pageSize) {
		Map<String,Object> map=new HashMap<String,Object>();
		String userIdSql="";
		if(userId!=null) {
			userIdSql="and  a.userId=:userId";
			map.put("userId", userId);
		}
		Date beginDate = DateUtil.fmtYmdHisToDate(startTime);
	    Date endDate = DateUtil.fmtYmdHisToDate(endTime);
		map.put("startTime", beginDate);
		map.put("endTime", endDate);
		String userTypeSql="";
		if(type==1) {//前端
			userTypeSql=" and b.userType!=:userType";
			map.put("userType", Constants.UserType.SYS_ADMIN.getCode());
		}else if(type==2) {//后台
			userTypeSql=" and b.userType=:userType";
			map.put("userType",Constants.UserType.SYS_ADMIN.getCode());
		}
		String sql="from SysLog a,UserInfo b where a.userId=b.id  "+userIdSql+" and a.createTime>=:startTime and a.createTime<:endTime "+userTypeSql+" ORDER BY a.id DESC";
		PageBean page=new PageBean();
		page.setPageIndex(pageIndex);
		page.setPageSize(pageSize);
		PageBean pageBean=queryByPagination(page,sql,map);
		return pageBean;
	}
  
}
