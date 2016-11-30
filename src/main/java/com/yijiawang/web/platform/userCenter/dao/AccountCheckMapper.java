package com.yijiawang.web.platform.userCenter.dao;

import com.yijiawang.web.platform.userCenter.param.AccountCheckParam;
import com.yijiawang.web.platform.userCenter.po.AccountCheck;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AccountCheckMapper {

    long insert(AccountCheck record);

    int insertSelective(AccountCheck record);

    AccountCheck selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AccountCheck record);

    // -------- 自定义 -------
    AccountCheck selectByTranId(String tranId);

    List<AccountCheck> selectByUserId(@Param("userId") String userId, @Param("status") Integer status, @Param("cursor") Long cursor, @Param("count") Integer count);

    AccountCheck getOrderPayAccountCheck(String orderId);

    /**
     * 获取众筹支付流水
     *
     * @param orderId
     * @return
     */
    AccountCheck getZcOrderPayAccountCheck(String orderId);

    /**
     * 判断该笔流水是否已经处理过,规则, trand_id + trade_type + type 为唯一键值
     *
     * @return
     */
    AccountCheck queryAccountCheck(AccountCheck accountCheck);

    /**
     * 根据订单id获取冻结流水
     *
     * @param orderId
     * @return
     */
    AccountCheck selectFrozenAccountCheck(String orderId);

    /**
     * 根据交易时间段获取列表
     *
     * @param tradeTimeBegin
     * @param tradeTimeEnd
     * @return
     */
    List<AccountCheck> queryListByTranTime(@Param("tradeTimeBegin") String tradeTimeBegin, @Param("tradeTimeEnd") String tradeTimeEnd);

    /**
     * 根据订单id获取最后一条流水
     *
     * @param orderId
     * @return
     */
    AccountCheck getLastAccountCheckByOrderId(String orderId);

    /**
     * 根据流水号、资金流向、金额、交易类型判断交易流水表中是否存在此条记录
     *
     * @param param
     * @return
     */
    List<AccountCheck> verifyAccountCheck(AccountCheckParam param);
}