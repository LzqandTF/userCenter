package com.yijiawang.web.platform.userCenter.dao;

import com.yijiawang.web.platform.userCenter.po.InsurePriceInfo;

public interface InsurePriceInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(InsurePriceInfo record);

    int insertSelective(InsurePriceInfo record);

    InsurePriceInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(InsurePriceInfo record);

    int updateByPrimaryKey(InsurePriceInfo record);
}