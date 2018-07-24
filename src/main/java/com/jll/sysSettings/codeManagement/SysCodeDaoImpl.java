package com.jll.sysSettings.codeManagement;

import java.util.List;

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
		String sql = "from SysCode where codeName=?";
	    Query query = getSessionFactory().getCurrentSession().createQuery(sql);
	    query.setParameter(0, typeCodeName);
	    List<SysCode> list = query.list();
	    Integer id=null;
        for(SysCode sysCode : list){    
        	id=sysCode.getId();
        }    
		return id;
	}

	@Override
	public Integer quertSysCodeSeq(Integer codeType) {
		String sql = "from SysCode where codeType=? ORDER BY seq DESC";
	    Query query = getSessionFactory().getCurrentSession().createQuery(sql);
	    query.setParameter(0, codeType);
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
		String sql = "from SysCode where isCodeType=? ORDER BY id";
	    Query query = getSessionFactory().getCurrentSession().createQuery(sql);
	    query.setParameter(0, isCodeType);
	    List<SysCode> list = query.list();
		return list;
	}
	@Override
	public List<SysCode> querySmallType(Integer id) {
		Integer isCodeType=0;
		String sql = "from SysCode where isCodeType=? and codeType=? ORDER BY seq";
	    Query query = getSessionFactory().getCurrentSession().createQuery(sql);
	    query.setParameter(0, isCodeType);
	    query.setParameter(1, id);
	    List<SysCode> list = query.list();
		return list;
	}
	@Override
	public void updateSyscode(SysCode sysCode) {
		Session session=getSessionFactory().getCurrentSession();
//		session.beginTransaction();
		String hql="";
		if(sysCode.getSeq()!=null) {
			hql = ("update SysCode set codeName=?,codeVal=?,seq=?,remark=? where id=?");
		}else {
			hql = ("update SysCode set codeName=?,codeVal=?,remark=? where id=?");
		}
		Query query = session.createQuery(hql);
		query.setParameter(0, sysCode.getCodeName());
		query.setParameter(1, sysCode.getCodeVal());
		if(sysCode.getSeq()!=null) {
			query.setParameter(2, sysCode.getSeq());
			query.setParameter(3, sysCode.getRemark());
			query.setParameter(4, sysCode.getId());
		}else {
			query.setParameter(2, sysCode.getSeq());
			query.setParameter(3, sysCode.getId());
		}
		query.executeUpdate();
//		session.getTransaction().commit();
	}
	@Override
	public void updateBigState(Integer id, Integer state) {
		Session session=getSessionFactory().getCurrentSession();
		String hql = ("update SysCode set state=? where id=? or codeType=?");  
		Query query = session.createQuery(hql);
		query.setParameter(0, state);
		query.setParameter(1, id);
		query.setParameter(2, id);
		query.executeUpdate();
//		getSessionFactory().getCurrentSession().getTransaction().commit();
	}
	@Override
	public void updateSmallState(Integer id, Integer state) {
		Session session=getSessionFactory().getCurrentSession();
		String hql = ("update SysCode set state=? where id=?");  
		Query query = session.createQuery(hql);
		query.setParameter(0, state);
		query.setParameter(1, id);;
		query.executeUpdate();
//		getSessionFactory().getCurrentSession().getTransaction().commit();
	}
	
}
