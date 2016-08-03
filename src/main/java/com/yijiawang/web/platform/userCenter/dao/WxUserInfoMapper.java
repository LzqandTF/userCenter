package com.yijiawang.web.platform.userCenter.dao;

import java.util.List;

import com.yijiawang.web.platform.userCenter.vo.UserVO;

import org.apache.ibatis.annotations.Param;

import com.yijiawang.web.platform.userCenter.po.WxUserInfo;

public interface WxUserInfoMapper {
    int insert(WxUserInfo record);

    int insertSelective(WxUserInfo record);

    WxUserInfo selectByPrimaryKey(String openId);

    int updateByPrimaryKeySelective(WxUserInfo record);

    int updateByPrimaryKey(WxUserInfo record);
    
    List<String> getInterestListByEntityId(@Param("entityId")String entityId, @Param("type")Byte type);

    WxUserInfo selectWxUserInfoByUserId(String userId);

    UserVO selectUserVOByUserId(String userId);
       
    List<String> getAllOpenId(@Param("subcribe")Integer subcribe);
}

