package com.yijiawang.web.platform.userCenter.dao;

import com.yijiawang.web.platform.userCenter.po.UserAccount;
import org.apache.ibatis.annotations.Param;

public interface UserAccountMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserAccount record);

    int insertSelective(UserAccount record);

    UserAccount selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserAccount record);

    int updateByPrimaryKey(UserAccount record);

    // ------ 自定义
    UserAccount selectByUserId(String userId);

    int updateUserPayPassword(@Param("userId") String userId, @Param("paypwd") String paypwd);

    int updateBalance2UserAccount(@Param("userId") String userId, @Param("amount") Integer amount);

    int updateFrozenMoney2UserAccount(@Param("userId") String userId, @Param("amount") Integer amount);
}