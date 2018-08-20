package com.jll.game.playtype;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.jll.dao.DefaultGenericDaoImpl;
import com.jll.entity.PlayType;
import com.jll.entity.SysCode;
@Repository
public class PlayTypeDaoImpl extends DefaultGenericDaoImpl<PlayType> implements PlayTypeDao
{
	private Logger logger = Logger.getLogger(PlayTypeDaoImpl.class);

	@Override
	public List<PlayType> queryByLotteryType(String lotteryType) {
		String sql = "from PlayType where lotteryType=?";
		List<Object> params = new ArrayList<>();
		params.add(lotteryType);
		
		return this.query(sql, params, PlayType.class);
	}

	@Override
	public void addPlayType(PlayType playType) {
		this.saveOrUpdate(playType);
	}

	@Override
	public Integer getCountSeq(String lotteryType) {
		String sql = "from PlayType where lotteryType=:lotteryType ORDER BY seq DESC";
	    Query<PlayType> query = getSessionFactory().getCurrentSession().createQuery(sql,PlayType.class);
	    query.setParameter("lotteryType", lotteryType);
	    query.setMaxResults(1);
	    List<PlayType> list = query.list();
	    Integer seq=0;
	    if(list.size()>0) {
	        for(PlayType playType : list){    
	        	seq=playType.getSeq();
	        } 
	    }
		return seq;
	}
	//修改状态
	@Override
	public void updateState(Integer id, Integer state) {
		Session session=getSessionFactory().getCurrentSession();
		String hql = ("update PlayType set state=:state where id=:id");  
		Query query = session.createQuery(hql);
		query.setParameter("state", state);
		query.setParameter("id", id);
		query.executeUpdate();
	}

	@Override
	public List<PlayType> queryById(Integer id) {
		String sql = "from PlayType where id=?";
		List<Object> params = new ArrayList<>();
		params.add(id);
		return this.query(sql, params, PlayType.class);
	}
	//是否隐藏
	@Override
	public void updateIsHidden(Integer id, Integer isHidden) {
		Session session=getSessionFactory().getCurrentSession();
		String hql = ("update PlayType set isHidden=:isHidden where id=:id");  
		Query query = session.createQuery(hql);
		query.setParameter("isHidden", isHidden);
		query.setParameter("id", id);
		query.executeUpdate();
	}
	//选择单式还是复式
	@Override
	public void updateMulSinFlag(Integer id, Integer mulSinFlag) {
		Session session=getSessionFactory().getCurrentSession();
		String hql = ("update PlayType set mulSinFlag=:mulSinFlag where id=:id");  
		Query query = session.createQuery(hql);
		query.setParameter("mulSinFlag", mulSinFlag);
		query.setParameter("id", id);
		query.executeUpdate();
	}
	//修改
	@Override
	public void updatePlayType(PlayType playType) {
		this.saveOrUpdate(playType);
	}
	
	@Override
	public List<PlayType> queryLotteryType(String lotteryType) {
		String sql = "from PlayType where lotteryType=:lotteryType";
	    Query<PlayType> query = getSessionFactory().getCurrentSession().createQuery(sql,PlayType.class);
	    query.setParameter("lotteryType", lotteryType);
	    List<PlayType> list = query.list();
		return list;
	}
	//查询这个玩法是否存在
	@Override
	public List<PlayType> queryByPlayType(PlayType playType) {
		String lotteryType=playType.getLotteryType();
		String classification=playType.getClassification();
		String ptName=playType.getPtName();
		String ptDesc=playType.getPtDesc();
		Integer mulSinFlag=playType.getMulSinFlag();
		String sql = "from PlayType where lotteryType=:lotteryType and classification=:classification and ptName=:ptName and ptDesc=:ptDesc and mulSinFlag=:mulSinFlag";
	    Query<PlayType> query = getSessionFactory().getCurrentSession().createQuery(sql,PlayType.class);
	    query.setParameter("lotteryType", lotteryType);
	    query.setParameter("classification", classification);
	    query.setParameter("ptName", ptName);
	    query.setParameter("ptDesc", ptDesc);
	    query.setParameter("mulSinFlag", mulSinFlag);
	    List<PlayType> list = query.list();
		return list;
	}
	//修改排序
	@Override
	public void updatePlayTypeSeq(PlayType playType) {
		this.saveOrUpdate(playType);
	}
}
