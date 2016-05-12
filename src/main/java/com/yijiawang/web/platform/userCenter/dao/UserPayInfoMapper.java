package com.yijiawang.web.platform.userCenter.dao;

import com.yijiawang.web.platform.userCenter.po.UserPayInfo;
import org.apache.ibatis.annotations.Param;

public interface UserPayInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserPayInfo record);

    int insertSelective(UserPayInfo record);

    UserPayInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserPayInfo record);

    int updateByPrimaryKey(UserPayInfo record);

    // ------ 自定义
    UserPayInfo selectByUserId(String userId);

    int updateUserPayPassword(@Param("userId") String userId, @Param("paypwd") String paypwd);
}