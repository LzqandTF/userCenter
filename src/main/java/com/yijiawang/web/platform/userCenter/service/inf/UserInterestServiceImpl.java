package com.yijiawang.web.platform.userCenter.service.inf;

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
    public List<RecommendSalerVO> recommendSaler(String userId) {
        // 当前系统推荐卖家列表
        List<UserInfo> recommendList = userInfoMapper.getRecommendUserList();
        // 返回客户端的列表
        List<RecommendSalerVO> retList = new ArrayList<>();
        // 用户已经关注的用户id
        List<String> hasInterestSaler = userInterestMapper.getUserInterestEntityIdByType(userId, InterestType.USER.value());
        // 已经处理过的推荐用户集合
        Set<String> retRecommendSet = new HashSet<>();
        UserInfo recommendUserInfo = null;
        do{
            int index = new Random().nextInt(recommendList.size());
            recommendUserInfo = recommendList.get(index);
            if (!retRecommendSet.contains(recommendUserInfo.getUserId())) {
                retRecommendSet.add(recommendUserInfo.getUserId());
                RecommendSalerVO vo = new RecommendSalerVO();
                vo.setSalerId(recommendUserInfo.getUserId());
                vo.setSalerName(recommendUserInfo.getName());
                if (hasInterestSaler.contains(recommendUserInfo.getUserId())) {
                    continue;
                }
                retList.add(vo);
            }
        }while (retList.size() < 6 && retRecommendSet.size()<recommendList.size());
        return retList;
    }
}
