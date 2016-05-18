package com.yijiawang.web.platform.userCenter.dao;

import com.yijiawang.web.platform.userCenter.po.AccountCheck;

public interface AccountCheckMapper {
    int deleteByPrimaryKey(Long id);

    int insert(AccountCheck record);

    int insertSelective(AccountCheck record);

    AccountCheck selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AccountCheck record);

    int updateByPrimaryKey(AccountCheck record);
}