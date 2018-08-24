package com.jll.sys.deposit;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.jll.dao.DefaultGenericDaoImpl;
import com.jll.entity.PayChannel;
import com.jll.entity.PayType;
@Repository
public class PayChannelDaoImpl extends DefaultGenericDaoImpl<PayChannel> implements PayChannelDao
{
	private Logger logger = Logger.getLogger(PayChannelDaoImpl.class);
	//添加
	@Override
	public void addPayChannel(PayChannel payChannel) {
		this.saveOrUpdate(payChannel);
	}
	//查找最后一条数据
	@Override
	public List<PayChannel> queryLast() {
		String sql = "from PayChannel ORDER BY id DESC";
	    Query<PayChannel> query = getSessionFactory().getCurrentSession().createQuery(sql,PayChannel.class);
	    query.setMaxResults(1);
	    List<PayChannel> list = query.list();
		return list;
	}
	//查询表中的排序
	@Override
	public Integer quertPayChannelSeq() {
		String sql = "from PayChannel ORDER BY seq DESC";
	    Query<PayChannel> query = getSessionFactory().getCurrentSession().createQuery(sql,PayChannel.class);
	    query.setMaxResults(1);
	    List<PayChannel> list = query.list();
	    Integer seq=0;
	    if(list.size()>0) {
	        for(PayChannel payChannel : list){    
	        	seq=payChannel.getSeq();
	        } 
	    }
		return seq;
	}
	@Override
	public void updatePayChannel(PayChannel payChannel) {
		this.saveOrUpdate(payChannel);
	}
	//通过id查询
	@Override
	public List<PayChannel> queryById(Integer id) {
		String sql = "from PayChannel where id=:id";
	    Query<PayChannel> query = getSessionFactory().getCurrentSession().createQuery(sql,PayChannel.class);
	    query.setParameter("id", id);
	    List<PayChannel> list = query.list();
		return list;
	}
	//查询所有 
	@Override
	public List<PayChannel> queryAll() {
		String sql = "from PayChannel";
	    Query<PayChannel> query = getSessionFactory().getCurrentSession().createQuery(sql,PayChannel.class);
	    List<PayChannel> list = query.list();
		return list;
	}
	
}
