package com.jll.sysSettings.codeManagement;

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
import com.jll.entity.SysCode;
import com.jll.entity.UserAccountDetails;
import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;


@Api2Doc(id = "sysSettings", name = "Settings code")
@ApiComment(seeClass = SysCode.class)
@RestController
@RequestMapping({"/settings"})
public class BackstageSysController {
	private Logger logger = Logger.getLogger(BackstageSysController.class);
	@Resource
	SysCodeService sysCodeService;
	@RequestMapping(value={"/codeManagement"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> addSysCode(@RequestParam(name = "type", required = true) Integer type,//新增时选择的类型  是大类还是小类  大类为1小类为2
			  @RequestParam(name = "typeCodeName", required = false) String typeCodeName,//如果是小类  此处请填写大类的code_name 因为要通过code_name查找对应的大类的id 而去添加小类
			  @RequestParam(name = "codeName", required = true) String codeName,
			  @RequestParam(name = "codeVal", required = true) String codeVal,
			  @RequestParam(name = "remark", required = true) String remark,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		if(type==null||codeName==null||codeVal==null||remark==null||(type==2 && typeCodeName==null)) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	return ret;
		}
		ret.put("typeCodeName", typeCodeName);
		ret.put("codeName", codeName);
		ret.put("codeVal", codeVal);
		ret.put("remark", remark);
		if(type==1) {
			sysCodeService.saveBigSysCode(ret);
		}else if(type==2){
			sysCodeService.saveSmallSysCode(ret);
		}else {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	return ret;
		}
		
//		logger.debug(flowDetailRecord+"------------------------------queryUserAccountDetails--------------------------------------");
		ret.clear();
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
//		ret.put("data", flowDetailRecord);
		return ret;
	}
	@RequestMapping(value={"/bigType"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> quertBigType() {
		Map<String, Object> ret = new HashMap<>();
		List<SysCode> sysCode=sysCodeService.quertBigType();
		ret.clear();
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put("data", sysCode);
		return ret;
	}
	@RequestMapping(value={"/smallType"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> querySmallType(@RequestParam(name = "id", required = true) Integer id,//此处的id为大类的id因为需要通过大类的id查找出这个大类下对应的所有小类
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		List<SysCode> sysCode=sysCodeService.querySmallType(id);
		ret.clear();
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
		ret.put("data", sysCode);
		return ret;
	}
	@RequestMapping(value={"/updateBigType"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> updateSyscode(@RequestParam(name = "id", required = true) Integer id,
			  @RequestParam(name = "type", required = true) Integer type,//新增时选择的类型  是大类还是小类  大类为1小类为2
			  @RequestParam(name = "codeName", required = false) String codeName,
			  @RequestParam(name = "codeVal", required = false) String codeVal,
			  @RequestParam(name = "seq", required = false) Integer seq,
			  @RequestParam(name = "remark", required = false) String remark,
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		if(codeName==null||codeVal==null||seq==null||remark==null) {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	return ret;
		}
		ret.put("id", id);
		ret.put("type", type);
		ret.put("codeName", codeName);
		ret.put("codeVal", codeVal);
		ret.put("remark", remark);
		if(type==2){
			ret.put("seq", seq);
		}else {
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_ERROR_PARAMS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_ERROR_PARAMS.getErrorMes());
	    	return ret;
		}
		sysCodeService.updateSyscode(ret);
		ret.clear();
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
//		ret.put("data", sysCode);
		return ret;
	}
	@RequestMapping(value={"/updateState"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> updateSyscodeState(@RequestParam(name = "id", required = true) Integer id,
			  @RequestParam(name = "type", required = true) Integer type,//新增时选择的类型  是大类还是小类  大类为1小类为2
			  @RequestParam(name = "state", required = true) Integer state,//1为有效0为无效
			  HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		ret.put("id", id);
		ret.put("type", type);
		ret.put("state", state);
		sysCodeService.updateState(ret);
		ret.clear();
		ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
//		ret.put("data", sysCode);
		return ret;
	}
}