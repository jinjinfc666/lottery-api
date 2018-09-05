package com.jll.help;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jll.common.utils.Utils;
import com.jll.dao.PageQueryDao;
import com.jll.entity.PlayType;
import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;

@Api2Doc(id = "HelpCenter", name = "Help Center")
@ApiComment(seeClass = PlayType.class)
@RestController
@RequestMapping({ "/help" })
public class HelpController {
	
	@Resource
	HelpService helpService;
	
	
	/**
	 * @param params  
	 * {
	"pageIndex":1,
	"pageSize":20
	}
	 * @return
	 */
	
	@ApiComment("Get System History Open Record")
	@RequestMapping(value="/history/open-record/{lotteryType}", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> getHistoryOpenRecord(
			@PathVariable(name="lotteryType", required = true) String lotteryType,
			@RequestBody PageQueryDao page) {
		return helpService.getHistoryOpenRecord(lotteryType, page);
	}
	
	
	/**
	 * @param params  
	 * {
	"pageIndex":1,
	"pageSize":20,
	"lotteryType":"cqssc"
	}
	 * @return
	 */
	@ApiComment("Get System All Lottory play type")
	@RequestMapping(value="/lottory/play-type", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> getLottoryPlayType(@RequestBody Map<String, String> params) {
		String lotteryType = Utils.toString(params.get("lotteryType"));
		PageQueryDao page = new PageQueryDao(Utils.toInteger(params.get("pageIndex")),
				Utils.toInteger(params.get("pageSize")));
		return helpService.getLottoryPlayType(lotteryType, page);
	}

}
