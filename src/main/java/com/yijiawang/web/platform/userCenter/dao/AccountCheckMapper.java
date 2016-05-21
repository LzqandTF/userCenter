package com.yijiawang.web.platform.userCenter.dao;

import com.yijiawang.web.platform.userCenter.po.AccountCheck;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AccountCheckMapper {
    int deleteByPrimaryKey(Long id);

    int insert(AccountCheck record);

    int insertSelective(AccountCheck record);

    AccountCheck selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AccountCheck record);

    int updateByPrimaryKey(AccountCheck record);

    // -------- 自定义 -------
    AccountCheck selectByTranId(String tranId);

    List<AccountCheck> selectByOrderId(String orderId);

    List<AccountCheck> selectByUserId(@Param("userId") String userId, @Param("cursor") Long cursor, @Param("count") Integer count);
}