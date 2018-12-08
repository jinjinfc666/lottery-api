package com.jll.sys.promo;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jll.common.constants.Constants;
import com.jll.common.utils.StringUtils;
import com.jll.common.utils.Utils;
import com.jll.dao.PageQueryDao;
import com.jll.entity.Promo;
import com.jll.entity.SysNotification;
import com.jll.entity.UserInfo;
import com.jll.user.UserInfoService;
import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;

@Api2Doc(id = "promo", name = "Promo")
@ApiComment(seeClass = SysNotification.class)
@RestController
@RequestMapping({ "/promo" })
public class PromoController {
	
	@Resource
	PromoService promoService;
	
	@Resource
	UserInfoService userInfoService;

	@ApiComment("Get Promo Lists")
	@RequestMapping(value="/lists", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> list(
			@RequestBody PageQueryDao page) {
		return promoService.getUserPromoLists(page);
	}
	
	@ApiComment("Get System Promo Lists")
	@RequestMapping(value="/sys/lists", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> sysPromolist(
			@RequestBody Promo po,
			@RequestBody PageQueryDao page
			/*@RequestBody Map<String, String> params*/) {
		/*PageQueryDao page = new PageQueryDao();
		page.setStartDate(Utils.toDate(params.get("startDate")));
		page.setEndDate(Utils.toDate(params.get("endDate")));
		page.setPageIndex(Utils.toInteger(params.get("pageIndex")));
		page.setPageSize(Utils.toInteger(params.get("pageSize")));
		
		promoType
		
		isMultiple*/
		return promoService.getPromoLists(po, page);
	}
	//参加活动
	@ApiComment("User accede promo")
	@RequestMapping(value="/accede-promo", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> accedeToPromo(
			@RequestBody Promo po) {
		return promoService.processAccedeToPromo(po);
	}
	//判断用户是否有参加活动的资格
	@ApiComment("User isOrOk accede promo")
	@RequestMapping(value="/qualification-Screening", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> qualificationScreening(
			@RequestBody Promo po) {
		return promoService.processUserQualification(po);
	}
	
	
	
	
	@RequestMapping(value="/listBRecord", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> queryListRecord(@RequestParam(name = "userName", required = false) String userName,
			@RequestParam(name = "startTime", required = true) String startTime,
			@RequestParam(name = "endTime", required = true) String endTime,
			@RequestParam(name = "pageIndex", required = true) Integer pageIndex,
			  HttpServletRequest request) {
		Integer userId=null;
		if(!StringUtils.isBlank(userName)) {
			UserInfo user=userInfoService.getUserByUserName(userName);
			userId=user.getId();
		}
		Integer pageSize=Constants.Pagination.SUM_NUMBER.getCode();
		return promoService.queryRecord(userId, startTime, endTime, pageIndex, pageSize);
	}
	@RequestMapping(value="/listFRecord", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> queryListRecord(@RequestParam(name = "startTime", required = true) String startTime,
			@RequestParam(name = "endTime", required = true) String endTime,
			@RequestParam(name = "pageIndex", required = true) Integer pageIndex,
			  HttpServletRequest request) {
		UserInfo user=userInfoService.getCurLoginInfo();
		Integer userId=user.getId();
		Integer pageSize=Constants.Pagination.SUM_NUMBER.getCode();
		return promoService.queryRecord(userId, startTime, endTime, pageIndex, pageSize);
	}
}
