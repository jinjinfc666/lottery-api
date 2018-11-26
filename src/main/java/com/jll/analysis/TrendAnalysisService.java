package com.jll.analysis;

import com.jll.entity.TbLtInfo;
import java.util.List;

public abstract interface TrendAnalysisService
{
  public abstract List<TbLtInfo> queryLotteryInfo(String paramString, int paramInt);
}
