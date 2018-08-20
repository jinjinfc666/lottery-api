package com.jll.sysSettings.syscode;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.NoResultException;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.type.DateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import com.jll.common.constants.Constants;
import com.jll.dao.DefaultGenericDaoImpl;
import com.jll.entity.SysCode;

@Repository
public class SysCodeDaoImpl extends DefaultGenericDaoImpl<SysCode> implements SysCodeDao {
	private final String state="1";
	@Autowired
	public void setSuperSessionFactory(SessionFactory sessionFactory){
		super.setSessionFactory(sessionFactory);
	}
	

//	@Override
//	public Integer quertSysCodeId(String typeCodeName) {
//		String sql = "from SysCode where codeName=:codeName";
//	    Query query = getSessionFactory().getCurrentSession().createQuery(sql);
//	    query.setParameter("codeName", typeCodeName);
//	    List<SysCode> list = query.list();
//	    Integer id=null;
//        for(SysCode sysCode : list){    
//        	id=sysCode.getId();
//        }    
//		return id;
//	}
//
//	@Override
//	public List<SysCode> querySmallType(Integer id) {
//		Integer isCodeType=0;
//		String sql = "from SysCode where isCodeType=:isCodeType and codeType=:codeType ORDER BY seq";
//	    Query query = getSessionFactory().getCurrentSession().createQuery(sql);
//	    query.setParameter("isCodeType", isCodeType);
//	    query.setParameter("codeType", id);
//	    List<SysCode> list = query.list();
//		return list;
//	}
//	@Override
//	public void updateSyscode(SysCode sysCode) {
//		Session session=getSessionFactory().getCurrentSession();
//		String hql="";
////		String codeNameSql="";
//		String codeValSql="";
//		String remarkSql="";
////		String seqSql="";
////		String codeName=sysCode.getCodeName();
//		String codeVal=sysCode.getCodeVal();
//		String remark=sysCode.getRemark();
////		Integer seq=sysCode.getSeq();
//		Map<String,Object> map=new HashMap();
////		if(!StringUtils.isBlank(codeName)) {
////			codeNameSql="codeName=:codeName,";
////			map.put("codeName", codeName);
////		}
//		if(!StringUtils.isBlank(codeVal)) {
//			codeValSql="codeVal=:codeVal,";
//			map.put("codeVal", codeVal);
//		}
//		if(!StringUtils.isBlank(remark)) {
//			remarkSql="remark=:remark,";
//			map.put("remark", remark);
//		}
////		if(seq!=null) {
////			seqSql="seq=:seq,";
////			map.put("seq", seq);
////		}
//		String sumSql=codeValSql+remarkSql;
//		String sumSql1=sumSql.substring(0, sumSql.length()-1);
//		hql = "update SysCode set "+sumSql1+" where id=:id";
//		
//		Query query = session.createQuery(hql);
//		if (map != null) {  
//            Set<String> keySet = map.keySet();  
//            for (String string : keySet) {  
//                Object obj = map.get(string);  
//            	if(obj instanceof Date){  
//                	query.setParameter(string, (Date)obj,DateType.INSTANCE); //query.setParameter(string, (Date)obj,DateType.INSTANCE);   此方法为setDate的替代方法 
//                }else if(obj instanceof Object[]){  
//                    query.setParameterList(string, (Object[])obj);  
//                }else{  
//                    query.setParameter(string, obj);  
//                }  
//            }  
//        }
//		query.setParameter("id", sysCode.getId());
//		query.executeUpdate();
//	}
//	@Override
//	public void updateBigState(Integer id, Integer state) {
//		Session session=getSessionFactory().getCurrentSession();
//		String hql = ("update SysCode set state=:state where id=:id or codeType=:codeType");  
//		Query query = session.createQuery(hql);
//		query.setParameter("state", state);
//		query.setParameter("id", id);
//		query.setParameter("codeType", id);
//		query.executeUpdate();
//	}
//	@Override
//	public void updateSmallState(Integer id, Integer state) {
//		Session session=getSessionFactory().getCurrentSession();
//		String hql = ("update SysCode set state=:state where id=:id");  
//		Query query = session.createQuery(hql);
//		query.setParameter("state", state);
//		query.setParameter("id", id);
//		query.executeUpdate();
//	}
//	@Override
//	public List<SysCode> queryType(String codeTypeName) {
//	    String sql="select * from sys_code where code_type=(select id from sys_code where code_name=:codeTypeName) order by seq";
//	    Query<SysCode> query = getSessionFactory().getCurrentSession().createNativeQuery(sql).addEntity(SysCode.class);
//	    query.setParameter("codeTypeName", codeTypeName);
//	    List<SysCode> types = new ArrayList<>();
//	    try {			
//	    	types = query.list();
//		}catch(NoResultException ex) {
//			
//		}
//		return types;
//	}
//	@Override
//	public List<SysCode> queryBigCodeName(Integer id) {
//		String sql = "from SysCode where id=:id";
//	    Query query = getSessionFactory().getCurrentSession().createQuery(sql);
//	    query.setParameter("id", id);
//	    List<SysCode> list = query.list();
//		return list;
//	}
//	@Override
//	public List<SysCode> querySmallCodeName(Integer id) {
//		String sql = "from SysCode where id=:id";
//	    Query query = getSessionFactory().getCurrentSession().createQuery(sql);
//	    query.setParameter("id", id);
//	    List<SysCode> list = query.list();
//	    String sql1 = "from SysCode where id=:id";
//	    Query query1 = getSessionFactory().getCurrentSession().createQuery(sql1);
//	    query1.setParameter("id", list.get(0).getCodeType());
//	    List<SysCode> list1 = query1.list();
//		return list1;
//	}
//	@Override
//	public long getCount(String codeName) {
//		String sql = "select count(*) from SysCode where codeType=(select id from SysCode where codeName=:codeName) order by seq";
//		Query query = getSessionFactory().getCurrentSession().createQuery(sql);
//	    query.setParameter("codeName", codeName);
//	    long count = ((Number)query.iterate().next()).longValue();
//	    return count;
//	}
//	@Override
//	public List<SysCode> queryCacheType(String codeName) {
//	    String sql="select * from sys_code where code_type=(select id from sys_code where code_name=:code_name) order by seq";
//	    Query<SysCode> query = getSessionFactory().getCurrentSession().createSQLQuery(sql).addEntity(SysCode.class);
//	    query.setParameter("code_name", codeName);
//	    List<SysCode> types = new ArrayList<>();
//	    try {			
//	    	types = query.list();
//		}catch(NoResultException ex) {
//			
//		}
//		return types;
//	}
//	@Override
//	public List<SysCode> queryCacheTypeOnly(String codeName) {
//		String sql = "from SysCode where codeName=:codeName";
//	    Query query = getSessionFactory().getCurrentSession().createQuery(sql);
//	    query.setParameter("codeName", codeName);
//	    List<SysCode> list = query.list();
//		return list;
//	}
	//-----------------------------------------------------------------------------------------------------------------------
	//-----------------------------------------------------------------------------------------------------------------------
	//-----------------------------------------------------------------------------------------------------------------------
	//-----------------------------------------------------------------------------------------------------------------------
	//通过大类的codeName查询这个大类的id
	@Override
	public Integer queryByCodeName(String codeName) {
		String sql = "from SysCode where codeName=:codeName";
	    Query<SysCode> query = getSessionFactory().getCurrentSession().createQuery(sql,SysCode.class);
	    query.setParameter("codeName", codeName);
	    List<SysCode> list = query.list();
	    Integer id=null;
	    if(list!=null&&list.size()>0) {
	    	id=list.get(0).getId();
	    }
		return id;
	}
	//通过codeType查询出当前seq字段的最后一个值
	@Override
	public Integer quertSysCodeSeq(Integer codeType) {
		String sql = "from SysCode where codeType=:codeType ORDER BY seq DESC";
	    Query<SysCode> query = getSessionFactory().getCurrentSession().createQuery(sql,SysCode.class);
	    query.setParameter("codeType", codeType);
	    query.setMaxResults(1);
	    List<SysCode> list = query.list();
	    Integer seq=0;
	    if(list.size()>0) {
	        for(SysCode sysCode : list){    
	        	seq=sysCode.getSeq();
	        } 
	    }
		return seq;
	}
	//添加
	@Override
	public void saveSysCode(SysCode sysCode) {
		currentSession().save(sysCode);
	}
	//通过小类的codeName和大类的CodeName来查询出这条数据
	@Override
	public List<SysCode> querySmallType(String bigCodeName, String smallCodeName) {
		String sql="select * from sys_code where code_type=(select id from sys_code where code_name=:bigCodeName) and code_name=:smallCodeName order by seq";
		Query<SysCode> query = getSessionFactory().getCurrentSession().createNativeQuery(sql,SysCode.class);
		query.setParameter("bigCodeName", bigCodeName);
		query.setParameter("smallCodeName", smallCodeName);
		List<SysCode> types = new ArrayList<SysCode>();
		try {			
			types = query.list();
		}catch(NoResultException ex) {
			
		}
		return types;
	}
	//查询某个大类下的所有小类
	@Override
	public List<SysCode> queryAllSmallType(String bigCodeName) {
		String sql="select * from sys_code where code_type=(select id from sys_code where code_name=:code_name) order by seq";
	    Query<SysCode> query = getSessionFactory().getCurrentSession().createNativeQuery(sql,SysCode.class);
	    query.setParameter("code_name", bigCodeName);
	    List<SysCode> types = new ArrayList<SysCode>();
	    try {			
	    	types = query.list();
		}catch(NoResultException ex) {
			
		}
		return types;
	}
	//修改某个小类的状态
	@Override
	public void updateSmallTypeState(Integer id, Integer state) {
		Session session=getSessionFactory().getCurrentSession();
		String hql = ("update SysCode set state=:state where id=:id");  
		Query query = session.createQuery(hql);
		query.setParameter("state", state);
		query.setParameter("id", id);
		query.executeUpdate();
	}
	//通过id获得一个SysCode对象
	@Override
	public List<SysCode> queryById(Integer id) {
		String sql = "from SysCode where id=:id";
	    Query<SysCode> query = getSessionFactory().getCurrentSession().createQuery(sql,SysCode.class);
	    query.setParameter("id", id);
	    List<SysCode> list = query.list();
		return list;
	}
	//修改某个大类下的某一条数据
	@Override
	public void updateSmallType(SysCode sysCode) {
		Session session=getSessionFactory().getCurrentSession();
		String hql="";
		String codeValSql="";
		String remarkSql="";
		String codeVal=sysCode.getCodeVal();
		String remark=sysCode.getRemark();
		Map<String,Object> map=new HashMap<String,Object>();
		if(!StringUtils.isBlank(codeVal)) {
			codeValSql="codeVal=:codeVal,";
			map.put("codeVal", codeVal);
		}
		if(!StringUtils.isBlank(remark)) {
			remarkSql="remark=:remark,";
			map.put("remark", remark);
		}
		String sumSql=codeValSql+remarkSql;
		String sumSql1=sumSql.substring(0, sumSql.length()-1);
		hql = "update SysCode set "+sumSql1+" where id=:id";
		
		Query query = session.createQuery(hql);
		if (map != null) {  
            Set<String> keySet = map.keySet();  
            for (String string : keySet) {  
                Object obj = map.get(string);  
            	if(obj instanceof Date){  
                	query.setParameter(string, (Date)obj,DateType.INSTANCE); //query.setParameter(string, (Date)obj,DateType.INSTANCE);   此方法为setDate的替代方法 
                }else if(obj instanceof Object[]){  
                    query.setParameterList(string, (Object[])obj);  
                }else{  
                    query.setParameter(string, obj);  
                }  
            }  
        }
		query.setParameter("id", sysCode.getId());
		query.executeUpdate();
	}
	//修改某个大类的状态
	@Override
	public void updateBigTypeState(Integer id, Integer state) {
		Session session=getSessionFactory().getCurrentSession();
		String hql = ("update SysCode set state=:state where id=:id or codeType=:codeType");  
		Query query = session.createQuery(hql);
		query.setParameter("state", state);
		query.setParameter("id", id);
		query.setParameter("codeType", id);
		query.executeUpdate();
	}
	//查询所有的大类
	@Override
	public List<SysCode> quertBigType() {
		Integer isCodeType=1;
		String sql = "from SysCode where isCodeType=:isCodeType ORDER BY id";
	    Query query = getSessionFactory().getCurrentSession().createQuery(sql);
	    query.setParameter("isCodeType", isCodeType);
	    List<SysCode> list = query.list();
		return list;
	}
	//通过codeName查询代码类型    这个codeName只能是大类的codeName
	@Override
	public List<SysCode> queryByCodeNameBigType(String codeName) {
		String sql = "from SysCode where codeName=:codeName";
	    Query query = getSessionFactory().getCurrentSession().createQuery(sql);
	    query.setParameter("codeName", codeName);
	    List<SysCode> list = query.list();
		return list;
	}
	//修改排序


	@Override
	public void updateSmallTypeSeq(SysCode sysCode) {
		this.saveOrUpdate(sysCode);
	}
}
