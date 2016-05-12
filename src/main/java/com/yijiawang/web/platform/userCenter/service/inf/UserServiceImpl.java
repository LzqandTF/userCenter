package com.yijiawang.web.platform.userCenter.service.inf;

import com.yijiawang.web.platform.userCenter.dao.ProtectAnswerMapper;
import com.yijiawang.web.platform.userCenter.dao.ProtectQuestionMapper;
import com.yijiawang.web.platform.userCenter.dao.UserPayInfoMapper;
import com.yijiawang.web.platform.userCenter.po.ProtectAnswer;
import com.yijiawang.web.platform.userCenter.po.UserPayInfo;
import com.yijiawang.web.platform.userCenter.vo.UserProtectQuestionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yijiawang.web.platform.userCenter.dao.UserInfoMapper;
import com.yijiawang.web.platform.userCenter.po.UserInfo;
import com.yijiawang.web.platform.userCenter.service.UserService;

import java.util.List;

@Service("userService")
public class UserServiceImpl implements UserService{
	@Autowired
	private UserInfoMapper userInfoMapper;
    @Autowired
    private ProtectQuestionMapper protectQuestionMapper;
    @Autowired
    private ProtectAnswerMapper protectAnswerMapper;
    @Autowired
    private UserPayInfoMapper userPayInfoMapper;
	
	
	@Override
	public UserInfo getUserByUserId(String userId) {
		return userInfoMapper.getUserByUserId(userId);
	}


	@Override
	public int updateByPrimaryKey(UserInfo userInfo) {
		return userInfoMapper.updateByPrimaryKey(userInfo);
	}


	@Override
	public int insertSelective(UserInfo userInfo) {
		return userInfoMapper.insertSelective(userInfo);
	}


	@Override
	public int updateByUserId(UserInfo userInfo) {
		return userInfoMapper.updateByPrimaryKeySelective(userInfo);
	}

    @Override
    public UserPayInfo getUserPayInfo(String userId) {
        return userPayInfoMapper.selectByUserId(userId);
    }

    @Override
    public List<UserProtectQuestionVO> getProtectQuestion(String userId) {
        UserPayInfo payInfo = getUserPayInfo(userId);
        if (payInfo != null && payInfo.getPayPwd() != null && payInfo.getPayPwd().length() > 0) {
            return protectQuestionMapper.userProtectQuestion(userId);
        }
        return protectQuestionMapper.userProtectQuestion(null);
    }

    /**
     * 保存用户密保问题(如果是第一次还要保存密保问题)
     * @param userId
     * @param payPassword
     * @param questions
     * @return  0-操作成功
     *          1-密保问题回答不正确
     */
    @Override
    public int saveUserProtectInfo(String userId, String payPassword, List<UserProtectQuestionVO> questions) {
        // 保存密保问题,如果已经设置过密保问题则进行校验
        List<UserProtectQuestionVO> currList = getProtectQuestion(userId);
        if (currList.size() == 2) {
            // 已经设置过密保问题, 验证问题答案, 如果正确, 保存支付密码
            for(int i=0;i<2;i++) {
                UserProtectQuestionVO curr = currList.get(i);
                UserProtectQuestionVO commit = questions.get(i);
                if (!curr.getQuestionId().equals(commit.getQuestionId()) || !curr.getUserAnswer().equals(commit.getUserAnswer())) {
                    return 1;
                }
            }
            userPayInfoMapper.updateUserPayPassword(userId, payPassword);
        } else {
            // 未设置过密保问题, 全部保存
            for(UserProtectQuestionVO vo : questions) {
                // 写入用户密保问题答案
                ProtectAnswer answer = new ProtectAnswer();
                answer.setQuestionId(vo.getQuestionId());
                answer.setUserId(userId);
                answer.setUserAnswer(vo.getUserAnswer());
                protectAnswerMapper.insert(answer);
            }
            UserPayInfo userPayInfo = new UserPayInfo();
            userPayInfo.setUserId(userId);
            userPayInfo.setPayPwd(payPassword);
            userPayInfoMapper.insert(userPayInfo);
        }
        return 0;
    }

    @Override
    public int updateUserPayPassword(String userId, String oldPwd, String newPwd) {
        UserPayInfo payInfo = getUserPayInfo(userId);
        if (payInfo != null && payInfo.getPayPwd().equals(oldPwd)) {
            if(userPayInfoMapper.updateUserPayPassword(userId, newPwd) > 0){
                return 0;
            } else {
                return 1;
            }
        } else {
            return 2;
        }
    }
}
