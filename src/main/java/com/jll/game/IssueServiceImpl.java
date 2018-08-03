package com.jll.game;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.event.spi.SaveOrUpdateEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jll.common.constants.Constants;
import com.jll.common.constants.Constants.EmailValidState;
import com.jll.common.constants.Constants.PhoneValidState;
import com.jll.common.constants.Constants.SysCodeTypes;
import com.jll.common.constants.Constants.SysCodeUseLists;
import com.jll.common.constants.Constants.UserLevel;
import com.jll.common.constants.Constants.UserState;
import com.jll.common.constants.Constants.UserType;
import com.jll.common.constants.Message;
import com.jll.common.utils.SecurityUtils;
import com.jll.common.utils.StringUtils;
import com.jll.common.utils.Utils;
import com.jll.dao.SupserDao;
import com.jll.entity.Issue;
import com.jll.entity.SysCode;
import com.jll.entity.UserBankCard;
import com.jll.entity.UserInfo;
import com.jll.sysSettings.codeManagement.SysCodeService;
import com.jll.user.wallet.WalletService;

@Configuration
@PropertySource("classpath:sys-setting.properties")
@Service
@Transactional
public class IssueServiceImpl implements IssueService
{
	private Logger logger = Logger.getLogger(IssueServiceImpl.class);

	@Resource
	IssueDao issueDao;
	
	@Override
	public void savePlan(List<Issue> issues) {
		if(issues == null || issues.size() == 0) {
			return ;
		}
		
		issueDao.savePlan(issues);
	}
	
	
}
