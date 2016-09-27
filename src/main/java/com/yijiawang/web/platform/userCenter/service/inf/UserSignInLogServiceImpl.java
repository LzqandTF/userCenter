package com.yijiawang.web.platform.userCenter.service.inf;

import java.util.Date;

import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yijiawang.web.platform.productCenter.po.SystemDict;
import com.yijiawang.web.platform.productCenter.service.SystemDictService;
import com.yijiawang.web.platform.userCenter.dao.UserSignInLogMapper;
import com.yijiawang.web.platform.userCenter.po.UserScore;
import com.yijiawang.web.platform.userCenter.po.UserSignInLog;
import com.yijiawang.web.platform.userCenter.service.UserScoreService;
import com.yijiawang.web.platform.userCenter.service.UserSignInLogService;
import com.yijiawang.web.platform.userCenter.util.DateUtil;

@Service("userSignInLogService")
public class UserSignInLogServiceImpl implements UserSignInLogService {

	private org.apache.commons.logging.Log log =
	LogFactory.getLog(UserScoreServiceImpl.class);

	@Autowired
	private UserSignInLogMapper userSignInLogMapper;
    @Autowired
    private SystemDictService systemDictService;
	@Autowired
	private UserScoreService userScoreService;

	@Override
	public UserSignInLog userSignIn(String userId, Integer oper) {
		UserSignInLog userSignInLog = new UserSignInLog();
		if (oper.intValue() == 0) { // 获取今日签到信息
			return getUserSignInLog(userId, oper);
		} else if (oper.intValue() == 1) { // 签到
			userSignInLog = getUserSignInLog(userId, oper);
			if (userSignInLog.getSignInStatus().intValue() == 1) {
				return userSignInLog;
			}
			
			// 插入签到数据
			userSignInLog.setId(null);
			userSignInLog.setCreateTime(null);
			userSignInLog.setSignInDay(userSignInLog.getSignInDay()+1);
			userSignInLog.setSignInStatus(1);
			userSignInLogMapper.insertSelective(userSignInLog);
			
			// 插入积分数据
			UserScore userScore = new UserScore();
			userScore.setUserId(userId);
			userScore.setClassCode(UserScore.SCORE_CLASS_CODE_SIGN_IN);
			userScore.setCodeKey(String.valueOf(userSignInLog.getSignInDay()));
			userScore.setClassDesc(String.format(UserScore.SCORE_CLASS_DESC_SIGN_IN, userSignInLog.getSignInDay()));
			userScore.setScoreAmount(userSignInLog.getNextScore());
			userScore.setStatus(UserScore.USER_SCORE_STATUS_1);
			userScoreService.insertSelective(userScore);
		}
		return userSignInLog;
	}

	private UserSignInLog getUserSignInLog(String userId, Integer oper){
		String codeKey = "";
		UserSignInLog userSignInLogRtn = new UserSignInLog();
		userSignInLogRtn.setUserId(userId);
		UserSignInLog userSignInLog = userSignInLogMapper.selectLastOneSignInLogByUserId(userId);
		if (userSignInLog != null) {
			if (compareSameDay(userSignInLog.getCreateTime())) { // 当天已经签到
				if (userSignInLog.getSignInDay().intValue() >= UserSignInLog.MAX_SIGN_IN_DAY) {
					codeKey = String.valueOf(UserSignInLog.MAX_SIGN_IN_DAY);
				} else {
					codeKey = String.valueOf(userSignInLog.getSignInDay()+1);
				}
				BeanUtils.copyProperties(userSignInLog, userSignInLogRtn);
				userSignInLogRtn.setSignInStatus(1);
			} else if (compareSameYesterDay(userSignInLog.getCreateTime())) { // 昨天已经签到
				if (userSignInLog.getSignInDay().intValue() >= UserSignInLog.MAX_SIGN_IN_DAY) {
					codeKey = String.valueOf(UserSignInLog.MAX_SIGN_IN_DAY);
				} else {
					codeKey = String.valueOf(userSignInLog.getSignInDay()+1);
				}
				BeanUtils.copyProperties(userSignInLog, userSignInLogRtn);
				userSignInLogRtn.setSignInStatus(0);
			} else { // 未签到
				codeKey = "1";
				BeanUtils.copyProperties(userSignInLog, userSignInLogRtn);
				userSignInLogRtn.setSignInDay(0);
				userSignInLogRtn.setSignInStatus(0);
			}
		} else { // 未签到
			codeKey = "1";
			userSignInLogRtn.setSignInDay(0);
			userSignInLogRtn.setSignInStatus(0);
		}
		codeKey = oper == 1 && 
				userSignInLogRtn.getSignInStatus().intValue() == 0 &&
				UserSignInLog.MAX_SIGN_IN_DAY != Integer.valueOf(codeKey) 
				? String.valueOf(Integer.valueOf(codeKey)+1) : codeKey;
		userSignInLogRtn.setNextScore(getScoreValue(codeKey));
		return userSignInLogRtn;
	}
	
	private Integer getScoreValue(String codeKey){
		SystemDict systemDict = systemDictService.getSystemDictByCodeKey(UserScore.SCORE_CLASS_CODE_SIGN_IN, codeKey);
		if (systemDict != null) {return Integer.valueOf(systemDict.getCodeValue());}
		return 0;
	}
	
	private boolean compareSameDay(Date getDate) {
		if (getDate == null) {return false;}
		String newGetDate = DateUtil.format(getDate, DateUtil.FORMAT_SHORT_EXT);
		String nowDate = DateUtil.getShortTime();
		//log.error(String.format("compareSameDay= newGetDate=%s : nowDate=%s", newGetDate, nowDate));
		return nowDate.equals(newGetDate);
	}
	
	private boolean compareSameYesterDay(Date getDate) {
		if (getDate == null) {return false;}
		String newGetDate = DateUtil.format(DateUtil.addDay(getDate, 1), DateUtil.FORMAT_SHORT_EXT);
		String nowDate = DateUtil.getShortTime();
		//log.error(String.format("compareSameYesterDay= newGetDate=%s : nowDate=%s", newGetDate, nowDate));
		return nowDate.equals(newGetDate);
	}
}
