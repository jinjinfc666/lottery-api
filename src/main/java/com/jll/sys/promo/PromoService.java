package com.jll.sys.promo;

import java.util.Map;

import com.jll.dao.PageQueryDao;
import com.jll.entity.Promo;
import com.jll.entity.SysNotification;
import com.jll.entity.UserInfo;

public interface PromoService
{
	Map<String, Object> getUserPromoLists(PageQueryDao page);
	Map<String, Object> getPromoLists(Promo po, PageQueryDao page);
	Map<String, Object> accedeToPromo(int userId,Promo po);
	Map<String, Object> accedeToLuckyDrwPromo(Promo dbPro,UserInfo userInfo);
}
