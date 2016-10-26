package com.yijiawang.web.platform.userCenter.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yijiawang.web.platform.userCenter.po.UserCard;

public interface UserCardMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserCard record);

    int insertSelective(UserCard record);

    UserCard selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserCard record);

    int updateByPrimaryKey(UserCard record);
    
    List<UserCard> findAllUserCard(@Param("userId")String userId);

    UserCard selectByPrimaryKeyByUserId(@Param("id")Long id, @Param("userId")String userId);
}