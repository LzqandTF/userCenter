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
    public List<InterestListItemVO> getInterestList(String userId, String interestType) {
        return userInterestMapper.getInterestList(userId, interestType);
    }

	@Override
	public List<String> getInterestListByEntityId(String entityId,
			String type) {
		return wxUserInfoMapper.getInterestListByEntityId(entityId, type);
	}
}