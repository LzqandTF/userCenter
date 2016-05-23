package com.yijiawang.web.platform.userCenter.service.inf;

import com.yijiawang.web.platform.userCenter.cache.JedisPoolManager;
import com.yijiawang.web.platform.userCenter.cache.UserCacheNameSpace;
import com.yijiawang.web.platform.userCenter.dao.*;
import com.yijiawang.web.platform.userCenter.po.*;
import com.yijiawang.web.platform.userCenter.type.BalanceChange;
import com.yijiawang.web.platform.userCenter.type.InsureStatus;
import com.yijiawang.web.platform.userCenter.type.PayType;
import com.yijiawang.web.platform.userCenter.type.TradeType;
import com.yijiawang.web.platform.userCenter.vo.UserProtectQuestionVO;
import com.yijiawang.web.platform.userCenter.vo.UserVO;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yijiawang.web.platform.userCenter.service.UserService;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;
import java.util.Map;

@Service("userService")
public class UserServiceImpl implements UserService {

    private org.apache.commons.logging.Log log = LogFactory.getLog(UserServiceImpl.class);

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
    @Autowired
    private JedisPoolManager jedisPoolManager;
	
	
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
            userAccount.setBalance(0);
            userAccount.setFrozenMoney(0);
            userAccountMapper.insert(userAccount);
        } else {
            userAccount.setPassWord(payPassword);
            userAccountMapper.updateUserPayPassword(userId, payPassword);
        }
        return 0;
    }

    @Override
    public int createUserAccount(UserAccount userAccount) {
        return userAccountMapper.insertSelective(userAccount);
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
    public AccountCheck getAccountCheckByTranId(String tran_id) {
        return accountCheckMapper.selectByTranId(tran_id);
    }

    @Override
    public int addAccountCheck(Map<String, String> param) {
        // 写入流水
        log.info("------------addAccountCheck-----------");
        AccountCheck accountCheck = new AccountCheck();
        if (param.get("tran_id") == null) {
            return 1;
        }
        log.info("tran_id == " + param.get("tran_id"));
        accountCheck.setTranId(param.get("tran_id"));
        if (param.get("open_id") == null) {
            return 2;
        }
        log.info("open_id == " + param.get("open_id"));
        WxUserInfo wxUserInfo = wxUserInfoMapper.selectByPrimaryKey(param.get("open_id"));
        if (wxUserInfo == null) {
            return 4;
        }
        accountCheck.setUserId(wxUserInfo.getUserId());
        accountCheck.setOpenId(wxUserInfo.getOpenId());
        if (param.get("title") != null) {
            accountCheck.setTitle(param.get("title"));
        }
        log.info("title == " + param.get("title"));
        if (param.get("trade_type") == null || param.get("trade_amount") == null) {
            return 3;
        }
        log.info("trade_type == " + param.get("trade_type"));
        Integer tradeType = Integer.parseInt(param.get("trade_type"));
        accountCheck.setTradeType(tradeType);
        log.info("trade_amount == " + param.get("trade_amount"));
        Integer amount = Integer.parseInt(param.get("trade_amount"));
        accountCheck.setTradeAmount(amount);
        if (param.get("pay_type") == null) {
            return 5;
        }
        log.info("pay_type == " + param.get("pay_type"));
        accountCheck.setPayType(Integer.parseInt(param.get("pay_type")));
        if (param.get("lot_id") != null) {
            accountCheck.setLotId(param.get("lot_id"));
            log.info("lot_id == " + param.get("lot_id"));
        }
        if (param.get("order_id") != null) {
            accountCheck.setOrderId(param.get("lot_id"));
            log.info("order_id == " + param.get("order_id"));
        }
        if (param.get("order_sn") != null) {
            accountCheck.setOrderSn(param.get("order_sn"));
            log.info("order_sn == " + param.get("order_sn"));
        }
        if (param.get("back_sn") != null) {
            accountCheck.setBackSn(param.get("back_sn"));
            log.info("back_sn == " + param.get("back_sn"));
        }
        log.info("----------- end addAccountCheck ------------");
        return changeBalance(accountCheck);
    }

    /**
     * 余额变化
     * 写入流水,更新账户信息
     * @param accountCheck
     * @return
     */
    private int changeBalance(AccountCheck accountCheck) {
        try {
            String userId = accountCheck.getUserId();
            log.info(" changeBalacne 获取用户id == " + userId);
            Integer amount = accountCheck.getTradeAmount();
            log.info(" changeBalacne amount == " + amount);
            if (accountCheck.getTradeType() == TradeType.TOPUP.value()) {
                // 充值到余额
                log.info(" 充值到余额 ");
                if (userAccountMapper.updateBalance2UserAccount(userId, amount) > 0) {
                    log.info(" 充值到余额, 余额增加完成!");
                    accountCheck.setType(BalanceChange.ADD.value());
                    accountCheck.setTitle("充值");
                    if (accountCheckMapper.insert(accountCheck) > 0) {
                        log.info(" 充值到余额, 余额充值流水写入完成");
                    }
                }
            } else if (accountCheck.getTradeType() == TradeType.ORDER_PAY.value()) {
                // 支付订单
                // 1. 充值余额
                log.info(" 订单支付 ");
                if (userAccountMapper.updateBalance2UserAccount(userId, amount) > 0) {
                    log.info(" 订单支付, 余额增加完成");
                    accountCheck.setType(BalanceChange.ADD.value());
                    accountCheck.setTitle("充值");
                    if (accountCheckMapper.insert(accountCheck) > 0) {
                        log.info(" 订单支付, 余额充值流水写入完成");
                        // 充值成功, 2. 从余额支付订单
                        if (userAccountMapper.updateBalance2UserAccount(userId, -1*amount) > 0) {
                            log.info(" 订单支付, 余额扣除完成");
                            AccountCheck outAccountCheck = new AccountCheck();
                            outAccountCheck.setUserId(accountCheck.getUserId());
                            outAccountCheck.setOpenId(accountCheck.getOpenId());
                            outAccountCheck.setTitle("订单支付");
                            outAccountCheck.setTradeType(accountCheck.getTradeType());
                            outAccountCheck.setTradeAmount(accountCheck.getTradeAmount());
                            outAccountCheck.setType(BalanceChange.SUB.value());
                            outAccountCheck.setPayType(PayType.BALANCE.value());
                            outAccountCheck.setLotId(accountCheck.getLotId());
                            outAccountCheck.setOrderId(accountCheck.getOrderId());
                            if (accountCheckMapper.insert(outAccountCheck) > 0) {
                                log.info(" 订单支付, 余额扣除流水写入完成");
                            }
                        }
                    }

                }

            } else if (accountCheck.getTradeType() == TradeType.INSURE_PAY.value()) {
                // 支付保证金
                // 1. 充值到余额
                log.info(" 支付保证金 ");
                if (userAccountMapper.updateBalance2UserAccount(userId, amount) > 0) {
                    log.info(" 支付保证金, 余额增加完成 !");
                    accountCheck.setType(BalanceChange.ADD.value());
                    accountCheck.setTitle("充值");
                    if (accountCheckMapper.insert(accountCheck) > 0) {
                        log.info(" 支付保证金, 余额充值流水写入完成 !");
                        // 2. 从余额支付保证金
                        userAccountMapper.updateBalance2UserAccount(userId, -1*amount);
                        log.info(" 支付保证金, 余额扣除完成 !");
                        userAccountMapper.updateFrozenMoney2UserAccount(userId, amount);
                        log.info(" 支付保证金, 冻结保证金完成 !");
                        // 插入保证金表
                        insertInsurePriceInfo(accountCheck);
                        log.info(" 支付保证金, 写入保证金表完成");
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
                            log.info(" 支付保证金, 余额消费流水写入完成 !");
                        }
                    }
                }
            } else if (accountCheck.getTradeType() == TradeType.CASH.value()) {
                // 提现
                log.info(" 提现操作 ");
                if (userAccountMapper.updateBalance2UserAccount(userId, -1*amount) > 0) {
                    log.info(" 提现操作 余额扣除完成 !");
                    accountCheck.setType(BalanceChange.SUB.value());
                    accountCheck.setTitle("提现");
                    if (accountCheckMapper.insert(accountCheck) > 0) {
                        log.info(" 提现操作 余额消费流水写入完成 !");
                    }
                }
            } else if (accountCheck.getTradeType() == TradeType.ORDER_WITHDRAW.value()) {
                // 退款
                log.info(" 退款操作 ");
                // 1.退款到余额
                if (userAccountMapper.updateBalance2UserAccount(userId, amount) > 0) {
                    log.info(" 退款操作 买家余额增加完成 !");
                    accountCheck.setType(BalanceChange.ADD.value());
                    accountCheck.setTitle(" 退款 ");
                    if (accountCheckMapper.insert(accountCheck) > 0) {
                        log.info(" 退款操作 退款给买家流水增加写入完成 !");
                        if (accountCheck.getPayType() != PayType.BALANCE.value()) {
                            // 2. 如果非余额付款,从余额扣除
                            if (userAccountMapper.updateBalance2UserAccount(userId, -1*amount) > 0) {
                                log.info(" 退款操作 卖家余额扣除完成 !");
                                AccountCheck outAccountCheck = new AccountCheck();
                                outAccountCheck.setUserId(accountCheck.getUserId());
                                outAccountCheck.setOpenId(accountCheck.getOpenId());
                                outAccountCheck.setTitle(" 退款 ");
                                outAccountCheck.setTradeType(accountCheck.getTradeType());
                                outAccountCheck.setTradeAmount(accountCheck.getTradeAmount());
                                outAccountCheck.setType(BalanceChange.SUB.value());
                                outAccountCheck.setPayType(PayType.BALANCE.value());
                                outAccountCheck.setLotId(accountCheck.getLotId());
                                outAccountCheck.setOrderId(accountCheck.getOrderId());
                                if (accountCheckMapper.insert(outAccountCheck) > 0) {
                                    log.info(" 退款操作 卖家余额扣款流水写入完成 !");
                                }
                            }
                        }
                    }
                }

            } else if (accountCheck.getTradeType() == TradeType.ORDER_FINISH.value()) {
                // 订单完成
                log.info(" 订单完成 ");
                // 修改余额
                if (userAccountMapper.updateBalance2UserAccount(userId, amount) > 0) {
                    log.info(" 订单完成 卖家余额增加完成!");
                    // 添加流水
                    accountCheck.setType(BalanceChange.ADD.value());
                    accountCheck.setTitle("订单完成收款");
                    if (accountCheckMapper.insert(accountCheck) > 0) {
                        log.info(" 订单完成 卖家余额增加流水写入完成 !");
                    }
                }
            } else if (accountCheck.getTradeType() == TradeType.INSURE_WITHDRAW.value()) {
                // 退回保证金
                log.info(" 退回保证金 ");
                // 1.退款到余额
                if (userAccountMapper.updateBalance2UserAccount(userId, amount) > 0) {
                    log.info(" 退回保证金 用户余额增加完成");
                    accountCheck.setType(BalanceChange.ADD.value());
                    accountCheck.setTitle("退回保证金");
                    if (accountCheckMapper.insert(accountCheck) > 0) {
                        log.info(" 退回保证金 用户余额增加流水写入完成 !");
                        updateInsurePriceInfo(accountCheck.getUserId(), accountCheck.getLotId(), InsureStatus.REFUND.value());
                        log.info(" 退回保证金 保证金表状态更新完成");
                        // 2. 如果非余额付款,从余额扣除
                        if (accountCheck.getPayType() != PayType.BALANCE.value()) {
                            log.info("非余额支付的保证金,需要原路退回");
                            if (userAccountMapper.updateBalance2UserAccount(userId, -1*amount) > 0) {
                                log.info(" 退回保证金 用户余额扣除完成!");
                                AccountCheck outAccountCheck = new AccountCheck();
                                outAccountCheck.setUserId(accountCheck.getUserId());
                                outAccountCheck.setOpenId(accountCheck.getOpenId());
                                outAccountCheck.setTitle("退回保证金");
                                outAccountCheck.setTradeType(accountCheck.getTradeType());
                                outAccountCheck.setTradeAmount(accountCheck.getTradeAmount());
                                outAccountCheck.setType(BalanceChange.SUB.value());
                                outAccountCheck.setPayType(PayType.BALANCE.value());
                                outAccountCheck.setLotId(accountCheck.getLotId());
                                if (accountCheckMapper.insert(outAccountCheck) > 0) {
                                    log.info(" 退回保证金 从余额扣除退回的保证金流水写入完成!");
                                }
                            }
                        }
                    }
                }
            } else if (accountCheck.getTradeType() == TradeType.INSURE_PUNISH.value()) {
                // 扣除保证金
                log.info(" 扣除保证金 ");
                // 从冻结保证金中扣除
                if (userAccountMapper.updateFrozenMoney2UserAccount(userId, -1*amount) > 0) {
                    log.info(" 扣除保证金 用户冻结资金扣除完成!");
                    accountCheck.setType(BalanceChange.SUB.value());
                    accountCheck.setTitle("扣除保证金");
                    if (accountCheckMapper.insert(accountCheck) > 0) {
                        log.info(" 扣除保证金 用户扣除保证金流水写入完成!");
                        // 把扣除的保证金加到卖家余额

                        InsurePriceInfo info = insurePriceInfoMapper.selectByTranId(accountCheck.getTranId());
                        if (userAccountMapper.updateBalance2UserAccount(info.getSalerId(), info.getInsurePrice()) > 0) {
                            log.info(" 扣除保证金 给卖家余额增加完成!");
                            // 更新保证金状态
                            updateInsurePriceInfo(accountCheck.getUserId(), accountCheck.getLotId(), InsureStatus.PUNISH.value());
                            log.info(" 扣除保证金 更新保证金表状态完成!");
                            AccountCheck salerAccountCheck = new AccountCheck();
                            salerAccountCheck.setUserId(info.getUserId());
                            salerAccountCheck.setOpenId(info.getOpenId());
                            salerAccountCheck.setTitle("买家保证金退款");
                            salerAccountCheck.setTradeType(TradeType.INSURE_GET.value());
                            salerAccountCheck.setTradeAmount(accountCheck.getTradeAmount());
                            salerAccountCheck.setType(BalanceChange.ADD.value());
                            salerAccountCheck.setPayType(PayType.BALANCE.value());
                            salerAccountCheck.setLotId(accountCheck.getLotId());
                            salerAccountCheck.setOrderId(accountCheck.getOrderId());
                            if (accountCheckMapper.insert(salerAccountCheck) > 0) {
                                log.info(" 扣除保证金 卖家获得保证金流水写入完成!");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
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


    @Override
    public List<AccountCheck> getAccountCheckByUserId(String userId, Long cursor, Integer count) {
        return accountCheckMapper.selectByUserId(userId, cursor, count);
    }

    @Override
    public AccountCheck getAccountById(Long id) {
        return accountCheckMapper.selectByPrimaryKey(id);
    }

    @Override
    public UserVO getUserVOByUserId(String userId) {
        String key = String.format(UserCacheNameSpace.USER_VO, userId);
        Object obj = jedisPoolManager.getObject(key);
        UserVO vo = null;
        if (obj != null && obj instanceof UserVO) {
            vo = (UserVO)obj;
        } else {
            vo = wxUserInfoMapper.selectUserVOByUserId(userId);
            if (vo != null) {
                jedisPoolManager.putObject(key, vo, UserCacheNameSpace.USER_VO_EXPIRE);
            }
        }
        return vo;
    }

    @Override
    public AccountCheck getOrderPayAccountCheck(String orderId) {
        return accountCheckMapper.getOrderPayAccountCheck(orderId);
    }
}
