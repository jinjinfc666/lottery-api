package com.jll.analysis;

import com.jll.entity.TbLtInfo;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TrendAnalysisServiceImpl
  implements TrendAnalysisService
{
  @Resource
  TrendAnalysisDao trendAnalysisDao;
  
  public List<TbLtInfo> queryLotteryInfo(String lotteryType, int limitAmount)
  {
    return this.trendAnalysisDao.queryLotteryInfo(lotteryType, limitAmount);
  }
}
