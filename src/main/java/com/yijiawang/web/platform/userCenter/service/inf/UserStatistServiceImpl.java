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
        return userStatistMapper.getUserStatist(userId, role, type);
    }

    /**
     * 初始化用户统计
     * @param userId
     * @param role
     * @param type
     */
    private void initUserStatic(String userId, Integer role, Integer type) {
        UserStatist userStatist = new UserStatist();
        userStatist.setUserId(userId);
        userStatist.setUpdatetime(new Date());
        userStatist.setRole(role);
        userStatist.setType(type);
        if(role.intValue() == UserRoleType.BUYER.value()) {
            if (BuyerStatistType.BID.value() == type.intValue()) {

            } else if (BuyerStatistType.ORDER.value() == type.intValue()) {

            } else if (BuyerStatistType.NOTPAY.value() == type.intValue()) {

            } else if (BuyerStatistType.PAY.value() == type.intValue()) {

            } else if (BuyerStatistType.RETURN.value() == type.intValue()) {

            } else if (BuyerStatistType.FINISH.value() == type.intValue()) {

            }
        } else if (role.intValue() == UserRoleType.SALER.value()) {
            if (SalerStatistType.PUBLISH_LOT.value() == type.intValue()) {

            } else if (SalerStatistType.PUBLISH_PERFORMANCE.value() == type.intValue()) {

            } else if (SalerStatistType.ORDER.value() == type.intValue()) {

            } else if (SalerStatistType.DELIVERY.value() == type.intValue()) {

            } else if (SalerStatistType.RETURN.value() == type.intValue()) {

            } else if (SalerStatistType.FINISH.value() == type.intValue()) {

            }
        }
        if (userStatist.getCount() != null) {
            userStatistMapper.insertSelective(userStatist);
        }
    }
}
