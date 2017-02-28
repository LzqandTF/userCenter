package com.yijiawang.web.platform.userCenter.dao;

import com.yijiawang.web.platform.userCenter.po.UserLevelLog;

public interface UserLevelLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserLevelLog record);

    int insertSelective(UserLevelLog record);

    UserLevelLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserLevelLog record);

    int updateByPrimaryKey(UserLevelLog record);
}