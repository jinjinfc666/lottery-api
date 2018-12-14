package com.jll.sys.deposit;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
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
	//修改排序
	@Override
	public void updatePayChannelState(Integer id, Integer state) {
		Session session=getSessionFactory().getCurrentSession();
		String hql = ("update PayChannel set state=:state where id=:id");  
		Query query = session.createQuery(hql);
		query.setParameter("state", state);
		query.setParameter("id", id);
		query.executeUpdate();
	}
	//修改激活
	@Override
	public void updatePayChannelEnableMaxAmount(Integer id, Integer enableMaxAmount) {
		Session session=getSessionFactory().getCurrentSession();
		String hql = ("update PayChannel set enableMaxAmount=:enableMaxAmount where id=:id");  
		Query query = session.createQuery(hql);
		query.setParameter("enableMaxAmount", enableMaxAmount);
		query.setParameter("id", id);
		query.executeUpdate();
	}
	//通过充值方式Id查询这个充值方式下的所有充值渠道
	@Override
	public List<PayChannel> queryByPayTypeIdPayChannel(Integer payTypeId) {
		String sql = "from PayChannel where payType=:payTypeId";
	    Query<PayChannel> query = getSessionFactory().getCurrentSession().createQuery(sql,PayChannel.class);
	    query.setParameter("payTypeId", payTypeId);
	    List<PayChannel> list = query.list();
		return list;
	}
	//通过channelName查询
	@Override
	public List<PayChannel> queryByChannelName(String channelName) {
		String sql = "from PayChannel where channelName=:channelName";
	    Query<PayChannel> query = getSessionFactory().getCurrentSession().createQuery(sql,PayChannel.class);
	    query.setParameter("channelName", channelName);
	    List<PayChannel> list = query.list();
		return list;
	}
}
