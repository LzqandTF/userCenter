package com.yijiawang.web.platform.userCenter.dao;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.yijiawang.web.platform.userCenter.po.UserScore;

public interface UserScoreMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserScore record);

    int insertSelective(UserScore record);

    UserScore selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserScore record);

    int updateByPrimaryKey(UserScore record);
    
    List<UserScore> queryListUserScoreByUserId(@Param("userId") String userId, @Param("cursor") Long cursor, @Param("count") Integer count);

    int countCurdateDataByRule(@Param("userId") String userId, @Param("classCode") String classCode, @Param("createTime") Date createTime);
    
    int countUserScoreDataByRule(@Param("userId") String userId, @Param("classCode") String classCode, @Param("codeKey") String codeKey);

    List<UserScore> getUserScoreByOrderId(@Param("orderId") String orderId);
}