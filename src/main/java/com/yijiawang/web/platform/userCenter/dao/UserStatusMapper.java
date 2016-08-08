package com.yijiawang.web.platform.userCenter.dao;


import com.yijiawang.web.platform.userCenter.po.UserStatus;
import org.apache.ibatis.annotations.Param;

public interface UserStatusMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserStatus record);

    int insertSelective(UserStatus record);

    UserStatus selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserStatus record);

    int updateByPrimaryKey(UserStatus record);

    // ---------- 手动添加方法
    UserStatus selectUserStatusByUserId(String userId);
}