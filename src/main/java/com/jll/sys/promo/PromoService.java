package com.jll.sys.promo;

import java.util.Map;

import com.jll.dao.PageQueryDao;
import com.jll.entity.Promo;
import com.jll.entity.UserInfo;

public interface PromoService
{
	Map<String, Object> getUserPromoLists(PageQueryDao page);
	Map<String, Object> getPromoLists(Promo po, PageQueryDao page);
	Map<String, Object> processAccedeToPromo(Promo po);
	Map<String, Object> processAccedeToLuckyDrwPromo(Promo dbPro,UserInfo userInfo);
	Map<String, Object> processAccedeTodaySingInDayPromo(Promo dbPro,UserInfo userInfo);
	Promo getPromoByCode(String pCode);
}
