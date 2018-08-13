package com.jll.game.playtype;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.cache.CacheRedisService;
import com.jll.entity.PlayType;

@Configuration
@PropertySource("classpath:sys-setting.properties")
@Service
@Transactional
public class PlayTypeServiceImpl implements PlayTypeService
{
	private Logger logger = Logger.getLogger(PlayTypeServiceImpl.class);

	@Resource
	PlayTypeDao playTypeDao;

	@Resource
	CacheRedisService cacheServ;
	
	@Override
	public List<PlayType> queryPlayType(String lotteryType) {
		List<PlayType> playTypes = playTypeDao.queryByLotteryType(lotteryType);
		return playTypes;
	}
	//添加
	@Override
	public void addPlayType(PlayType playType) {
		Integer seq=playTypeDao.getCountSeq(playType.getLotteryType())+1;
		playType.setSeq(seq);
        playType.setCreateTime(new Date());
		playTypeDao.addPlayType(playType);
		String lotteryType=playType.getLotteryType();
		List<PlayType> playTypes=playTypeDao.queryByLotteryType(lotteryType);
		cacheServ.setPlayType(lotteryType, playTypes);
	}
	//修改状态
	@Override
	public void updateState(Integer id, Integer state) {
		playTypeDao.updateState(id, state);
		List<PlayType> playType=playTypeDao.queryById(id);
		if(playType!=null&&playType.size()>0) {
			String lotteryType=playType.get(0).getLotteryType();
			List<PlayType> playTypes=playTypeDao.queryByLotteryType(lotteryType);
			cacheServ.setPlayType(lotteryType, playTypes);
		}
	}
	//设置是否隐藏
	@Override
	public void updateIsHidden(Integer id, Integer isHidden) {
		playTypeDao.updateIsHidden(id, isHidden);
		List<PlayType> playType=playTypeDao.queryById(id);
		if(playType != null && playType.size() > 0) {
			String lotteryType=playType.get(0).getLotteryType();
			List<PlayType> playTypes=playTypeDao.queryByLotteryType(lotteryType);
			cacheServ.setPlayType(lotteryType, playTypes);
		}
	}
	//选择单式还是复式
	@Override
	public void updateMulSinFlag(Integer id, Integer mulSinFlag) {
		playTypeDao.updateMulSinFlag(id, mulSinFlag);
		List<PlayType> playType=playTypeDao.queryById(id);
		if(playType != null && playType.size() > 0) {
			String lotteryType=playType.get(0).getLotteryType();
			List<PlayType> playTypes=playTypeDao.queryByLotteryType(lotteryType);
			cacheServ.setPlayType(lotteryType, playTypes);
		}
	}
	//修改
	@Override
	public void updatePlayType(PlayType playType) {
		Integer id=playType.getId();
		String lotteryType=playType.getLotteryType();
		String classification=playType.getClassification();
		String pdName=playType.getPtName();
		String ptDesc=playType.getPtDesc();
		Integer state=playType.getState();
		Integer mulSinFlag=playType.getMulSinFlag();
		Integer isHidden=playType.getIsHidden();
		List<PlayType> playType1=playTypeDao.queryById(id);
		if(playType1 != null && playType1.size() > 0) {
			if(lotteryType.equals(playType1.get(0).getLotteryType())) {
				Integer seq=null;
				playTypeDao.updatePlayType(id,lotteryType,classification, pdName, ptDesc,state,mulSinFlag,isHidden,seq);
				List<PlayType> playTypes=playTypeDao.queryByLotteryType(lotteryType);
				cacheServ.setPlayType(lotteryType, playTypes);
			}else {
				Integer seq=playTypeDao.getCountSeq(lotteryType)+1;
				playType.setSeq(seq);
				playTypeDao.updatePlayType(id,lotteryType,classification, pdName, ptDesc,state,mulSinFlag,isHidden,seq);
				List<PlayType> playTypes=playTypeDao.queryLotteryType(playType1.get(0).getLotteryType());
				cacheServ.setPlayType(playType1.get(0).getLotteryType(), playTypes);
				List<PlayType> playTypes1=playTypeDao.queryLotteryType(lotteryType);
				cacheServ.setPlayType(lotteryType, playTypes1);
			}
		}
	}
}

