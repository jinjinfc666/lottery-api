package com.jll.sys.promo;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jll.dao.PageQueryDao;
import com.jll.entity.Promo;
import com.jll.entity.SysNotification;
import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;

@Api2Doc(id = "promo", name = "Promo")
@ApiComment(seeClass = SysNotification.class)
@RestController
@RequestMapping({ "/promo" })
public class PromoController {
	
	@Resource
	PromoService promoService;

	@ApiComment("Get Promo Lists")
	@RequestMapping(value="/lists", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> list(
			@RequestBody PageQueryDao page) {
		return promoService.getUserPromoLists(page);
	}
	
	@ApiComment("Get System Promo Lists")
	@RequestMapping(value="/sys/lists", method = { RequestMethod.GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> sysPromolist(
			@RequestBody Promo po,
			@RequestBody PageQueryDao page) {
		return promoService.getPromoLists(po, page);
	}
	
	@ApiComment("User accede promo")
	@RequestMapping(value="/accede-promo/{userId}", method = { RequestMethod.POST}, produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> accedeToPromo(
			@PathVariable("userId") int userId,
			@RequestBody Promo po) {
		return promoService.accedeToPromo(userId, po);
	}
	
}