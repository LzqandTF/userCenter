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
	public int saveSelective(UserScore userScore) {
		userScoreMapper.insertSelective(userScore);
		if (userScore.getStatus().intValue() == UserScore.USER_SCORE_STATUS_1.intValue()) {
			return userService.updateIncrUserCredits(userScore.getUserId(), userScore.getScoreAmount());
		} else if (userScore.getStatus().intValue() == UserScore.USER_SCORE_STATUS_2.intValue()) {
			return userService.updateDecrUserCredits(userScore.getUserId(), userScore.getScoreAmount());
		}
		return 0;
	}
	
	private int saveSelectiveNotScore(UserScore userScore) {
		if (userScore.getStatus().intValue() == UserScore.USER_SCORE_STATUS_1.intValue()) {
			return userService.updateIncrUserCredits(userScore.getUserId(), userScore.getScoreAmount());
		} else if (userScore.getStatus().intValue() == UserScore.USER_SCORE_STATUS_2.intValue()) {
			return userService.updateDecrUserCredits(userScore.getUserId(), userScore.getScoreAmount());
		}
		return 0;
	}

	@Override
	public boolean saveUserScoreByRule(String userId, String classCode, String codeKey, String orderId) {
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
			saveSelective(userScore);
		} else if (UserScore.SCORE_CLASS_CODE_LOT_ACCUSATION.equals(classCode)) { // 成功举报拍品
			int scoreAmount = getScoreValue(classCode, codeKey);
			if (scoreAmount == 0) { return false; }
			UserScore userScore = new UserScore();
			userScore.setUserId(userId);
			userScore.setClassCode(classCode);
			userScore.setCodeKey(codeKey);
			userScore.setClassDesc(UserScore.SCORE_CLASS_DESC_LOT_ACCUSATION);
			userScore.setScoreAmount(scoreAmount);
			userScore.setStatus(UserScore.USER_SCORE_STATUS_1);
			saveSelective(userScore);
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
				saveSelective(userScore);
			}
		} else if (UserScore.SCORE_CLASS_CODE_REC_SUBSCRIBE.equals(classCode)) { // 推荐关注，赠与成功推荐者
			// TODO
		} else if (UserScore.SCORE_CLASS_CODE_ILLEGAL_SELLER_NOT_PAY.equals(classCode)) { // 违规 买家不付款
			int illegalCount = userScoreMapper.countUserScoreDataByRule(userId, classCode, null);
			illegalCount = illegalCount%3+1;
			double scoreAmount = getScoreDoubleValue(classCode, String.valueOf(illegalCount));
			if (scoreAmount == 0d) { return false; }
			UserInfo userinfo = userService.getUserByUserId(userId);
			Integer userCredits = userinfo.getUserCredits();
			userCredits = new Double(userCredits*scoreAmount).intValue();
			UserScore userScore = new UserScore();
			userScore.setUserId(userId);
			userScore.setClassCode(classCode);
			userScore.setCodeKey(String.valueOf(illegalCount));
			userScore.setClassDesc(String.format(UserScore.SCORE_CLASS_DESC_ILLEGAL_SELLER_NOT_PAY, illegalCount));
			userScore.setScoreAmount(userCredits);
			userScore.setStatus(UserScore.USER_SCORE_STATUS_2);
            userScore.setOrderId(orderId);
			saveSelective(userScore);
		} else if (UserScore.SCORE_INIT.equals(classCode)) {
			UserInfo userinfo = userService.getUserByUserId(userId);
			if (userinfo == null || userinfo.getUserCredits() != 0) {return false;}
			if (userScoreMapper.countUserScoreDataByRule(userId, null, null) > 0) {return false;}
			UserScore userScore = new UserScore();
			userScore.setUserId(userId);
			userScore.setClassCode(UserScore.SCORE_CLASS_CODE_FIRST_SUBSCRIBE);
			userScore.setCodeKey(UserScore.SCORE_CLASS_CODE_FIRST_SUBSCRIBE);
			userScore.setClassDesc(UserScore.SCORE_CLASS_DESC_FIRST_SUBSCRIBE);
			userScore.setScoreAmount(getScoreValue(UserScore.SCORE_CLASS_CODE_FIRST_SUBSCRIBE, UserScore.SCORE_CLASS_CODE_FIRST_SUBSCRIBE));
			userScore.setStatus(UserScore.USER_SCORE_STATUS_1);
			userScore.setCreateTime(userinfo.getCreateTime());
			saveSelective(userScore);
			int userCredits = (userinfo.getBuyScore() == null || userinfo.getBuyScore() == 0) ? 0 : userinfo.getBuyScore();
			if (userCredits > 0) {
				saveUserScoreByRuleForPay(userId, null, userCredits);
			}
		} else {
			log.error(String.format(UserScore.SCORE_CLASS_NOT_FOUND, classCode, codeKey));
			return false;
		}
		return true;
	}
	
	@Override
	public boolean saveUserScoreByRuleForPay(String userId, String orderId, Integer dealPrice) {
		if (dealPrice == null || dealPrice.intValue() < 1) {return false;}
		int scoreAmount = getScoreValue(UserScore.SCORE_CLASS_CODE_PAY_ONE_DOLLAR, 
				                        UserScore.SCORE_KEY_CODE_PAY_ONE_DOLLAR);
		Integer newDealPrice = dealPrice/Integer.valueOf(UserScore.SCORE_KEY_CODE_PAY_ONE_DOLLAR);
		UserScore userScore = new UserScore();
		userScore.setUserId(userId);
        userScore.setOrderId(orderId);
        userScore.setClassCode(UserScore.SCORE_CLASS_CODE_PAY_ONE_DOLLAR);
        userScore.setCodeKey(UserScore.SCORE_KEY_CODE_PAY_ONE_DOLLAR);
        userScore.setClassDesc(String.format(UserScore.SCORE_CLASS_DESC_PAY_DOLLAR, newDealPrice));
        userScore.setScoreAmount(newDealPrice*scoreAmount);
        userScore.setStatus(UserScore.USER_SCORE_STATUS_1);
		saveSelective(userScore);
		return true;
	}

    @Override
    public List<UserScore> getUserScoreByOrderId(String orderId) {
        return userScoreMapper.getUserScoreByOrderId(orderId);
    }

    @Override
    public boolean rollbackUserScore(String userId, String orderId, Integer amount) {
        if (amount == null || amount.intValue() < 1) {return false;}
        UserScore userScore = new UserScore();
        userScore.setUserId(userId);
        userScore.setOrderId(orderId);
        userScore.setClassCode(UserScore.SCORE_CLASS_CODE_BACK_SCORE);
        userScore.setCodeKey(UserScore.SCORE_KEY_CODE_PAY_ONE_DOLLAR);
        userScore.setClassDesc(UserScore.SCORE_CLASS_DESC_BACK_SCORE);
        userScore.setScoreAmount(amount);
        userScore.setStatus(UserScore.USER_SCORE_STATUS_1);
        saveSelective(userScore);
        return true;
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
