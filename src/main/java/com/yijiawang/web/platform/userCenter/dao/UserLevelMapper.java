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

    int getUserSellSocre(String userId);

    int getUserBuyerScore(String userId);

}
