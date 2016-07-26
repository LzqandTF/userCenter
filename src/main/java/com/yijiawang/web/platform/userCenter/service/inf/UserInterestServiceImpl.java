package com.yijiawang.web.platform.userCenter.service.inf;

import com.yijiawang.web.platform.userCenter.cache.JedisPoolManager;
import com.yijiawang.web.platform.userCenter.cache.UserCacheNameSpace;
import com.yijiawang.web.platform.userCenter.dao.UserInfoMapper;
import com.yijiawang.web.platform.userCenter.dao.UserInterestMapper;
import com.yijiawang.web.platform.userCenter.dao.WxUserInfoMapper;
import com.yijiawang.web.platform.userCenter.po.UserInfo;
import com.yijiawang.web.platform.userCenter.po.UserInterest;
import com.yijiawang.web.platform.userCenter.service.UserInterestService;
import com.yijiawang.web.platform.userCenter.type.InterestType;
import com.yijiawang.web.platform.userCenter.vo.InterestCountVO;
import com.yijiawang.web.platform.userCenter.vo.InterestListItemVO;
import com.yijiawang.web.platform.userCenter.vo.InterestUserVO;

import com.yijiawang.web.platform.userCenter.vo.RecommendSalerVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by xy on 16/4/6.
 */
@Service("userInterestService")
public class UserInterestServiceImpl implements UserInterestService{

    @Autowired
    private UserInterestMapper userInterestMapper;
    @Autowired
    private WxUserInfoMapper wxUserInfoMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private JedisPoolManager jedisPoolManager;

    @Override
    public int setUserInterest(UserInterest userInterest) {
        return userInterestMapper.setUserInterest(userInterest);
    }

    @Override
    public List<InterestListItemVO> getInterestList(String userId, Byte interestType, Long cursor, Integer count) {
        return userInterestMapper.getInterestList(userId, interestType, cursor, count);
    }

    @Override
	public List<String> getInterestListByEntityId(String entityId,
			Byte type) {
		return wxUserInfoMapper.getInterestListByEntityId(entityId, type);
	}

    @Override
    public Byte getUserInterestStatus(String userId, Byte interestType, String entityId) {
        Byte status = userInterestMapper.getUserInterestStatus(userId, interestType, entityId);
        if (status == null) {
            return 1;
        } else {
            return status;
        }
    }

    @Override
    public List<InterestListItemVO> getInterestMeList(String myUserId, Long cursor, Integer count) {
        return userInterestMapper.getInterestMeList(myUserId, cursor, count);
    }

    @Override
    public InterestUserVO getInterestUserInfo(String userId, String myUserId) {
        InterestUserVO vo = userInterestMapper.getInterestUser(userId);
        Byte status = userInterestMapper.getUserInterestStatus(myUserId, InterestType.USER.value(), userId);
        if (status == null) {
            vo.setStatus((byte)1);
        } else {
            vo.setStatus(status);
        }
        return vo;
    }

	@Override
	public InterestCountVO getInterestCount(String userId) {
		return userInterestMapper.getInterestCount(userId);
	}

	@Override
	public List<UserInterest> getInterestFansList(String entityId) {
		return userInterestMapper.getInterestFansList(entityId);
	}

    @Override
    public List<RecommendSalerVO> recommendSaler(String userId, Integer next, Integer count) {
        // 当前系统推荐卖家列表
        Set<UserInfo> recommendList = userInfoMapper.getRecommendUserList();
        if (recommendList != null && recommendList.size() > 0) {
            // 返回客户端的列表
            List<RecommendSalerVO> retList = new ArrayList<>();
            // 用户已经关注的用户id
            Set<String> hasInterestSaler = userInterestMapper.getUserInterestEntityIdByType(userId, InterestType.USER.value());
            String jedisKey = String.format(UserCacheNameSpace.RECOMMEND_SALER_LIST, userId);
            if (next == 0) {
                // 第一次请求,清空缓存
                jedisPoolManager.del(jedisKey);
            }
            Iterator<UserInfo> itor = recommendList.iterator();
            while(itor.hasNext()) {
                UserInfo saler = itor.next();
                // 是否是自己
                if (saler.getUserId().equals(userId)) {
                    continue;
                }
                // 已经关注了该卖家
                if (hasInterestSaler.contains(saler.getUserId())) {
                    continue;
                }
                // 是否已经返回给前端
                if (next > 0) {
                    // 非第一次请求,已经返回过
                    if (jedisPoolManager.sismember(jedisKey, saler.getUserId())) {
                        continue;
                    }
                }
                RecommendSalerVO vo = new RecommendSalerVO();
                vo.setSalerId(saler.getUserId());
                vo.setSalerName(saler.getName());
                vo.setInterested(0);
                retList.add(vo);
                // 放入缓存中
                jedisPoolManager.sadd(jedisKey, saler.getUserId());
                if (retList.size() == count) {
                    return retList;
                }
            }
            return retList;
        }
        return null;
    }
}
