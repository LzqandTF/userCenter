package com.yijiawang.web.platform.userCenter.dao;


import com.yijiawang.web.platform.userCenter.po.UserInterest;
import com.yijiawang.web.platform.userCenter.vo.InterestListItemVO;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserInterestMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserInterest record);

    int insertSelective(UserInterest record);

    UserInterest selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserInterest record);

    int updateByPrimaryKey(UserInterest record);

    int setUserInterest(UserInterest userInterest);

    List<InterestListItemVO> getInterestList(@Param("userId")String userId, @Param("type")String type);
}