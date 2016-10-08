package com.yijiawang.web.platform.userCenter.dao;

import org.apache.ibatis.annotations.Param;

import com.yijiawang.web.platform.userCenter.po.UserSignInLog;

public interface UserSignInLogMapper {
	
    int deleteByPrimaryKey(Long id);

    int insert(UserSignInLog record);

    int insertSelective(UserSignInLog record);

    UserSignInLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserSignInLog record);

    int updateByPrimaryKey(UserSignInLog record);

    UserSignInLog selectLastOneSignInLogByUserId(@Param("userId")String userId);
}