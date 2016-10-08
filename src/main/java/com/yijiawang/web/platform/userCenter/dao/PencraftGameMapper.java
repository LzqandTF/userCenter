package com.yijiawang.web.platform.userCenter.dao;


import com.yijiawang.web.platform.userCenter.po.PencraftGame;

public interface PencraftGameMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PencraftGame record);

    int insertSelective(PencraftGame record);

    PencraftGame selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PencraftGame record);

    int updateByPrimaryKeyWithBLOBs(PencraftGame record);

    int updateByPrimaryKey(PencraftGame record);

    /**
     * 根据电话号获取书法大赛报名信息
     *
     * @param userTel
     * @return
     */
    PencraftGame selectByTel(String userTel);

    /**
     * 根据openId获取书法大赛报名信息
     *
     * @param openId
     * @return
     */
    PencraftGame selectByOpenId(String openId);
}