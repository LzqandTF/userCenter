package com.yijiawang.web.platform.userCenter.dao;


import com.yijiawang.web.platform.userCenter.po.UserStatist;
import org.apache.ibatis.annotations.Param;

public interface UserStatistMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(UserStatist record);

    int insertSelective(UserStatist record);

    UserStatist selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserStatist record);

    int updateByPrimaryKey(UserStatist record);

    // ------------- 新增 -----------------------

    UserStatist getUserStatist(@Param("userId")String userId, @Param("role")Integer role, @Param("type")Integer type);

    int addUserStatist(@Param("userId")String userId, @Param("role")Integer role, @Param("type")Integer type, @Param("count")Integer count);

    int initSalerUserStatist(@Param("userId")String userId, @Param("type")Integer type);

    int initBuyerUserStatist(@Param("userId")String userId, @Param("type")Integer type);
}