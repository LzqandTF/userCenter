package com.yijiawang.web.platform.userCenter.service.inf;

import com.yijiawang.web.platform.userCenter.dao.*;
import com.yijiawang.web.platform.userCenter.po.AccountCheck;
import com.yijiawang.web.platform.userCenter.po.ProtectAnswer;
import com.yijiawang.web.platform.userCenter.po.UserAccount;
import com.yijiawang.web.platform.userCenter.vo.UserProtectQuestionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yijiawang.web.platform.userCenter.po.UserInfo;
import com.yijiawang.web.platform.userCenter.service.UserService;

import java.util.List;
import java.util.Map;

@Service("userService")
public class UserServiceImpl implements UserService{
	@Autowired
	private UserInfoMapper userInfoMapper;
    @Autowired
    private ProtectQuestionMapper protectQuestionMapper;
    @Autowired
    private ProtectAnswerMapper protectAnswerMapper;
    @Autowired
    private UserAccountMapper userAccountMapper;
    @Autowired
    private AccountCheckMapper accountCheckMapper;
	
	
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
    public UserAccount getUserPayInfo(String userId) {
        return userAccountMapper.selectByUserId(userId);
    }

    @Override
    public List<UserProtectQuestionVO> getProtectQuestion(String userId) {
        UserAccount payInfo = getUserPayInfo(userId);
        if (payInfo != null && payInfo.getPassWord() != null && payInfo.getPassWord().length() > 0) {
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
        }
        UserAccount userAccount = getUserPayInfo(userId);
        if (userAccount == null) {
            userAccount = new UserAccount();
            userAccount.setUserId(userId);
            userAccount.setPassWord(payPassword);
            userAccount.setStatus(0);
            userAccountMapper.insert(userAccount);
        } else {
            userAccount.setPassWord(payPassword);
            userAccountMapper.updateUserPayPassword(userId, payPassword);
        }
        return 0;
    }

    @Override
    public int updateUserPayPassword(String userId, String oldPwd, String newPwd) {
        UserAccount payInfo = getUserPayInfo(userId);
        if (payInfo != null && payInfo.getPassWord().equals(oldPwd)) {
            if(userAccountMapper.updateUserPayPassword(userId, newPwd) > 0){
                return 0;
            } else {
                return 1;
            }
        } else {
            return 2;
        }
    }

    @Override
    public int addAccountCheck(Map<String, String> param) {
        // 写入流水
        AccountCheck accountCheck = new AccountCheck();
        if (param.get("tran_id") == null) {
            return 1;
        }
        accountCheck.setTranId(param.get("tran_id"));
        if (param.get("user_id") == null || param.get("open_id") == null) {
            return 2;
        }
        accountCheck.setUserId(param.get("user_id"));
        accountCheck.setOpenId(param.get("open_id"));
        if (param.get("title") != null) {
            accountCheck.setTitle(param.get("title"));
        }
        if (param.get("trade_type") == null || param.get("trade_amount") == null) {
            return 3;
        }
        Integer tradeType = Integer.parseInt(param.get("trade_type"));
        accountCheck.setTradeType(tradeType);
        Integer amount = Integer.parseInt(param.get("trade_amount"));
        accountCheck.setTradeAmount(amount);
        if (param.get("type") == null) {
            return 4;
        }
        Integer type = Integer.parseInt(param.get("type"));
        accountCheck.setType(type);
        if (param.get("pay_type") == null) {
            return 5;
        }
        accountCheck.setPayType(Integer.parseInt(param.get("pay_type")));
        if (param.get("lot_id") != null) {
            accountCheck.setLotId(param.get("lot_id"));
        }
        if (param.get("order_id") != null) {
            accountCheck.setOrderId(param.get("lot_id"));
        }
        if (accountCheckMapper.insert(accountCheck) > 0) {
            // 更新用户账户余额
            UserAccount userAccount = getUserPayInfo(param.get("user_id"));
            if (type == 0) {
                // 从余额支出
                userAccountMapper.updateBalance2UserAccount(param.get("user_id"), amount*-1);
                if (tradeType == 2) {
                    // 支付保证金
                    userAccountMapper.updateFrozenMoney2UserAccount(param.get("user_id"), amount);
                }
            } else if (type == 1) {
                // 余额收入
                userAccountMapper.updateBalance2UserAccount(param.get("user_id"), amount);
                if (tradeType == 2) {
                    // 退还保证金
                    userAccountMapper.updateFrozenMoney2UserAccount(param.get("user_id"), amount*-1);
                }
            }
        }
        return 6;
    }
}
