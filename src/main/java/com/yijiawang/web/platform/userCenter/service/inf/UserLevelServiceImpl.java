package com.yijiawang.web.platform.userCenter.service.inf;

import com.yijiawang.web.platform.userCenter.cache.JedisPoolManager;
import com.yijiawang.web.platform.userCenter.cache.UserCacheNameSpace;
import com.yijiawang.web.platform.userCenter.dao.UserLevelMapper;
import com.yijiawang.web.platform.userCenter.po.UserInfo;
import com.yijiawang.web.platform.userCenter.po.UserLevelBuyer;
import com.yijiawang.web.platform.userCenter.po.UserLevelSaler;
import com.yijiawang.web.platform.userCenter.service.UserLevelService;
import com.yijiawang.web.platform.userCenter.service.UserService;
import com.yijiawang.web.platform.userCenter.type.UserRoleType;
import com.yijiawang.web.platform.userCenter.vo.UserLevelVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by xy on 16/8/18.
 */
@Service("userLevelService")
public class UserLevelServiceImpl implements UserLevelService{

    @Autowired
    private UserLevelMapper userLevelMapper;
    @Autowired
    private JedisPoolManager jedisPoolManager;
    @Autowired
    private UserService userService;

    @Override
    public void initUserLevelBuyer() {
        List<UserLevelBuyer> list = userLevelMapper.selectAllUserLevelBuyer();
        if(list != null) {
            jedisPoolManager.del(UserCacheNameSpace.USER_LEVEL_BUYER_SORT_SET);
            jedisPoolManager.del(UserCacheNameSpace.USER_LEVEL_BUYER_NAME_HSET);
            for (UserLevelBuyer buyer : list) {
                Double score = Double.parseDouble(buyer.getScore().toString());
                Integer level = buyer.getLevel();
                jedisPoolManager.hset(UserCacheNameSpace.USER_LEVEL_BUYER_NAME_HSET, level.toString(), buyer.getName());
                if (buyer.getLevel() == 1) {
                    jedisPoolManager.zadd(UserCacheNameSpace.USER_LEVEL_BUYER_SORT_SET, score, level+"_start");
                } else {
                    Integer lastLevel = level - 1;
                    jedisPoolManager.zadd(UserCacheNameSpace.USER_LEVEL_BUYER_SORT_SET, score-1, lastLevel+"_end");
                    jedisPoolManager.zadd(UserCacheNameSpace.USER_LEVEL_BUYER_SORT_SET, score, level+"_start");
                }
            }
        }
    }

    @Override
    public void initUserLevelSaler() {
        List<UserLevelSaler> list = userLevelMapper.selectAllUserLevelSaler();
        if(list != null) {
            jedisPoolManager.del(UserCacheNameSpace.USER_LEVEL_SALER_SORT_SET);
            jedisPoolManager.del(UserCacheNameSpace.USER_LEVEL_SALER_NAME_HSET);
            for (UserLevelSaler saler : list) {
                Double score = Double.parseDouble(saler.getScore().toString());
                Integer level = saler.getLevel();
                jedisPoolManager.hset(UserCacheNameSpace.USER_LEVEL_SALER_NAME_HSET, level.toString(), saler.getName());
                if (saler.getLevel() == 1) {
                    jedisPoolManager.zadd(UserCacheNameSpace.USER_LEVEL_SALER_SORT_SET, score, level+"_start");
                } else {
                    Integer lastLevel = level - 1;
                    jedisPoolManager.zadd(UserCacheNameSpace.USER_LEVEL_SALER_SORT_SET, score-1, lastLevel+"_end");
                    jedisPoolManager.zadd(UserCacheNameSpace.USER_LEVEL_SALER_SORT_SET, score, level+"_start");
                }
            }
        }
    }

    @Override
    public UserLevelVO getUserLevelBuyer(String userId) {
        UserInfo userInfo = userService.getUserByUserId(userId);
        if (userInfo == null) {
        	return null;
        }
        Integer score = userInfo.getBuyScore();
        if (score == null) {
            score = initUserBuyerScore(userId, UserRoleType.BUYER.value());
            if (score == null) {
                score = 0;
            }
            userService.setUserScore(userId, UserRoleType.BUYER.value(), score);
        }
        // 转化为元
        score = score/100;
        Set<String> levelSet = jedisPoolManager.zrangeByscore(UserCacheNameSpace.USER_LEVEL_BUYER_SORT_SET, score.toString(), "+inf", 0, 1);
        if(levelSet != null) {
            String levelStr = null;
            Iterator<String> it = levelSet.iterator();
            while(it.hasNext()){
                levelStr = it.next();
            }
            String level = levelStr.substring(0,levelStr.lastIndexOf("_"));
            UserLevelVO vo = new UserLevelVO();
            vo.setLevel(Integer.parseInt(level));
            vo.setName(jedisPoolManager.hget(UserCacheNameSpace.USER_LEVEL_BUYER_NAME_HSET, level));
            return vo;
        } else {
            return null;
        }
    }

    @Override
    public UserLevelVO getUserLevelSaler(String userId) {
        UserInfo userInfo = userService.getUserByUserId(userId);
        Integer score = userInfo.getSellScore();
        if (score == null) {
            score = initUserBuyerScore(userId, UserRoleType.SALER.value());
            if (score == null) {
                score = 0;
            }
            userService.setUserScore(userId, UserRoleType.SALER.value(), score);
        }
        //转化为元
        score = score/100;
        Set<String> levelSet = jedisPoolManager.zrangeByscore(UserCacheNameSpace.USER_LEVEL_SALER_SORT_SET, score.toString(), "+inf", 0, 1);
        if(levelSet != null) {
            String levelStr = null;
            Iterator<String> it = levelSet.iterator();
            while(it.hasNext()){
                levelStr = it.next();
            }
            String level = levelStr.substring(0,levelStr.lastIndexOf("_"));
            UserLevelVO vo = new UserLevelVO();
            vo.setLevel(Integer.parseInt(level));
            vo.setName(jedisPoolManager.hget(UserCacheNameSpace.USER_LEVEL_SALER_NAME_HSET, level));
            return vo;
        } else {
            return null;
        }
    }

    private Integer initUserBuyerScore(String userId, Integer role) {
        if (role.intValue() == UserRoleType.BUYER.value()) {
            return userLevelMapper.getUserBuyerScore(userId);
        } else if (role.intValue() == UserRoleType.SALER.value()) {
            return userLevelMapper.getUserSellSocre(userId);
        }
        return null;
    }
}
