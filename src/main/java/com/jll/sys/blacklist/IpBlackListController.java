package com.jll.sys.blacklist;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jll.common.cache.CacheRedisService;
import com.jll.common.constants.Constants;
import com.jll.common.constants.Message;
import com.jll.entity.IpBlackList;
import com.jll.entity.UserAccountDetails;
import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;

@Api2Doc(id = "IpBlackList", name = "Ip BlackList")
@ApiComment(seeClass = UserAccountDetails.class)
@RestController
@RequestMapping({"/ip-black-lists"})
public class IpBlackListController {
	private Logger logger = Logger.getLogger(IpBlackListController.class);
	@Resource
	CacheRedisService cacheServ;
	@Resource
	IpBlackListService ipBlackListService;
	//添加
	@RequestMapping(value={"/addIpBlackList"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> addIpBlackList(@RequestParam(name = "ip1", required = true) String ip1,
			  @RequestParam(name = "ip2", required = false) String ip2,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		IpBlackList ipBlackList=new IpBlackList();
		String ip=null;
		if(!StringUtils.isBlank(ip2)) {
			ip=ip1+","+ip2;
		}else {
			ip=ip1;
		}
		ipBlackList.setIp(ip);
		try {
			ret.clear();
			ret=ipBlackListService.addIp(ipBlackList);
			int status=(int) ret.get(Message.KEY_STATUS);
			if(status==Message.status.SUCCESS.getCode()) {
				IpBlackList list=ipBlackListService.queryByIp(ip);
				String codeTypeName=Constants.IpBlackList.IP_BLACK_LIST.getCode();
				cacheServ.setIpBlackList(codeTypeName, list);
			}
			return ret;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//查询所有
	@RequestMapping(value={"/queryAllIpBlackList"}, method={RequestMethod.GET}, produces={"application/json"})
	public Map<String, Object> queryAllIpBlackList() {
		Map<String, Object> ret = new HashMap<>();
		try {
			ret.clear();
			List<IpBlackList> list=ipBlackListService.query();
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			ret.put("data", list);
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//修改
	@RequestMapping(value={"/updateIpBlackList"}, method={RequestMethod.PUT}, produces={"application/json"})
	public Map<String, Object> updateIpBlackList(@RequestParam(name = "id", required = true) Integer id,
			  @RequestParam(name = "ip1", required = true) String ip1,
			  @RequestParam(name = "ip2", required = true) String ip2,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		IpBlackList ipBlackList=new IpBlackList();
		ipBlackList.setId(id);
		String ip=null;
		if(!StringUtils.isBlank(ip2)) {
			ip=ip1+","+ip2;
		}else {
			ip=ip1;
		}
		ipBlackList.setIp(ip);
		try {
			ret.clear();
			ret=ipBlackListService.update(ipBlackList);
			int status=(int) ret.get(Message.KEY_STATUS);
			if(status==Message.status.SUCCESS.getCode()) {
				String codeTypeName=Constants.IpBlackList.IP_BLACK_LIST.getCode();
				IpBlackList list=cacheServ.getIpBlackList(codeTypeName, id);
				list.setIp(ip);
				cacheServ.setIpBlackList(codeTypeName, list);
			}
			return ret;
		}catch(Exception e){
			e.printStackTrace();
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
	//删除
	@RequestMapping(value={"/{id}"}, method={RequestMethod.DELETE}, produces={"application/json"})
	public Map<String, Object> deleteIpBlackList(@PathVariable(name = "id", required = true) Integer id,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		String codeTypeName=Constants.IpBlackList.IP_BLACK_LIST.getCode();
		try {
			ret.clear();
			ret=ipBlackListService.deleteIpBlackList(id);
			int status=(int) ret.get(Message.KEY_STATUS);
			if(status==Message.status.SUCCESS.getCode()) {
				cacheServ.deleteIpBlackList(codeTypeName, id);
			}
			return ret;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
		}
		return ret;
	}
}
