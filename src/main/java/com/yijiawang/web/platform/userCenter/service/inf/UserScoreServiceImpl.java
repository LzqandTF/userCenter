package com.yijiawang.web.platform.userCenter.service.inf;

import java.util.Date;
import java.util.List;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yijiawang.web.platform.productCenter.po.SystemDict;
import com.yijiawang.web.platform.productCenter.service.SystemDictService;
import com.yijiawang.web.platform.userCenter.dao.UserScoreMapper;
import com.yijiawang.web.platform.userCenter.po.UserInfo;
import com.yijiawang.web.platform.userCenter.po.UserScore;
import com.yijiawang.web.platform.userCenter.service.UserScoreService;
import com.yijiawang.web.platform.userCenter.service.UserService;

@Service("userScoreService")
public class UserScoreServiceImpl implements UserScoreService {

    private org.apache.commons.logging.Log log = LogFactory.getLog(UserScoreServiceImpl.class);

	@Autowired
	private UserScoreMapper userScoreMapper;
	@Autowired
	private UserService userService;
    @Autowired
    private SystemDictService systemDictService;
	
	@Override
	public List<UserScore> queryListUserScoreByUserId(String userId, Long cursor, Integer count) {
		return userScoreMapper.queryListUserScoreByUserId(userId, cursor, count);
	}

	@Override
	public int insertSelective(UserScore userScore) {
		if (userScoreMapper.insertSelective(userScore) == 1) {
			if (userScore.getStatus().intValue() == UserScore.USER_SCORE_STATUS_1.intValue()) {
				return userService.incrUserCredits(userScore.getUserId(), userScore.getScoreAmount());
			} else if (userScore.getStatus().intValue() == UserScore.USER_SCORE_STATUS_2.intValue()) {
				return userService.decrUserCredits(userScore.getUserId(), userScore.getScoreAmount());
			}
		};
		return 0;
	}

	@Override
	public boolean saveUserScoreByRule(String userId, String classCode, String codeKey){
		try {
			if (UserScore.SCORE_CLASS_CODE_FIRST_SUBSCRIBE.equals(classCode)) { // 首次关注
				int scoreAmount = getScoreValue(classCode, codeKey);
				if (scoreAmount == 0) { return false; }
				UserScore userScore = new UserScore();
				userScore.setUserId(userId);
				userScore.setClassCode(classCode);
				userScore.setCodeKey(codeKey);
				userScore.setClassDesc(UserScore.SCORE_CLASS_DESC_FIRST_SUBSCRIBE);
				userScore.setScoreAmount(scoreAmount);
				userScore.setStatus(UserScore.USER_SCORE_STATUS_1);
				insertSelective(userScore);
			} else if (UserScore.SCORE_CLASS_CODE_SHARE_LINK.equals(classCode)) { // 分享链接
				if (userScoreMapper.countCurdateDataByRule(userId, classCode, new Date()) < UserScore.SCORE_CLASS_SHARE_LINK_MAX) {
					int scoreAmount = getScoreValue(classCode, codeKey);
					if (scoreAmount == 0) { return false; }
					UserScore userScore = new UserScore();
					userScore.setUserId(userId);
					userScore.setClassCode(classCode);
					userScore.setCodeKey(codeKey);
					userScore.setClassDesc(UserScore.SCORE_CLASS_DESC_SHARE_LINK);
					userScore.setScoreAmount(scoreAmount);
					userScore.setStatus(UserScore.USER_SCORE_STATUS_1);
					insertSelective(userScore);
				}
			} else if (UserScore.SCORE_CLASS_CODE_REC_SUBSCRIBE.equals(classCode)) { // 推荐关注，赠与成功推荐者
				// TODO
				
			} else if (UserScore.SCORE_CLASS_CODE_ILLEGAL_SELLER_NOT_PAY.equals(classCode)) { // 违规 买家不付款
				int illegalCount = userScoreMapper.countUserScoreDataByRule(userId, classCode, null);
				illegalCount = illegalCount == 0 ? 1 : illegalCount+1;
				double scoreAmount = getScoreDoubleValue(classCode, String.valueOf(illegalCount));
				if (scoreAmount == 0d) { return false; }
				UserInfo userinfo = userService.getUserByUserId(userId);
				Integer userCredits = userinfo.getUserCredits();
				userCredits = new Double(userCredits*scoreAmount).intValue();
				UserScore userScore = new UserScore();
				userScore.setUserId(userId);
				userScore.setClassCode(classCode);
				userScore.setCodeKey(String.valueOf(illegalCount));
				userScore.setClassDesc(UserScore.SCORE_CLASS_DESC_ILLEGAL_SELLER_NOT_PAY);
				userScore.setScoreAmount(userCredits);
				userScore.setStatus(UserScore.USER_SCORE_STATUS_2);
				insertSelective(userScore);
			} else {
				log.error(String.format(UserScore.SCORE_CLASS_NOT_FOUND, classCode, codeKey));
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	private Integer getScoreValue(String classCode, String codeKey){
		SystemDict systemDict = systemDictService.getSystemDictByCodeKey(classCode, codeKey);
		if (systemDict != null) {return Integer.valueOf(systemDict.getCodeValue());}
		log.error(String.format("classCode=%s ; codeKey=%s 在system_dict表中不存在！", classCode, codeKey));
		return 0;
	}
	
	private double getScoreDoubleValue(String classCode, String codeKey){
		SystemDict systemDict = systemDictService.getSystemDictByCodeKey(classCode, codeKey);
		if (systemDict != null) {return Double.valueOf(systemDict.getCodeValue());}
		log.error(String.format("classCode=%s ; codeKey=%s 在system_dict表中不存在！", classCode, codeKey));
		return 0d;
	}
	
}
