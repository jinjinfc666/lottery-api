package com.jll.sys.sig;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jll.common.constants.Constants;
import com.jll.common.constants.Message;
import com.jll.common.utils.StringUtils;
import com.jll.entity.SignupRec;
import com.jll.entity.UserAccount;
import com.jll.entity.UserInfo;
import com.jll.user.UserInfoService;
import com.jll.user.account.UserAccountService;
import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;

@Api2Doc(id = "signup", name = "签到")
@ApiComment(seeClass = UserInfo.class)
@RestController
@RequestMapping({ "/signup" })
public class SignupRecController {
	private Logger logger = Logger.getLogger(SignupRecController.class);

	@Resource
	SignupRecService signupRecService;
	@Resource
	UserInfoService userInfoService;
	@Resource
	UserAccountService userAccountService;
	//通过用户名(false)或时间去查询(true)
	@RequestMapping(value={"/sgnupRecSave"}, method={RequestMethod.POST}, produces={"application/json"})
	public Map<String, Object> sgnupRecSave() {
		Map<String, Object> ret = new HashMap<>();
		try {
			UserInfo user=userInfoService.getCurLoginInfo();
			if(null == user){
				ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
				ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_NO_VALID_USER.getCode());
				ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_NO_VALID_USER.getErrorMes());
				return ret;
			}
			Integer count=signupRecService.getCount(user.getId());
			if(count>0) {
				ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
				ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_USER_SIGNED_IN.getCode());
				ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_USER_SIGNED_IN.getErrorMes());
				return ret;
			}
			SignupRec signupRec=new SignupRec();
			signupRec.setUserId(user.getId());
			signupRec.setCreateTime(new Date());
			signupRec.setRewardPoints(Constants.SignupRecClass.SIGN_IN_INTEGRATION.getCode());
			signupRecService.saveSignupRec(signupRec);
			UserAccount userAccount=userAccountService.getUserAccount(user.getId());
			Long rewardPoints=userAccount.getRewardPoints();
			userAccount.setRewardPoints(rewardPoints+Constants.SignupRecClass.SIGN_IN_INTEGRATION.getCode());
			userAccountService.saveUserAccount(userAccount);
			ret.put(Message.KEY_STATUS, Message.status.SUCCESS.getCode());
			return ret;
		}catch(Exception e){
			ret.clear();
			ret.put(Message.KEY_STATUS, Message.status.FAILED.getCode());
			ret.put(Message.KEY_ERROR_CODE, Message.Error.ERROR_COMMON_OTHERS.getCode());
			ret.put(Message.KEY_ERROR_MES, Message.Error.ERROR_COMMON_OTHERS.getErrorMes());
			return ret;
		}
	}
}
