package com.yijiawang.web.platform.userCenter.dao;

import com.yijiawang.web.platform.userCenter.po.ProtectQuestion;
import com.yijiawang.web.platform.userCenter.vo.UserProtectQuestionVO;

import java.util.List;

public interface ProtectQuestionMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ProtectQuestion record);

    int insertSelective(ProtectQuestion record);

    ProtectQuestion selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ProtectQuestion record);

    int updateByPrimaryKey(ProtectQuestion record);

    //---------- 自定义方法
    List<UserProtectQuestionVO> userProtectQuestion(String userId);
}