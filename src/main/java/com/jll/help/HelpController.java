package com.jll.help;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
	
	@ApiComment("Get System History Open Record")
	@RequestMapping(value="/history/open-record", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> getHistoryOpenRecord(
			@PathVariable("lotteryType") String lotteryType,
			@RequestBody PageQueryDao page) {
		return helpService.getHistoryOpenRecord(lotteryType, page);
	}
	
	
	@ApiComment("Get System All Lottory play type")
	@RequestMapping(value="/lottory/play-type", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> getLottoryPlayType(
			@PathVariable("lotteryType") String lotteryType,
			@RequestBody PageQueryDao page) {
		return helpService.getLottoryPlayType(lotteryType, page);
	}

}
