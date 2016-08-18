package com.yijiawang.web.platform.userCenter.dao;

import com.yijiawang.web.platform.userCenter.po.UserLevelBuyer;
import com.yijiawang.web.platform.userCenter.po.UserLevelSaler;

import java.util.List;

/**
 * Created by xy on 16/8/17.
 */
public interface UserLevelMapper {

    List<UserLevelBuyer> selectAllUserLevelBuyer();

    List<UserLevelSaler> selectAllUserLevelSaler();

    Integer getUserSellSocre(String userId);

    Integer getUserBuyerScore(String userId);

}
