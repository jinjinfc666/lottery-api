package com.jll.analysis;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.jll.entity.TbLtInfo;

@RestController
@RequestMapping({"/analysis"})
public class AnalysisController
{
  private Logger logger = Logger.getLogger(AnalysisController.class);
  @Resource
  TrendAnalysisService trendAnalysisService;
  
  @RequestMapping(value={"/trendsAnalysis"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public ModelAndView trendAnalysis(@RequestParam("typeid") String typeId, @RequestParam("num") int limitAmount)
  {
    List<TbLtInfo> recs = null;
    if ((StringUtils.isBlank(typeId)) || 
      (limitAmount <= 0) || 
      (limitAmount >= 1000)) {
      recs = new ArrayList();
    } else {
      recs = this.trendAnalysisService.queryLotteryInfo(typeId, limitAmount);
    }
    ModelAndView view = new ModelAndView();
    view.setViewName("trendAnysis");
    view.addObject("list", recs);
    
    return view;
  }
}
