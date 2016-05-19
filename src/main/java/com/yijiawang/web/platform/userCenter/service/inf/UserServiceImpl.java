package com.yijiawang.web.platform.userCenter.service.inf;

import com.yijiawang.web.platform.userCenter.dao.*;
import com.yijiawang.web.platform.userCenter.po.*;
import com.yijiawang.web.platform.userCenter.type.BalanceChange;
import com.yijiawang.web.platform.userCenter.type.InsureStatus;
import com.yijiawang.web.platform.userCenter.type.PayType;
import com.yijiawang.web.platform.userCenter.type.TradeType;
import com.yijiawang.web.platform.userCenter.vo.UserProtectQuestionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    @Autowired
    private InsurePriceInfoMapper insurePriceInfoMapper;
    @Autowired
    private WxUserInfoMapper wxUserInfoMapper;
	
	
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
        if (param.get("open_id") == null) {
            return 2;
        }
        WxUserInfo wxUserInfo = wxUserInfoMapper.selectByPrimaryKey(param.get("open_id"));
        if (wxUserInfo == null) {
            return 4;
        }
        accountCheck.setUserId(wxUserInfo.getUserId());
        accountCheck.setOpenId(wxUserInfo.getOpenId());
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

        return changeBalance(accountCheck);
    }

    /**
     * 余额变化
     * 写入流水,更新账户信息
     * @param accountCheck
     * @return
     */
    private int changeBalance(AccountCheck accountCheck) {
        String userId = accountCheck.getUserId();
        Integer amount = accountCheck.getTradeAmount();
        if (accountCheck.getTradeType() == TradeType.TOPUP.value()) {
            // 充值到余额
            accountCheck.setType(BalanceChange.ADD.value());
            if (accountCheckMapper.insert(accountCheck) > 0) {
                userAccountMapper.updateBalance2UserAccount(userId, amount);
            }
        } else if (accountCheck.getTradeType() == TradeType.ORDER.value()) {
            // 支付订单
            // 1. 充值余额
            accountCheck.setType(BalanceChange.ADD.value());
            if (accountCheckMapper.insert(accountCheck) > 0) {
                if (userAccountMapper.updateBalance2UserAccount(userId, amount) > 0) {
                    // 充值成功, 2. 从余额支付订单
                    AccountCheck outAccountCheck = new AccountCheck();
                    outAccountCheck.setUserId(accountCheck.getUserId());
                    outAccountCheck.setOpenId(accountCheck.getOpenId());
                    outAccountCheck.setTitle("支付保证金");
                    outAccountCheck.setTradeType(accountCheck.getTradeType());
                    outAccountCheck.setTradeAmount(accountCheck.getTradeAmount());
                    outAccountCheck.setType(BalanceChange.SUB.value());
                    outAccountCheck.setPayType(PayType.BALANCE.value());
                    outAccountCheck.setLotId(accountCheck.getLotId());
                    if (accountCheckMapper.insert(outAccountCheck) > 0) {
                        userAccountMapper.updateBalance2UserAccount(userId, -1*amount);
                    }
                }
            }
        } else if (accountCheck.getTradeType() == TradeType.INSURE_PAY.value()) {
            // 支付保证金
            accountCheck.setType(BalanceChange.ADD.value());
            if (accountCheckMapper.insert(accountCheck) > 0) {
                // 1. 充值到余额
                if (userAccountMapper.updateBalance2UserAccount(userId, amount) > 0) {
                    // 充值成功, 2. 从余额支付保证金
                    accountCheck.setType(BalanceChange.SUB.value());
                    if (accountCheckMapper.insert(accountCheck) > 0) {
                        userAccountMapper.updateBalance2UserAccount(userId, -1*amount);
                        userAccountMapper.updateFrozenMoney2UserAccount(userId, amount);
                        // 插入保证金表
                        insertInsurePriceInfo(accountCheck);
                    }
                }
            }
        } else if (accountCheck.getTradeType() == TradeType.CASH.value()) {
            // 提现
            accountCheck.setType(BalanceChange.SUB.value());
            if (accountCheckMapper.insert(accountCheck) > 0) {
                userAccountMapper.updateBalance2UserAccount(userId, -1*amount);
            }
        } else if (accountCheck.getTradeType() == TradeType.REFUND.value()) {
            // 退款
            accountCheck.setType(BalanceChange.ADD.value());
            if (accountCheckMapper.insert(accountCheck) > 0) {
                // 1.退款到余额
                if (userAccountMapper.updateBalance2UserAccount(userId, amount) > 0) {
                    if (accountCheck.getPayType() != PayType.BALANCE.value()) {
                        // 2. 如果非余额付款,从余额扣除
                        accountCheck.setType(BalanceChange.SUB.value());
                        if (accountCheckMapper.insert(accountCheck) > 0) {
                            userAccountMapper.updateBalance2UserAccount(userId, -1*amount);
                        }
                    }

                }
            }
        } else if (accountCheck.getTradeType() == TradeType.INSURE_WITHDRAW.value()) {
            // 退回保证金
            accountCheck.setType(BalanceChange.ADD.value());
            if (accountCheckMapper.insert(accountCheck) > 0) {
                // 1.退款到余额
                if (userAccountMapper.updateBalance2UserAccount(userId, amount) > 0) {
                    if (accountCheck.getPayType() != PayType.BALANCE.value()) {
                        // 2. 如果非余额付款,从余额扣除
                        accountCheck.setType(BalanceChange.SUB.value());
                        if (accountCheckMapper.insert(accountCheck) > 0) {
                            userAccountMapper.updateBalance2UserAccount(userId, -1*amount);
                        }
                    }
                    updateInsurePriceInfo(accountCheck.getUserId(), accountCheck.getLotId(), InsureStatus.REFUND.value());
                }
            }
        } else if (accountCheck.getTradeType() == TradeType.INSURE_PUNISH.value()) {
            // 扣除保证金
            accountCheck.setType(BalanceChange.SUB.value());
            if (accountCheckMapper.insert(accountCheck) > 0) {
                // 从冻结保证金中扣除
                userAccountMapper.updateFrozenMoney2UserAccount(userId, -1*amount);
                // 把扣除的保证金加到卖家余额
                InsurePriceInfo info = insurePriceInfoMapper.selectByTranId(accountCheck.getTranId());
                AccountCheck salerAccountCheck = new AccountCheck();
                salerAccountCheck.setUserId(info.getUserId());
                salerAccountCheck.setOpenId(info.getOpenId());
                salerAccountCheck.setTitle("保证金");
                salerAccountCheck.setTradeType(TradeType.INSURE_GET.value());
                salerAccountCheck.setTradeAmount(accountCheck.getTradeAmount());
                salerAccountCheck.setType(BalanceChange.ADD.value());
                salerAccountCheck.setPayType(PayType.BALANCE.value());
                salerAccountCheck.setLotId(accountCheck.getLotId());
                salerAccountCheck.setOrderId(accountCheck.getOrderId());
                accountCheckMapper.insert(salerAccountCheck);
                // 卖家增加余额
                userAccountMapper.updateBalance2UserAccount(salerAccountCheck.getUserId(), salerAccountCheck.getTradeAmount());
                // 更新保证金状态
                updateInsurePriceInfo(accountCheck.getUserId(), accountCheck.getLotId(), InsureStatus.PUNISH.value());
            }
        }
        return 0;
    }

    private int insertInsurePriceInfo(AccountCheck accountCheck) {
        UserInfo salerInfo = insurePriceInfoMapper.getLotSalerInfo(accountCheck.getLotId());
        InsurePriceInfo info = new InsurePriceInfo();
        info.setTranId(accountCheck.getTranId());
        info.setSalerId(salerInfo.getUserId());
        info.setOpenId(accountCheck.getOpenId());
        info.setLotId(accountCheck.getLotId());
        info.setOpenId(accountCheck.getOpenId());
        info.setUserId(accountCheck.getUserId());
        info.setInsurePrice(accountCheck.getTradeAmount());
        info.setStatus(InsureStatus.FROZEN.value());
        info.setPayType(accountCheck.getPayType());
        return insurePriceInfoMapper.insert(info);
    }

    private int updateInsurePriceInfo(String userId, String lotId, Byte status) {
        return insurePriceInfoMapper.updateInsurePriceInfoStatus(userId, lotId, status);
    }

}
