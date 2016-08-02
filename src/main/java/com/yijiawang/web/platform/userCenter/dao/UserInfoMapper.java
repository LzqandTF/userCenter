package com.yijiawang.web.platform.userCenter.dao;

import com.yijiawang.web.platform.userCenter.po.UserInfo;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

public interface UserInfoMapper {
    int insert(UserInfo record);

    int insertSelective(UserInfo record);

    UserInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserInfo record);

    int updateByPrimaryKey(UserInfo record);
    
    UserInfo getUserByUserId(String userId);

    Set<UserInfo> getRecommendUserList();
    
    List<String> getAllOpenId(@Param("subcribe")Integer subcribe);
}