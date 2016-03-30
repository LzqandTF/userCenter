package com.yijiawang.web.platform.userCenter.service.inf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yijiawang.web.platform.userCenter.dao.UserInfoMapper;
import com.yijiawang.web.platform.userCenter.po.UserInfo;
import com.yijiawang.web.platform.userCenter.service.UserService;

@Service("userService")
public class UserServiceImpl implements UserService{
	@Autowired
	private UserInfoMapper userInfoMapper;
	
	
	@Override
	public UserInfo getUserByUserId(String userId) {
		return userInfoMapper.getUserByUserId(userId);
	}


	@Override
	public int updateByPrimaryKey(UserInfo userInfo) {
		return userInfoMapper.updateByPrimaryKey(userInfo);
	}


	@Override
	public int insertSelective(UserInfo userInfo) {
		return userInfoMapper.insertSelective(userInfo);
	}

}
