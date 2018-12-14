package com.jll.sys.deposit;




import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.jll.dao.DefaultGenericDaoImpl;
import com.jll.entity.IpBlackList;
import com.jll.entity.PayType;
import com.jll.entity.SysCode;
@Repository
public class PayTypeDaoImpl extends DefaultGenericDaoImpl<PayType> implements PayTypeDao
{
	private Logger logger = Logger.getLogger(PayTypeDaoImpl.class);
	//添加
	@Override
	public void addPayType(PayType payType) {
		this.saveOrUpdate(payType);
	}
	//通过条件查询
	@Override
	public List<PayType> queryBy(String name, String nickName, String platId) {
		String sql="from PayType where name=:name and nickName=:nickName and platId=:platId";
	    Query<PayType> query = getSessionFactory().getCurrentSession().createQuery(sql,PayType.class);
	    query.setParameter("name", name);
	    query.setParameter("nickName", nickName);
	    query.setParameter("platId", platId);
	    List<PayType> list = query.list();
	    return list;
	}
	//查询表中的排序
	@Override
	public Integer quertPayTypeSeq() {
		String sql = "from PayType ORDER BY seq DESC";
	    Query<PayType> query = getSessionFactory().getCurrentSession().createQuery(sql,PayType.class);
	    query.setMaxResults(1);
	    List<PayType> list = query.list();
	    Integer seq=0;
	    if(list.size()>0) {
	        for(PayType payType : list){    
	        	seq=payType.getSeq();
	        } 
	    }
		return seq;
	}
	//修改
	@Override
	public void updatePayType(PayType payType) {
		this.saveOrUpdate(payType);
	}
	//通过id查找存不存在
	@Override
	public List<PayType> queryById(Integer id) {
		String sql="from PayType where id=:id";
	    Query<PayType> query = getSessionFactory().getCurrentSession().createQuery(sql,PayType.class);
	    query.setParameter("id", id);
	    List<PayType> list = query.list();
	    return list;
	}
	//查询所有数据
	@Override
	public List<?> queryAllPayType(Integer bigCodeNameId) {
		String sql="from PayType a,SysCode b where a.platId=b.codeName and b.codeType=:bigCodeNameId";
	    Query<?> query = getSessionFactory().getCurrentSession().createQuery(sql);
	    query.setParameter("bigCodeNameId", bigCodeNameId);
	    List<?> list = query.list();
	    return list;
	}
	//通过seq  查询数据
	@Override
	public PayType queryBySeq(Integer seq) {
		String sql="from PayType where seq=:seq";
	    Query<PayType> query = getSessionFactory().getCurrentSession().createQuery(sql,PayType.class);
	    query.setParameter("seq", seq);
	    List<PayType> list = query.list();
	    return list.get(0);
	}
	@Override
	public List<PayType> queryAllPayType() {
		String sql="from PayType";
	    Query<PayType> query = getSessionFactory().getCurrentSession().createQuery(sql,PayType.class);
	    List<PayType> list = query.list();
	    return list;
	}
	//通过id查询某一条数据
	@Override
	public List<?> queryPayTypeById(Integer id,Integer bigCodeNameId) {
		String sql="from PayType a,SysCode b where a.platId=b.codeName and b.codeType=:bigCodeNameId and a.id=:id";
	    Query<?> query = getSessionFactory().getCurrentSession().createQuery(sql);
	    query.setParameter("bigCodeNameId", bigCodeNameId);
	    query.setParameter("id", id);
	    List<?> list = query.list();
	    return list;
	}
	@Override
	public List<PayType> queryByPlatId(String platId) {
		String sql="from PayType where platId=:platId";
	    Query<PayType> query = getSessionFactory().getCurrentSession().createQuery(sql,PayType.class);
	    query.setParameter("platId", platId);
	    List<PayType> list = query.list();
	    return list;
	}
	@Override
	public List<PayType> queryByName(String name) {
		String sql="from PayType where name=:name";
	    Query<PayType> query = getSessionFactory().getCurrentSession().createQuery(sql,PayType.class);
	    query.setParameter("name", name);
	    List<PayType> list = query.list();
	    return list;
	}
}
