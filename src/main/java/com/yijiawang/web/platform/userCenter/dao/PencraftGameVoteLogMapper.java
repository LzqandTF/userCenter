package com.yijiawang.web.platform.userCenter.dao;

import com.yijiawang.web.platform.userCenter.po.PencraftGameVoteLog;
import org.apache.ibatis.annotations.Param;

public interface PencraftGameVoteLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PencraftGameVoteLog record);

    int insertSelective(PencraftGameVoteLog record);

    PencraftGameVoteLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PencraftGameVoteLog record);

    int updateByPrimaryKey(PencraftGameVoteLog record);

    /**
     * 根据书法大赛参赛码、投票者对应艺家平台id 获取当天投票记录
     *
     * @param userGameId
     * @param voterUserId
     * @return
     */
    PencraftGameVoteLog selectByGameIdAndNum(@Param("userGameId") String userGameId, @Param("voterUserId") String voterUserId);
}