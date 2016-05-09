package com.yijiawang.web.platform.userCenter.dao;


import com.yijiawang.web.platform.userCenter.po.UserInterest;
import com.yijiawang.web.platform.userCenter.vo.InterestCountVO;
import com.yijiawang.web.platform.userCenter.vo.InterestListItemVO;
import com.yijiawang.web.platform.userCenter.vo.InterestUserVO;

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

    List<InterestListItemVO> getInterestList(@Param("userId")String userId, @Param("type")Byte type, @Param("cursor")Long cursor, @Param("count")Integer count);

    Byte getUserInterestStatus(@Param("userId")String userId, @Param("type")Byte type, @Param("entityId")String entityId);

    List<InterestListItemVO> getInterestMeList(@Param("myUserId")String myUserId, @Param("cursor")Long cursor, @Param("count")Integer count);

    InterestUserVO getInterestUser(String userId);
    
    InterestCountVO getInterestCount(@Param("userId")String userId);
    
    List<UserInterest> getInterestFansList(String entityId);

    List<String> getUserInterestEntityIdByType(@Param("userId") String userId, @Param("type") Byte type);
}