package com.yijiawang.web.platform.userCenter.service.inf;

import com.yijiawang.web.platform.userCenter.dao.UserStatistMapper;
import com.yijiawang.web.platform.userCenter.po.UserStatist;
import com.yijiawang.web.platform.userCenter.service.UserStatistService;
import com.yijiawang.web.platform.userCenter.type.BuyerStatistType;
import com.yijiawang.web.platform.userCenter.type.SalerStatistType;
import com.yijiawang.web.platform.userCenter.type.UserRoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by xy on 16/8/18.
 */
@Service("userStatistService")
public class UserStatistServiceImpl implements UserStatistService {

    @Autowired
    private UserStatistMapper userStatistMapper;

    @Override
    public int addUserStatist(String userId, Integer role, Integer type, Integer count) {
        UserStatist userStatist = getUserStatist(userId, role, type);
        if (userStatist == null) {
            initUserStatic(userId, role, type);
        }
        return userStatistMapper.addUserStatist(userId, role, type, count);
    }

    @Override
    public UserStatist getUserStatist(String userId, Integer role, Integer type) {
        UserStatist userStatist = userStatistMapper.getUserStatist(userId, role, type);
        if (userStatist == null) {
            userStatist = initUserStatic(userId, role, type);
        }
        return userStatist;
    }

    /**
     * 初始化用户统计
     * @param userId
     * @param role
     * @param type
     */
    @Override
    public UserStatist initUserStatic(String userId, Integer role, Integer type) {
        UserStatist userStatist = userStatistMapper.getUserStatist(userId, role, type);
        if (userStatist == null) {
            userStatist = new UserStatist();
            userStatist.setUserId(userId);
            userStatist.setUpdatetime(new Date());
            userStatist.setRole(role);
            userStatist.setType(type);
            userStatist.setCount(0);
        }
        Integer count = null;
        if(role.intValue() == UserRoleType.BUYER.value()) {
            if (type.intValue() != BuyerStatistType.NOTPAY.value()) {
                count = userStatistMapper.initBuyerUserStatist(userId, type);
            }
        } else if (role.intValue() == UserRoleType.SALER.value()) {
            count = userStatistMapper.initSalerUserStatist(userId, type);
        }
        if (count != null) {
            userStatist.setCount(count);
        }
        if (userStatist.getId() != null) {
            userStatistMapper.updateByPrimaryKeySelective(userStatist);
        } else {
            userStatistMapper.insertSelective(userStatist);
        }
        return userStatist;
    }

}
