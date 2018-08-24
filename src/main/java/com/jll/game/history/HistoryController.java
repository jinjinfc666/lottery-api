package com.jll.game.history;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jll.common.constants.Message;
import com.jll.entity.Issue;
import com.jll.entity.SysCode;
import com.jll.game.IssueService;
import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;


@Api2Doc(id = "historys", name = "历史查询")
@ApiComment(seeClass = SysCode.class)
@RestController
@RequestMapping({"/history"})
public class HistoryController {
	private Logger logger = Logger.getLogger(HistoryController.class);
	@Resource
	IssueService issueService;
	
	//增加大类
	@RequestMapping(value={"/queryIssueByNumber"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> queryIssueByNumber(@RequestParam(name = "lotteryType", required = true) String lotteryType,
			  @RequestParam(name = "number", required = true) Integer number,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		try {
			List<Issue> list=issueService.queryByLTNumber(lotteryType, number);
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", list);
		}catch(Exception e){
			ret.clear();
			e.printStackTrace();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
}
