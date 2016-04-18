package com.yijiawang.web.platform.userCenter.service.inf;

import com.yijiawang.web.platform.userCenter.dao.UserInterestMapper;
import com.yijiawang.web.platform.userCenter.dao.WxUserInfoMapper;
import com.yijiawang.web.platform.userCenter.po.UserInterest;
import com.yijiawang.web.platform.userCenter.service.UserInterestService;
import com.yijiawang.web.platform.userCenter.vo.InterestListItemVO;

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
}
