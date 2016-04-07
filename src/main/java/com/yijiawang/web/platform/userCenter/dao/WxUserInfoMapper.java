package com.yijiawang.web.platform.userCenter.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yijiawang.web.platform.userCenter.po.WxUserInfo;

public interface WxUserInfoMapper {
    int insert(WxUserInfo record);

    int insertSelective(WxUserInfo record);

    WxUserInfo selectByPrimaryKey(String openId);

    int updateByPrimaryKeySelective(WxUserInfo record);

    int updateByPrimaryKey(WxUserInfo record);
    
    List<String> getInterestListByEntityId(@Param("entityId")String entityId, @Param("type")String type);
}

