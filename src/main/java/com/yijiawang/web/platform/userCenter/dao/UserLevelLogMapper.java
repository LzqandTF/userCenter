package com.yijiawang.web.platform.userCenter.dao;

import com.yijiawang.web.platform.userCenter.po.UserLevelLog;
import org.apache.ibatis.annotations.Param;

public interface UserLevelLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserLevelLog record);

    int insertSelective(UserLevelLog record);

    UserLevelLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserLevelLog record);

    int updateByPrimaryKey(UserLevelLog record);

    UserLevelLog getUserLevelLog(@Param("userId") String userId, @Param("roleType") Integer roleType, @Param("category") Integer category, @Param("entityId") String entityId);
}