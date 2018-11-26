package com.jll.analysis;

import com.jll.entity.TbLtInfo;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

@Repository
public class TrendAnalysisDaoImpl
  extends HibernateDaoSupport
  implements TrendAnalysisDao
{
  @Autowired
  public void setSuperSessionFactory(SessionFactory sessionFactory)
  {
    super.setSessionFactory(sessionFactory);
  }
  
  public List<TbLtInfo> queryLotteryInfo(String lotteryType, int limitAmount)
  {
    Query query = getSessionFactory().getCurrentSession().createQuery("from TbLtInfo t where t.lotteryType=? order by kjTime desc");
    query.setParameter(0, lotteryType);
    query.setFirstResult(1);
    query.setMaxResults(limitAmount);
    
    return query.list();
  }
}
