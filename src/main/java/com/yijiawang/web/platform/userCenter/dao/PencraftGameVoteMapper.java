package com.yijiawang.web.platform.userCenter.dao;

import com.yijiawang.web.platform.userCenter.po.PencraftGameVote;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("pencraftGameVoteMapper")
public interface PencraftGameVoteMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PencraftGameVote record);

    int insertSelective(PencraftGameVote record);

    PencraftGameVote selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PencraftGameVote record);

    int updateByPrimaryKey(PencraftGameVote record);

    /**
     * 根据投票编号获取选手信息
     *
     * @param userNum
     * @return
     */
    PencraftGameVote getVoterByUserNum(String userNum);

    /**
     * 获取选手分页信息
     *
     * @param page
     * @return
     */
    List<PencraftGameVote> getVoters(@Param("page") Integer page, @Param("pageSize") Integer pageSize);

    /**
     * 获取排行榜
     *
     * @return
     */
    List<PencraftGameVote> getTopList();

    /**
     * 同步排行榜
     */
    void syncTop();

    /**
     * 同步总票数
     */
    void syncTotal();
}