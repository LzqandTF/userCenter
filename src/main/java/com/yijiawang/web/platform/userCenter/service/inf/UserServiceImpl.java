package com.yijiawang.web.platform.userCenter.service.inf;

import com.yijiawang.web.platform.userCenter.cache.JedisPoolManager;
import com.yijiawang.web.platform.userCenter.cache.UserCacheNameSpace;
import com.yijiawang.web.platform.userCenter.dao.*;
import com.yijiawang.web.platform.userCenter.param.AccountCheckParam;
import com.yijiawang.web.platform.userCenter.po.*;
import com.yijiawang.web.platform.userCenter.service.UserAccountLogService;
import com.yijiawang.web.platform.userCenter.type.*;
import com.yijiawang.web.platform.userCenter.vo.UserProtectQuestionVO;
import com.yijiawang.web.platform.userCenter.vo.UserVO;
import com.yijiawang.web.platform.userCenter.vo.XUserVO;

import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yijiawang.web.platform.userCenter.service.UserService;

import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;

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
    @Autowired
    private UserAccountLogService userAccountLogService;
    @Autowired
    private ApplyVipMapper applyVipMapper;
    @Autowired
    private UserStatusMapper userStatusMapper;
		
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
        List<UserProtectQuestionVO> list = protectQuestionMapper.userProtectQuestion(userId);
        UserAccount userAccount = userAccountMapper.selectByUserId(userId);
        if (userAccount != null && userAccount.getPassWord() != null) {
            List<UserProtectQuestionVO> answerList = new ArrayList<>();
            for (UserProtectQuestionVO vo : list) {
                if (vo.getUserAnswer() != null && vo.getUserAnswer().length() > 0) {
                    answerList.add(vo);
                }
            }
            return answerList;
        } else {
            return list;
        }

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
    public long addAccountCheck(Map<String, String> param) {
        long result = 0;
        List<String> logObject = new ArrayList<>();
        try {
            // 写入流水
            logObject.add("--------------------------------------");
            logObject.add("********** [addAccountCheck] *********");
            logObject.add("********** 传入参数 **********");
            logObject.add(param.toString());
            AccountCheck accountCheck = new AccountCheck();
            if (param.get("tran_id") != null) {
                logObject.add("tran_id == " + param.get("tran_id"));
                accountCheck.setTranId(param.get("tran_id"));
            }
            if (param.get("open_id") == null) {
                result = -2;
            }
            logObject.add("open_id == " + param.get("open_id"));
            WxUserInfo wxUserInfo = wxUserInfoMapper.selectByPrimaryKey(param.get("open_id"));
            if (wxUserInfo == null) {
                result = -4;
            }
            accountCheck.setUserId(wxUserInfo.getUserId());
            accountCheck.setOpenId(wxUserInfo.getOpenId());
            if (param.get("title") != null) {
                accountCheck.setTitle(param.get("title"));
            }
            logObject.add("title == " + param.get("title"));
            if (param.get("trade_type") == null || param.get("trade_amount") == null) {
                result = -3;
            }
            logObject.add("trade_type == " + param.get("trade_type"));
            Integer tradeType = Integer.parseInt(param.get("trade_type"));
            accountCheck.setTradeType(tradeType);
            logObject.add("trade_amount == " + param.get("trade_amount"));
            Integer amount = Integer.parseInt(param.get("trade_amount"));
            accountCheck.setTradeAmount(amount);
            if (param.get("pay_type") == null) {
                result = -5;
            }
            logObject.add("pay_type == " + param.get("pay_type"));
            accountCheck.setPayType(Integer.parseInt(param.get("pay_type")));
            if (param.get("lot_id") != null) {
                accountCheck.setLotId(param.get("lot_id"));
                logObject.add("lot_id == " + param.get("lot_id"));
            }
            if (param.get("order_id") != null) {
                accountCheck.setOrderId(param.get("order_id"));
                logObject.add("order_id == " + param.get("order_id"));
            }
            if (param.get("order_sn") != null) {
                accountCheck.setOrderSn(param.get("order_sn"));
                logObject.add("order_sn == " + param.get("order_sn"));
            }
            if (param.get("back_sn") != null) {
                accountCheck.setBackSn(param.get("back_sn"));
                logObject.add("back_sn == " + param.get("back_sn"));
            }
            logObject.add("*********** [end addAccountCheck] result=["+result+"]**********");
            // 判断该笔流水是否已经处理过,规则, trand_id + trade_type + type 为唯一键值
            AccountCheck accountCheckChecker = accountCheckMapper.queryAccountCheck(accountCheck);
            if (result == 0) {
                if (accountCheckChecker != null) {
                    logObject.add("*********** 该比流水已经处理过 ID= "+ accountCheckChecker.getId());
                    result = accountCheckChecker.getId();
                    userAccountLogService.asyncLoggerUserAccount(logObject);
                } else {
                    userAccountLogService.asyncLoggerUserAccount(logObject);
                    result = changeBalance(accountCheck);
                }
            }
        } catch (Exception e) {
            logObject.add(e.getMessage());
            userAccountLogService.asyncLoggerUserAccount(logObject);
            result = -1;
        }
        return result;
    }

    /**
     * 余额变化
     * 写入流水,更新账户信息
     * @param accountCheck
     * @return 0-所有操作成功  2-余额修改失败   3-流水写入失败   1-系统异常
     */
    private long changeBalance(AccountCheck accountCheck) {
        List<String> logObject = new ArrayList<>();
        logObject.add("*********** 开始流水处理操作 *****************");
        long result = 0;
        try {
            String userId = accountCheck.getUserId();
            Integer amount = accountCheck.getTradeAmount();
            logObject.add(" 处理流水前 ... 用户:"+userId+" 账户余额为: "+ userAccountMapper.selectByUserId(userId).getBalance());
            if (accountCheck.getTradeType() == TradeType.TOPUP.value()) {
                // 充值到余额, 充值系统生成一笔提交给微信的订单号,该订单号跟拍品订单无关
                logObject.add(" 充值到余额 ");
                if (userAccountMapper.updateBalance2UserAccount(userId, amount) > 0) {
                    logObject.add(" 充值到余额 余额增加完成!");
                    accountCheck.setType(BalanceChange.ADD.value());
                    accountCheck.setTitle("充值");
                    accountCheck.setResultBalance(userAccountMapper.selectByUserId(userId).getBalance());
                    if (accountCheckMapper.insert(accountCheck) > 0) {
                        // un_index : trand_id + 0 + 1
                        result = accountCheck.getId();
                        logObject.add(" 充值到余额, 余额充值流水写入完成");
                    } else {
                        logObject.add(" 充值到余额, 余额充值流水写入失败");
                        result = -3;
                    }
                } else {
                    logObject.add(" 充值到余额, 余额增加失败!");
                    result = -2;
                }
            } else if (accountCheck.getTradeType() == TradeType.ORDER_PAY.value()) {
                // 支付订单
                // 1. 充值余额
                logObject.add(" 订单支付 ");
                if (accountCheck.getPayType().intValue() != PayType.BALANCE.value()) {
                    // 使用第三方支付,先进行充值
                    logObject.add(" 订单支付 [非余额支付]");
                    if (userAccountMapper.updateBalance2UserAccount(userId, amount) > 0) {
                        logObject.add(" 订单支付 [非余额支付] 余额增加完成");
                        accountCheck.setType(BalanceChange.ADD.value());
                        accountCheck.setTitle("充值");
                        accountCheck.setResultBalance(userAccountMapper.selectByUserId(userId).getBalance());
                        if (accountCheckMapper.insert(accountCheck) > 0) {
                            // un_index : trand_id + 1 + 1
                            result = accountCheck.getId();
                            logObject.add(" 订单支付, [非余额] 余额充值流水写入完成");
                        } else {
                            logObject.add(" 订单支付, [非余额] 余额充值流水写入失败");
                            result = -3;
                        }
                    } else {
                        logObject.add(" 订单支付, [非余额] 余额增加失败");
                        result = -2;
                    }
                }
                if (result >= 0) {
                    // 如果以上步骤操作成功
                    logObject.add(" 订单支付 开始从余额支付订单 ");
                    if (userAccountMapper.updateBalance2UserAccount(userId, -1*amount) > 0) {
                        logObject.add(" 订单支付, [余额] 余额扣除完成");
                        AccountCheck outAccountCheck = new AccountCheck();
                        outAccountCheck.setUserId(accountCheck.getUserId());
                        outAccountCheck.setOpenId(accountCheck.getOpenId());
                        outAccountCheck.setTranId(accountCheck.getTranId());
                        outAccountCheck.setTitle("订单支付");
                        outAccountCheck.setResultBalance(userAccountMapper.selectByUserId(userId).getBalance());
                        outAccountCheck.setTradeType(accountCheck.getTradeType());
                        outAccountCheck.setTradeAmount(accountCheck.getTradeAmount());
                        outAccountCheck.setType(BalanceChange.SUB.value());
                        outAccountCheck.setPayType(accountCheck.getPayType());
                        outAccountCheck.setLotId(accountCheck.getLotId());
                        outAccountCheck.setOrderId(accountCheck.getOrderId());
                        if (accountCheckMapper.insert(outAccountCheck) > 0) {
                            // un_index : trand_id + 1 + 0
                            result = outAccountCheck.getId();
                            logObject.add(" 订单支付, [余额] 余额扣除流水写入完成");
                        } else {
                            logObject.add(" 订单支付, [余额] 余额扣除流水写入失败");
                            result = -3;
                        }
                    } else {
                        logObject.add(" 订单支付, [余额] 余额扣除失败");
                        result = -2;
                    }
                } else {
                    logObject.add(" 订单支付 余额充值步骤失败");
                }

            } else if (accountCheck.getTradeType() == TradeType.INSURE_PAY.value()) {
                // 支付保证金
                // 1. 充值到余额
                logObject.add(" 支付保证金 ");
                if (accountCheck.getPayType().intValue() != PayType.BALANCE.value()) {
                    // 第三方支付,先充值到余额
                    logObject.add(" 支付保证金, [非余额支付] ");
                    if (userAccountMapper.updateBalance2UserAccount(userId, amount) > 0) {
                        logObject.add(" 支付保证金, [非余额支付] 余额增加完成");
                        accountCheck.setType(BalanceChange.ADD.value());
                        accountCheck.setTitle("充值");
                        accountCheck.setResultBalance(userAccountMapper.selectByUserId(userId).getBalance());
                        if (accountCheckMapper.insert(accountCheck) > 0) {
                            // un_index : tran_id + 2 + 1
                            result = accountCheck.getId();
                            logObject.add(" 支付保证金, [非余额支付] 余额充值流水写入完成");
                        } else {
                            logObject.add(" 支付保证金, [非余额支付] 余额充值流水写入失败");
                            result = -3;
                        }
                    } else {
                        logObject.add(" 支付保证金, [非余额支付] 余额增加失败");
                        result = -2;
                    }
                }
                if (result >= 0) {
                    // 2. 从余额支付保证金
                    logObject.add(" 支付保证金, 开始从余额支付保证金 ");
                    if (userAccountMapper.updateBalance2UserAccount(userId, -1*amount)>0) {
                        logObject.add(" 支付保证金, 余额扣除完成");
                    } else {
                        logObject.add(" 支付保证金, 余额扣除失败");
                        result = -2;
                    }
                    // 插入保证金信息表
                    if (insertInsurePriceInfo(accountCheck) > 0) {
                        logObject.add(" 支付保证金, 写入保证金表完成");
                    } else {
                        logObject.add(" 支付保证金, 写入保证金表失败");
                        result = -4;
                    }
                    AccountCheck outAccountCheck = new AccountCheck();
                    outAccountCheck.setUserId(accountCheck.getUserId());
                    outAccountCheck.setOpenId(accountCheck.getOpenId());
                    outAccountCheck.setTranId(accountCheck.getTranId());
                    outAccountCheck.setTitle("支付保证金");
                    outAccountCheck.setResultBalance(userAccountMapper.selectByUserId(userId).getBalance());
                    outAccountCheck.setTradeType(accountCheck.getTradeType());
                    outAccountCheck.setTradeAmount(accountCheck.getTradeAmount());
                    outAccountCheck.setType(BalanceChange.SUB.value());
                    outAccountCheck.setPayType(PayType.BALANCE.value());
                    outAccountCheck.setLotId(accountCheck.getLotId());
                    if (accountCheckMapper.insert(outAccountCheck) > 0) {
                        // un_index : tranId + 2 + 0
                        result = outAccountCheck.getId();
                        logObject.add(" 支付保证金, 余额消费流水写入完成");
                    } else {
                        logObject.add(" 支付保证金, 余额消费流水写入失败");
                        result = -3;
                    }
                } else {
                    logObject.add(" 支付保证金, 充值到余额失败");
                }
            } else if (accountCheck.getTradeType() == TradeType.CASH.value()) {
                // 提现
                logObject.add(" 提现操作 ");
                if (userAccountMapper.updateBalance2UserAccount(userId, -1*amount) > 0) {
                    logObject.add(" 提现操作 余额扣除完成 !");
                    accountCheck.setType(BalanceChange.SUB.value());
                    accountCheck.setTitle("提现");
                    accountCheck.setStatus(AccountCheckStatus.GETCASH.value());
                    accountCheck.setResultBalance(userAccountMapper.selectByUserId(userId).getBalance());
                    if (accountCheckMapper.insert(accountCheck) > 0) {
                        // un_index : tran_id + 3 + 0
                        result = accountCheck.getId();
                        logObject.add(" 提现操作 余额消费流水写入完成");
                    } else {
                        logObject.add(" 提现操作 余额消费流水写入失败");
                        result = -3;
                    }
                } else {
                    logObject.add(" 提现操作 余额扣除失败");
                    result = -2;
                }
            } else if (accountCheck.getTradeType() == TradeType.ORDER_WITHDRAW.value()) {
                // 退款
                logObject.add(" 退款操作 ");
                // 如果是7天无理由退款,订单状态为Finish,先解冻保证金
                AccountCheck frozenAccountCheck = accountCheckMapper.selectFrozenAccountCheck(accountCheck.getOrderId());
                if (frozenAccountCheck != null) {
                    logObject.add(" 退款操作 7天无理由退款 ");
                    // 有冻结金额,先更新卖家冻结金额
                    if (userAccountMapper.updateFrozenMoney2UserAccount(frozenAccountCheck.getUserId(),-1*frozenAccountCheck.getTradeAmount()) > 0) {
                        logObject.add(" 退款操作 7天无理由退款 更新卖家冻结余额完成");
                        // 新增一条解冻的流水
                        AccountCheck unFrozenCheck = new AccountCheck();
                        unFrozenCheck.setUserId(frozenAccountCheck.getUserId());
                        unFrozenCheck.setOpenId(frozenAccountCheck.getOpenId());
                        unFrozenCheck.setTitle("7天无理由退款");
                        unFrozenCheck.setResultBalance(userAccountMapper.selectByUserId(frozenAccountCheck.getUserId()).getBalance());
                        unFrozenCheck.setTradeType(TradeType.ORDER_WITHDRAW.value());
                        unFrozenCheck.setTradeAmount(frozenAccountCheck.getTradeAmount());
                        unFrozenCheck.setType(BalanceChange.SUB.value());
                        unFrozenCheck.setPayType(PayType.BALANCE.value());
                        unFrozenCheck.setLotId(frozenAccountCheck.getLotId());
                        unFrozenCheck.setOrderId(frozenAccountCheck.getOrderId());
                        unFrozenCheck.setStatus(AccountCheckStatus.UNFREEZE.value());
                        if (accountCheckMapper.insert(unFrozenCheck) > 0) {
                            result = unFrozenCheck.getId();
                            logObject.add(" 退款操作 7天无理由退款 卖家冻结资金退款解冻流水写入完成");
                        } else {
                            logObject.add(" 退款操作 7天无理由退款 卖家冻结资金退款解冻流水写入失败");
                            result = -5;
                        }
                    } else {
                        logObject.add(" 退款操作 7天无理由退款 更新卖家冻结余额失败");
                        result = -6;
                    }
                }
                if (result >= 0) {
                    // 1.退款到余额
                    if (userAccountMapper.updateBalance2UserAccount(userId, amount) > 0) {
                        logObject.add(" 退款操作 买家余额增加完成");
                        accountCheck.setType(BalanceChange.ADD.value());
                        accountCheck.setTitle(" 退款 ");
                        accountCheck.setResultBalance(userAccountMapper.selectByUserId(userId).getBalance());
                        if (accountCheckMapper.insert(accountCheck) > 0) {
                            // un_index : tran_id + 4 + 1
                            result = accountCheck.getId();
                            logObject.add(" 退款操作 退款给买家流水增加写入完成");
                            if (accountCheck.getPayType() != PayType.BALANCE.value()) {
                                // 2. 如果非余额付款,从余额扣除
                                if (userAccountMapper.updateBalance2UserAccount(userId, -1*amount) > 0) {
                                    logObject.add(" 退款操作 卖家余额扣除完成 !");
                                    AccountCheck outAccountCheck = new AccountCheck();
                                    outAccountCheck.setUserId(accountCheck.getUserId());
                                    outAccountCheck.setOpenId(accountCheck.getOpenId());
                                    outAccountCheck.setTitle(" 退款 原支付方式退回");
                                    outAccountCheck.setResultBalance(userAccountMapper.selectByUserId(userId).getBalance());
                                    outAccountCheck.setTradeType(accountCheck.getTradeType());
                                    outAccountCheck.setTradeAmount(accountCheck.getTradeAmount());
                                    outAccountCheck.setType(BalanceChange.SUB.value());
                                    outAccountCheck.setPayType(PayType.BALANCE.value());
                                    outAccountCheck.setLotId(accountCheck.getLotId());
                                    outAccountCheck.setOrderId(accountCheck.getOrderId());
                                    if (accountCheckMapper.insert(outAccountCheck) > 0) {
                                        // un_index : tran_id + 4 + 0
                                        result = outAccountCheck.getId();
                                        logObject.add(" 退款操作 卖家余额扣款流水写入完成");
                                    } else {
                                        logObject.add(" 退款操作 卖家余额扣款流水写入失败");
                                        result = -3;
                                    }
                                } else {
                                    logObject.add(" 退款操作 卖家余额扣除失败");
                                    result = -2;
                                }
                            }
                        } else {
                            logObject.add(" 退款操作 退款给买家流水增加写入失败");
                            result = -3;
                        }
                    } else {
                        logObject.add(" 退款操作 买家余额增加失败");
                        result = -2;
                    }
                }
            } else if (accountCheck.getTradeType() == TradeType.ORDER_FINISH.value()) {
                // 订单完成
                logObject.add(" 订单完成 ");
                // 修改余额
                if (userAccountMapper.updateBalance2UserAccount(userId, amount) > 0) {
                    logObject.add(" 订单完成 卖家余额增加完成");
                    // 查看是否已经有冻结流水
                    AccountCheck frozenAccountCheck = accountCheckMapper.selectFrozenAccountCheck(accountCheck.getOrderId());
                    if (frozenAccountCheck != null) {
                        // 已经有冻结的流水,先更新冻结余额
                        if (userAccountMapper.updateFrozenMoney2UserAccount(accountCheck.getUserId(),-1*accountCheck.getTradeAmount()) > 0) {
                            logObject.add(" 订单完成 更新卖家冻结余额完成");
                            // 新增一条解冻的流水
                            AccountCheck unFrozenCheck = new AccountCheck();
                            unFrozenCheck.setUserId(frozenAccountCheck.getUserId());
                            unFrozenCheck.setOpenId(frozenAccountCheck.getOpenId());
                            unFrozenCheck.setTitle("货款解冻");
                            unFrozenCheck.setResultBalance(userAccountMapper.selectByUserId(userId).getBalance());
                            unFrozenCheck.setTradeType(TradeType.ORDER_FINISH.value());
                            unFrozenCheck.setTradeAmount(frozenAccountCheck.getTradeAmount());
                            unFrozenCheck.setType(BalanceChange.ADD.value());
                            unFrozenCheck.setPayType(PayType.BALANCE.value());
                            unFrozenCheck.setLotId(frozenAccountCheck.getLotId());
                            unFrozenCheck.setOrderId(frozenAccountCheck.getOrderId());
                            unFrozenCheck.setStatus(AccountCheckStatus.UNFREEZE.value());
                            if (accountCheckMapper.insert(unFrozenCheck) > 0) {
                                result = unFrozenCheck.getId();
                                logObject.add(" 订单完成 卖家余额增加流水更新完成");
                            } else {
                                logObject.add(" 订单完成 卖家余额增加流水更新失败");
                                result = -3;
                            }
                        } else {
                            logObject.add(" 订单完成 更新卖家冻结余额失败");
                            result = -2;
                        }
                    } else {
                        // 添加流水
                        accountCheck.setType(BalanceChange.ADD.value());
                        accountCheck.setTitle("货款到账");
                        accountCheck.setResultBalance(userAccountMapper.selectByUserId(userId).getBalance());
                        if (accountCheckMapper.insert(accountCheck) > 0) {
                            // un_index : tran_id(FIN前缀) + 8 + 1
                            result = accountCheck.getId();
                            logObject.add(" 订单完成 卖家余额增加流水写入完成");
                        } else {
                            logObject.add(" 订单完成 卖家余额增加流水写入失败");
                            result = -3;
                        }
                    }
                } else {
                    logObject.add(" 订单完成 卖家余额增加失败");
                    result = -2;
                }
            } else if (accountCheck.getTradeType() == TradeType.ORDER_FROZEN.value()) {
                // 订单确认收货,需要冻结货款
                logObject.add(" 订单货款冻结 ");
                // 修改冻结余额
                if (userAccountMapper.updateFrozenMoney2UserAccount(userId, amount) > 0) {
                    accountCheck.setStatus(AccountCheckStatus.FROZEN.value());
                    accountCheck.setType(BalanceChange.ADD.value());
                    accountCheck.setTitle("货款冻结中");
                    accountCheck.setResultFrozen(userAccountMapper.selectByUserId(userId).getFrozenMoney());
                    if (accountCheckMapper.insert(accountCheck) > 0) {
                        // un_index : tran_id + 9 + 1
                        result = accountCheck.getId();
                        logObject.add(" 订单货款冻结 卖家冻结余额流水写入完成");
                    } else {
                        logObject.add(" 订单货款冻结 卖家冻结余额流水写入失败");
                        result = -3;
                    }
                } else {
                    logObject.add(" 订单货款冻结 卖家冻结余额增加失败");
                    result = -2;
                }
            } else if (accountCheck.getTradeType() == TradeType.INSURE_WITHDRAW.value()) {
                // 退回保证金
                logObject.add(" 退回保证金 ");
                // 1.退款到余额
                if (userAccountMapper.updateBalance2UserAccount(userId, amount) > 0) {
                    logObject.add(" 退回保证金 用户余额增加完成");
                    accountCheck.setType(BalanceChange.ADD.value());
                    accountCheck.setTitle("退回保证金");
                    accountCheck.setResultBalance(userAccountMapper.selectByUserId(userId).getBalance());
                    if (accountCheckMapper.insert(accountCheck) > 0) {
                        // un_index : tran_id + 5 +1
                        result = accountCheck.getId();
                        logObject.add(" 退回保证金 用户余额增加流水写入完成");
                        if (updateInsurePriceInfo(accountCheck.getUserId(), accountCheck.getLotId(), InsureStatus.REFUND.value()) > 0) {
                            logObject.add(" 退回保证金 保证金表状态更新完成");
                        } else {
                            logObject.add(" 退回保证金 保证金表状态更新失败");
                            result = -4;
                        }
                        // 2. 如果非余额付款,从余额扣除
                        if (accountCheck.getPayType() != PayType.BALANCE.value()) {
                            logObject.add("非余额支付的保证金,需要原路退回");
                            if (userAccountMapper.updateBalance2UserAccount(userId, -1*amount) > 0) {
                                logObject.add(" 退回保证金 用户余额扣除完成");
                                AccountCheck outAccountCheck = new AccountCheck();
                                outAccountCheck.setUserId(accountCheck.getUserId());
                                outAccountCheck.setOpenId(accountCheck.getOpenId());
                                outAccountCheck.setTranId(accountCheck.getTranId());
                                outAccountCheck.setResultBalance(userAccountMapper.selectByUserId(userId).getBalance());
                                outAccountCheck.setTitle("退回保证金 原支付方式退回");
                                outAccountCheck.setTradeType(accountCheck.getTradeType());
                                outAccountCheck.setTradeAmount(accountCheck.getTradeAmount());
                                outAccountCheck.setType(BalanceChange.SUB.value());
                                outAccountCheck.setPayType(PayType.BALANCE.value());
                                outAccountCheck.setLotId(accountCheck.getLotId());
                                if (accountCheckMapper.insert(outAccountCheck) > 0) {
                                    // un_index : tran_id + 5 + 0
                                    result = outAccountCheck.getId();
                                    logObject.add(" 退回保证金 从余额扣除退回的保证金流水写入完成");
                                } else {
                                    logObject.add(" 退回保证金 从余额扣除退回的保证金流水写入失败");
                                    result = -3;
                                }
                            } else {
                                logObject.add(" 退回保证金 用户余额扣除失败");
                                result = -2;
                            }
                        }
                    } else {
                        logObject.add(" 退回保证金 用户余额增加流水写入失败");
                        result = -3;
                    }
                } else {
                    logObject.add(" 退回保证金 用户余额增加失败");
                    result = -2;
                }
            } else if (accountCheck.getTradeType() == TradeType.INSURE_PUNISH.value()) {
                // 扣除保证金
                logObject.add(" 扣除保证金 ");
                // 把扣除的保证金加到卖家余额
                InsurePriceInfo info = insurePriceInfoMapper.selectByTranId(accountCheck.getTranId());
                if (info != null) {
                    if (userAccountMapper.updateBalance2UserAccount(info.getSalerId(), info.getInsurePrice()) > 0) {
                        logObject.add(" 扣除保证金 给卖家余额增加完成");
                        // 更新保证金状态
                        if (updateInsurePriceInfo(accountCheck.getUserId(), accountCheck.getLotId(), InsureStatus.PUNISH.value()) > 0) {
                            logObject.add(" 扣除保证金 更新保证金表状态完成");
                        } else {
                            logObject.add(" 扣除保证金 更新保证金表状态失败");
                            result = -4;
                        }
                        WxUserInfo salerWxUserInfo = wxUserInfoMapper.selectWxUserInfoByUserId(info.getSalerId());
                        AccountCheck salerAccountCheck = new AccountCheck();
                        salerAccountCheck.setUserId(salerWxUserInfo.getUserId());
                        salerAccountCheck.setOpenId(salerWxUserInfo.getOpenId());
                        salerAccountCheck.setTitle("买家保证金退款");
                        salerAccountCheck.setResultBalance(userAccountMapper.selectByUserId(salerWxUserInfo.getUserId()).getBalance());
                        salerAccountCheck.setTradeType(TradeType.INSURE_GET.value());
                        salerAccountCheck.setTradeAmount(accountCheck.getTradeAmount());
                        salerAccountCheck.setType(BalanceChange.ADD.value());
                        salerAccountCheck.setPayType(PayType.BALANCE.value());
                        salerAccountCheck.setLotId(accountCheck.getLotId());
                        salerAccountCheck.setOrderId(accountCheck.getOrderId());
                        if (accountCheckMapper.insert(salerAccountCheck) > 0) {
                            result = salerAccountCheck.getId();
                            logObject.add(" 扣除保证金 卖家获得保证金流水写入完成!");
                        } else {
                            logObject.add(" 扣除保证金 卖家获得保证金流水写入失败");
                            result = -3;
                        }
                    } else {
                        logObject.add(" 扣除保证金 给卖家余额增加失败");
                        result = -2;
                    }
                } else {
                    logObject.add(" 扣除保证金 该拍品没有保证金");
                    result = -4;
                }
            } else if (accountCheck.getTradeType() == TradeType.CASH_WITHDRAW.value()) {
                // 提现失败,退回余额
                logObject.add(" 退回余额 ");
                if (userAccountMapper.updateBalance2UserAccount(userId, amount) > 0) {
                    logObject.add(" 退回余额 余额增加完成 !");
                    accountCheck.setType(BalanceChange.ADD.value());
                    accountCheck.setTitle("退回余额");
                    accountCheck.setStatus(AccountCheckStatus.GETCASH.value());
                    accountCheck.setResultBalance(userAccountMapper.selectByUserId(userId).getBalance());
                    if (accountCheckMapper.insert(accountCheck) > 0) {
                        // un_index : tran_id + 10 + 1
                        result = accountCheck.getId();
                        logObject.add(" 退回余额 余额回退流水写入完成");
                    } else {
                        logObject.add(" 退回余额 余额回退流水写入失败");
                        result = -3;
                    }
                } else {
                    logObject.add(" 退回余额 余额增加失败");
                    result = -2;
                }
            }
        } catch (Exception e) {
            logObject.add(e.getMessage());
            result = -1;
        }
        if (result <= 0) {
            // 如果失败,所有事物回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        logObject.add("*********** 流水处理操作结束   [result]="+result+" *****************");
        userAccountLogService.asyncLoggerUserAccount(logObject);
        return result;
    }

    private int insertInsurePriceInfo(AccountCheck accountCheck) {
        if (accountCheck.getLotId() == null) {
            log.error(" 支付保证金, 未传入LotId");
        }
        UserInfo salerInfo = insurePriceInfoMapper.getLotSalerInfo(accountCheck.getLotId());
        if (salerInfo == null) {
            log.error(" 支付保证金, 未找到卖家信息");
        }
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
    public List<AccountCheck> getAccountCheckByUserId(String userId, Integer status, Long cursor, Integer count) {
        return accountCheckMapper.selectByUserId(userId, status, cursor, count);
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

    @Override
    public List<AccountCheck> queryListByTranTime(String tradeTimeBegin, String tradeTimeEnd) {
        return accountCheckMapper.queryListByTranTime(tradeTimeBegin,tradeTimeEnd);
    }

    @Override
    public AccountCheck getLastAccountCheckByOrderId(String orderId) {
        return accountCheckMapper.getLastAccountCheckByOrderId(orderId);
    }

    @Override
    public List<AccountCheck> verifyAccountCheck(AccountCheckParam param) {
        return accountCheckMapper.verifyAccountCheck(param);
    }

    @Override
    public Long selectAllBalanceSum() {
        return userAccountMapper.selectAllBalanceSum();
    }

    @Override
    public Long selectAllFrozenMoneySum() {
        return userAccountMapper.selectAllFrozenMoneySum();
    }

    @Override
    public int applyVip(ApplyVip applyVip) {
        ApplyVip curr = applyVipMapper.queryUserApplyVip(applyVip.getUserId());
        if (curr == null) {
            return applyVipMapper.insertSelective(applyVip);
        } else {
            applyVip.setId(curr.getId());
            applyVip.setStatus(ApplyVipStatus.applyIng.value());
            applyVip.setUpdateTime(new Date());
            return applyVipMapper.updateByPrimaryKeySelective(applyVip);
        }
    }

    @Override
    public ApplyVip queryUserApplyVip(String userId) {
        return applyVipMapper.queryUserApplyVip(userId);
    }


	@Override
	public List<String> getAllOpenId(Integer subcribe) {
		return wxUserInfoMapper.getAllOpenId(subcribe);
	}

    @Override
    public int setUserStatusBid(String userId, Integer bidStatus) {
        int result = 0;
        WxUserInfo wxUserInfo = wxUserInfoMapper.selectWxUserInfoByUserId(userId);
        if (wxUserInfo != null) {
            UserStatus userStatus = userStatusMapper.selectUserStatusByUserId(userId);
            if (userStatus == null) {
                userStatus = new UserStatus();
                userStatus.setUserId(wxUserInfo.getUserId());
                userStatus.setOpenId(wxUserInfo.getOpenId());
                userStatus.setDtBid(bidStatus);
                userStatus.setUpdatetime(new Date());
                result = userStatusMapper.insertSelective(userStatus);
            } else {
                userStatus.setDtBid(bidStatus);
                userStatus.setUpdatetime(new Date());
                result = userStatusMapper.updateByPrimaryKeySelective(userStatus);
            }
        }
        if (result > 0) {
            // 设置缓存
            if(bidStatus.intValue() == BidStatus.ALLOW.value()) {
                jedisPoolManager.srem(UserCacheNameSpace.FORBIDDEN_BID_USER_SET, userId);
            } else if (bidStatus.intValue() == BidStatus.FORBIDDEN.value()) {
                jedisPoolManager.sadd(UserCacheNameSpace.FORBIDDEN_BID_USER_SET, userId);
            }
        }
        return result;
    }

    @Override
    public int setUserStatusPublish(String userId, Integer publishStatus) {
        int result = 0;
        WxUserInfo wxUserInfo = wxUserInfoMapper.selectWxUserInfoByUserId(userId);
        if (wxUserInfo != null) {
            UserStatus userStatus = userStatusMapper.selectUserStatusByUserId(userId);
            if (userStatus == null) {
                userStatus = new UserStatus();
                userStatus.setUserId(wxUserInfo.getUserId());
                userStatus.setOpenId(wxUserInfo.getOpenId());
                userStatus.setDtPublish(publishStatus);
                userStatus.setUpdatetime(new Date());
                result = userStatusMapper.insertSelective(userStatus);
            } else {
                userStatus.setDtPublish(publishStatus);
                userStatus.setUpdatetime(new Date());
                result = userStatusMapper.updateByPrimaryKeySelective(userStatus);
            }
        }
        if (result > 0) {
            // 设置缓存
            if(publishStatus.intValue() == PublishStatus.ALLOW.value()) {
                jedisPoolManager.srem(UserCacheNameSpace.FORBIDDEN_PUBLISH_USER_SET, userId);
            } else if (publishStatus.intValue() == PublishStatus.FORBIDDEN.value()) {
                jedisPoolManager.sadd(UserCacheNameSpace.FORBIDDEN_PUBLISH_USER_SET, userId);
            }
        }
        return result;
    }

    @Override
    public int setUserStatusDelivery(String userId, String delivery) {
        WxUserInfo wxUserInfo = wxUserInfoMapper.selectWxUserInfoByUserId(userId);
        if (wxUserInfo != null) {
            UserStatus userStatus = userStatusMapper.selectUserStatusByUserId(userId);
            if (userStatus == null) {
                userStatus = new UserStatus();
                userStatus.setUserId(wxUserInfo.getUserId());
                userStatus.setOpenId(wxUserInfo.getOpenId());
                userStatus.setDtDelivery(delivery);
                userStatus.setUpdatetime(new Date());
                return userStatusMapper.insertSelective(userStatus);
            } else {
                userStatus.setDtDelivery(delivery);
                userStatus.setUpdatetime(new Date());
                return userStatusMapper.updateByPrimaryKeySelective(userStatus);
            }
        }
        return 0;
    }

    @Override
    public UserStatus getUserStatusByUserId(String userId) {
        return userStatusMapper.selectUserStatusByUserId(userId);
    }

    @Override
    public Integer getUserOrderSum(String userId, String status) {
        Integer sum = userAccountMapper.getUserOrderSum(userId, status);
        if(sum != null) {
            return sum;
        } else {
            return 0;
        }
    }

    @Override
    public int addUserScore(String userId, Integer role, Integer amount) {
        return userInfoMapper.addUserScore(userId, role, amount);
    }

    @Override
    public int setUserScore(String userId, Integer role, Integer amount) {
        return userInfoMapper.setUserScore(userId, role, amount);
    }

    @Override
    public int setBidCondition(String userId, String bidCondition) {
        WxUserInfo wxUserInfo = wxUserInfoMapper.selectWxUserInfoByUserId(userId);
        if (wxUserInfo != null) {
            UserStatus userStatus = userStatusMapper.selectUserStatusByUserId(userId);
            if (userStatus == null) {
                userStatus = new UserStatus();
                userStatus.setUserId(wxUserInfo.getUserId());
                userStatus.setOpenId(wxUserInfo.getOpenId());
                userStatus.setBidCondition(bidCondition);
                userStatus.setUpdatetime(new Date());
                return userStatusMapper.insertSelective(userStatus);
            } else {
                userStatus.setBidCondition(bidCondition);
                userStatus.setUpdatetime(new Date());
                return userStatusMapper.updateByPrimaryKeySelective(userStatus);
            }
        }
        return 0;
    }


	@Override
	public List<XUserVO> queryUserByParam(String userid, String param, Integer size, Integer page, String status) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Integer changeUserStatusById(String userId, String shielduserid, String status) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Integer findUserForShield(String userId, String ownerId) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Integer getBlackHouseCountByUserId(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public int updateIncrUserCredits(String userId, Integer userCredits) {
		return userInfoMapper.incrUserCredits(userId, userCredits);
	}

	
	@Override
	public int updateDecrUserCredits(String userId, Integer userCredits) {
		return userInfoMapper.decrUserCredits(userId, userCredits);
	}
}
