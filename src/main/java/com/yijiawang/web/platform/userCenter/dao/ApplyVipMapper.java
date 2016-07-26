package com.yijiawang.web.platform.userCenter.dao;


import com.yijiawang.web.platform.userCenter.po.ApplyVip;

public interface ApplyVipMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ApplyVip record);

    int insertSelective(ApplyVip record);

    ApplyVip selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ApplyVip record);

    int updateByPrimaryKey(ApplyVip record);

    ApplyVip queryUserApplyVip(String userId);
}