package com.yijiawang.web.platform.userCenter.service.inf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yijiawang.web.platform.userCenter.dao.UserEntityMapper;
import com.yijiawang.web.platform.userCenter.po.UserEntity;
import com.yijiawang.web.platform.userCenter.service.UserService;

@Service("userService")
public class UserServiceImpl implements UserService{
	@Autowired
	private UserEntityMapper userEntityMapper;
	
	
	@Override
	public UserEntity getUserByUserId(String userCustomId) {
		return userEntityMapper.getUserByUserId(userCustomId);
	}

}
