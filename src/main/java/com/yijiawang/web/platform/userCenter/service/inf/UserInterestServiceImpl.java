package com.yijiawang.web.platform.userCenter.service.inf;

import com.yijiawang.web.platform.userCenter.dao.UserInterestMapper;
import com.yijiawang.web.platform.userCenter.dao.WxUserInfoMapper;
import com.yijiawang.web.platform.userCenter.po.UserInterest;
import com.yijiawang.web.platform.userCenter.service.UserInterestService;
import com.yijiawang.web.platform.userCenter.type.InterestType;
import com.yijiawang.web.platform.userCenter.vo.InterestCountVO;
import com.yijiawang.web.platform.userCenter.vo.InterestListItemVO;
import com.yijiawang.web.platform.userCenter.vo.InterestUserVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by xy on 16/4/6.
 */
@Service("userInterestService")
public class UserInterestServiceImpl implements UserInterestService{

    @Autowired
    private UserInterestMapper userInterestMapper;
    @Autowired
    private WxUserInfoMapper wxUserInfoMapper;

    @Override
    public int setUserInterest(UserInterest userInterest) {
        return userInterestMapper.setUserInterest(userInterest);
    }

    @Override
    public List<InterestListItemVO> getInterestList(String userId, String interestType, Long cursor, Integer count) {
        return userInterestMapper.getInterestList(userId, interestType, cursor, count);
    }

	@Override
	public List<String> getInterestListByEntityId(String entityId,
			String type) {
		return wxUserInfoMapper.getInterestListByEntityId(entityId, type);
	}

    @Override
    public String getUserInterestStatus(String userId, String interestType, String entityId) {
        String status = userInterestMapper.getUserInterestStatus(userId, interestType, entityId);
        if (status == null) {
            return "1";
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
        String status = userInterestMapper.getUserInterestStatus(myUserId, InterestType.USER.value(), userId);
        if (status == null) {
            vo.setStatus("1");
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
}
