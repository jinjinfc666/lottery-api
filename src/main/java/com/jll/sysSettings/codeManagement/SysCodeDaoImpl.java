package com.jll.sysSettings.codeManagement;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import com.jll.common.constants.Constants;
import com.jll.entity.SysCode;

@Repository
public class SysCodeDaoImpl extends HibernateDaoSupport implements SysCodeDao {
	private final String state="1";
	@Autowired
	public void setSuperSessionFactory(SessionFactory sessionFactory){
		super.setSessionFactory(sessionFactory);
	}
	@Override
	public void saveSysCode(Integer codeType, Integer isCodeType, String codeName, String codeVal, Integer seq,
			Integer state, String remark) {
		SysCode sysCode=new SysCode();
		sysCode.setCodeType(codeType);
		sysCode.setIsCodeType(isCodeType);
		sysCode.setCodeName(codeName);
		sysCode.setCodeVal(codeVal);
		sysCode.setSeq(seq);
		sysCode.setState(Constants.SysCodeState.VALID_STATE.getCode());
		sysCode.setRemark(remark);
		currentSession().save(sysCode);
	}

	@Override
	public Integer quertSysCodeId(String typeCodeName) {
		String sql = "from SysCode where codeName=:codeName";
	    Query query = getSessionFactory().getCurrentSession().createQuery(sql);
	    query.setParameter("codeName", typeCodeName);
	    List<SysCode> list = query.list();
	    Integer id=null;
        for(SysCode sysCode : list){    
        	id=sysCode.getId();
        }    
		return id;
	}

	@Override
	public Integer quertSysCodeSeq(Integer codeType) {
		String sql = "from SysCode where codeType=:codeType ORDER BY seq DESC";
	    Query query = getSessionFactory().getCurrentSession().createQuery(sql);
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
	@Override
	public List<SysCode> quertBigType() {
		Integer isCodeType=1;
		String sql = "from SysCode where isCodeType=:isCodeType ORDER BY id";
	    Query query = getSessionFactory().getCurrentSession().createQuery(sql);
	    query.setParameter("isCodeType", isCodeType);
	    List<SysCode> list = query.list();
		return list;
	}
	@Override
	public List<SysCode> querySmallType(Integer id) {
		Integer isCodeType=0;
		String sql = "from SysCode where isCodeType=:isCodeType and codeType=:codeType ORDER BY seq";
	    Query query = getSessionFactory().getCurrentSession().createQuery(sql);
	    query.setParameter("isCodeType", isCodeType);
	    query.setParameter("codeType", id);
	    List<SysCode> list = query.list();
		return list;
	}
	@Override
	public void updateSyscode(SysCode sysCode) {
		Session session=getSessionFactory().getCurrentSession();
		String hql="";
		if(sysCode.getSeq()!=null) {
			hql = ("update SysCode set codeName=:codeName,codeVal=:codeVal,seq=:seq,remark=:remark where id=:id");
		}else {
			hql = ("update SysCode set codeName=:codeName,codeVal=:codeVal,remark=:remark where id=:id");
		}
		Query query = session.createQuery(hql);
		query.setParameter("codeName", sysCode.getCodeName());
		query.setParameter("codeVal", sysCode.getCodeVal());
		if(sysCode.getSeq()!=null) {
			query.setParameter("seq", sysCode.getSeq());
			query.setParameter("remark", sysCode.getRemark());
			query.setParameter("id", sysCode.getId());
		}else {
			query.setParameter("seq", sysCode.getSeq());
			query.setParameter("id", sysCode.getId());
		}
		query.executeUpdate();
	}
	@Override
	public void updateBigState(Integer id, Integer state) {
		Session session=getSessionFactory().getCurrentSession();
		String hql = ("update SysCode set state=:state where id=:id or codeType=:codeType");  
		Query query = session.createQuery(hql);
		query.setParameter("state", state);
		query.setParameter("id", id);
		query.setParameter("codeType", id);
		query.executeUpdate();
	}
	@Override
	public void updateSmallState(Integer id, Integer state) {
		Session session=getSessionFactory().getCurrentSession();
		String hql = ("update SysCode set state=:state where id=:id");  
		Query query = session.createQuery(hql);
		query.setParameter("state", state);
		query.setParameter("id", id);;
		query.executeUpdate();
	}
	@Override
	public List<SysCode> queryType(String bigType) {
	    String sql="select * from sys_code where code_type=(select id from sys_code where code_name=:code_name) and state=:state order by seq";
	    Query<SysCode> query = getSessionFactory().getCurrentSession().createSQLQuery(sql).addEntity(SysCode.class);
	    query.setParameter("code_name", bigType);
	    query.setParameter("state", state);
	    List<SysCode> types = new ArrayList<>();
	    try {			
	    	types = query.list();
		}catch(NoResultException ex) {
			
		}
		return types;
	}
}
