package com.jll.game.playtype;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.jll.dao.DefaultGenericDaoImpl;
import com.jll.entity.Issue;
import com.jll.entity.PlayType;

@Repository
public class PlayTypeDaoImpl extends DefaultGenericDaoImpl<PlayType> implements PlayTypeDao
{
	private Logger logger = Logger.getLogger(PlayTypeDaoImpl.class);

	@Override
	public List<PlayType> queryByLotteryType(String lotteryType) {
		String sql = "from PlayType where lotteryType=?";
		List<Object> params = new ArrayList<>();
		params.add(lotteryType);
		
		return this.query(sql, params, PlayType.class);
	}

	
}
