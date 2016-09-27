package com.yijiawang.web.platform.userCenter.service.inf;

import java.util.Date;
import java.util.List;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yijiawang.web.platform.productCenter.po.SystemDict;
import com.yijiawang.web.platform.productCenter.service.SystemDictService;
import com.yijiawang.web.platform.userCenter.dao.UserScoreMapper;
import com.yijiawang.web.platform.userCenter.po.UserScore;
import com.yijiawang.web.platform.userCenter.service.UserScoreService;

@Service("userScoreService")
public class UserScoreServiceImpl implements UserScoreService {

    private org.apache.commons.logging.Log log = LogFactory.getLog(UserScoreServiceImpl.class);

	@Autowired
	private UserScoreMapper userScoreMapper;
    @Autowired
    private SystemDictService systemDictService;
	
	@Override
	public List<UserScore> queryListUserScoreByUserId(String userId, Long cursor, Integer count) {
		return userScoreMapper.queryListUserScoreByUserId(userId, cursor, count);
	}

	@Override
	public int insertSelective(UserScore userScore) {
		return userScoreMapper.insertSelective(userScore);
	}

	@Override
	public boolean saveUserScoreByRule(String userId, String classCode, String codeKey){
		try {
			int scoreAmount = 0;
			if (UserScore.SCORE_CLASS_CODE_FIRST_SUBSCRIBE.equals(classCode)) { // 首次关注
				scoreAmount = getScoreValue(classCode, codeKey);
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
					scoreAmount = getScoreValue(classCode, codeKey);
					UserScore userScore = new UserScore();
					userScore.setUserId(userId);
					userScore.setClassCode(classCode);
					userScore.setCodeKey(codeKey);
					userScore.setClassDesc(UserScore.SCORE_CLASS_DESC_SHARE_LINK);
					userScore.setScoreAmount(scoreAmount);
					userScore.setStatus(UserScore.USER_SCORE_STATUS_1);
					insertSelective(userScore);
				}
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
	
}
