package com.jll.report;

import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.NoResultException;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.Query;
import org.hibernate.type.DateType;
import org.hibernate.type.TimestampType;
import org.springframework.stereotype.Repository;

import com.jll.common.constants.Constants;
import com.jll.common.utils.DateUtil;
import com.jll.dao.DefaultGenericDaoImpl;
import com.jll.dao.PageBean;
import com.jll.entity.UserAccountDetails;


@Repository
public class FlowDetailDaoImpl extends DefaultGenericDaoImpl<UserAccountDetails> implements FlowDetailDao {
	@Override
	public Map<String,Object> queryUserAccountDetails(Integer codeTypeNameId,String userName,Float amountStart,Float amountEnd,String operationType,String startTime,String endTime,Integer pageIndex,Integer pageSize) {
		String userNameSql="";
		String amountStartSql="";
		String amountEndSql="";
		String operationTypeSql="";
		String timeSql="";
		List<Object> list=new ArrayList<Object>();
		Map<String,Object> map=new HashMap();
		if(!StringUtils.isBlank(userName)) {
			userNameSql=" and b.userName=:userName";
			map.put("userName", userName);
		}
		if(amountStart!=null) {
			amountStartSql=" and a.amount>:amount";
			map.put("amountStart", amountStart);
		}
		if(amountEnd!=null) {
			amountEndSql=" and a.amount<=:amount";
			map.put("amountEnd", amountEnd);
		}
		if(!StringUtils.isBlank(operationType)) {
			operationTypeSql=" and a.operationType in(:operationType)";
			String[] strarr = operationType.split(",");
			for(int b=0;b<strarr.length;b++) {
				list.add(strarr[b]);
			}
			map.put("operationType", list);
		}
		if(!StringUtils.isBlank(startTime)&&!StringUtils.isBlank(endTime)) {
			timeSql=" and a.createTime >=:startTime and a.createTime <=:endTime";
			Date beginDate = DateUtil.fmtYmdHisToDate(startTime);
		    Date endDate = DateUtil.fmtYmdHisToDate(endTime);
			map.put("startTime", beginDate);
			map.put("endTime", endDate);
		}
		map.put("userType", Constants.UserType.DEMO_PLAYER.getCode());
		String sql="from UserAccountDetails a,UserInfo b,SysCode c where a.userId=b.id and a.operationType=c.codeName and c.codeType=:codeTypeNameId and b.userType!=:userType "+userNameSql+amountStartSql+amountEndSql+operationTypeSql+timeSql+" order by a.id desc";
		String sql1="select coalesce(SUM(a.amount),0) from UserAccountDetails a,UserInfo b,SysCode c where a.userId=b.id and a.operationType=c.codeName and c.codeType=:codeTypeNameId and b.userType!=:userType "+userNameSql+amountStartSql+amountEndSql+operationTypeSql+timeSql+" order by a.id desc";
		
		Query<?> query1 = getSessionFactory().getCurrentSession().createQuery(sql1);
		map.put("codeTypeNameId", codeTypeNameId);
		PageBean page=new PageBean();
		page.setPageIndex(pageIndex);
		page.setPageSize(pageSize);
		PageBean pageBean=queryByPagination(page,sql,map);
		if (map != null) {  
            Set<String> keySet = map.keySet();  
            for (String string : keySet) {  
                Object obj = map.get(string);  
            	if(obj instanceof Date){  
                	query1.setParameter(string, (Date)obj,TimestampType.INSTANCE);//query.setParameter(string, (Date)obj,DateType.INSTANCE);   此方法为setDate的替代方法 
                }else if(obj instanceof Object[]){  
                    query1.setParameterList(string, (Object[])obj);
                }else{  
                    query1.setParameter(string, obj); 
                }  
            }  
        }
		map.clear();
		Float sumAmount=null;	
		sumAmount = ((Number)query1.iterate().next()).floatValue();
		map.put("sumAmount", sumAmount);
		map.put("data", pageBean);
		return map;
	}
	//代理的转账记录查询  
	@Override
	public Map<String, Object> queryAgentTransfer(Integer agentId, String startTime, String endTime) {
		Map<String, Object> map=new HashMap<String, Object>();
		List<?> list=queryAgentId(agentId);
		List<?> listData=null;
		if(list!=null&&list.size()>0) {
			String sql="from UserAccountDetails a,UserInfo b where a.userId=b.id and a.operationType=:operationType and a.orderId in(:listId) and a.createTime>=:startTime and a.createTime<=:endTime";
			Query<?> query=getSessionFactory().getCurrentSession().createQuery(sql);
			query.setParameterList("listId", list);
			query.setParameter("operationType", Constants.AccOperationType.TRANSFER.getCode());
		    Date beginDate = DateUtil.fmtYmdHisToDate(startTime);
		    Date endDate = DateUtil.fmtYmdHisToDate(endTime);
		    query.setParameter("startTime", beginDate,DateType.INSTANCE);
		    query.setParameter("endTime", endDate,DateType.INSTANCE);
		    listData=query.list();
		    map.put("data", listData);
			return map;
		}
		map.put("data", listData);
		return map;
	}
	//查询
	private List<?> queryAgentId(Integer agentId){
		String sql1="select id from user_account_details where user_id=:agentId and operation_type=:operationType";
	    Query<?> query1 = getSessionFactory().getCurrentSession().createNativeQuery(sql1);
	    query1.setParameter("agentId", agentId);
	    query1.setParameter("operationType", Constants.AccOperationType.TRANSFER.getCode());
	    List<?> list=query1.list();
		return list;
	}
}

