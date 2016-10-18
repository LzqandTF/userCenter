package com.yijiawang.web.platform.userCenter.dao;

import com.yijiawang.web.platform.userCenter.po.UserInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Set;


public interface UserInfoMapper {
    int insert(UserInfo record);

    int insertSelective(UserInfo record);

    UserInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserInfo record);

    int updateByPrimaryKey(UserInfo record);
    
    UserInfo getUserByUserId(String userId);

    Set<UserInfo> getRecommendUserList();

    int addUserScore(@Param("userId")String userId, @Param("role")Integer role, @Param("amount")Integer amount);

    int setUserScore(@Param("userId")String userId, @Param("role")Integer role, @Param("amount")Integer amount);
    
    int incrUserCredits(@Param("userId")String userId, @Param("userCredits")Integer userCredits);
    
    int decrUserCredits(@Param("userId")String userId, @Param("userCredits")Integer userCredits);

	Integer getBStatusByUserId(@Param("userId")String userId, @Param("myUserid")String myUserid);
}