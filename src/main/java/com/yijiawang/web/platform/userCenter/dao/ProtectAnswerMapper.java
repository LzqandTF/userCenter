package com.yijiawang.web.platform.userCenter.dao;


import com.yijiawang.web.platform.userCenter.po.ProtectAnswer;

public interface ProtectAnswerMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ProtectAnswer record);

    int insertSelective(ProtectAnswer record);

    ProtectAnswer selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ProtectAnswer record);

    int updateByPrimaryKey(ProtectAnswer record);
}