package com.yijiawang.web.platform.userCenter.dao;

import com.yijiawang.web.platform.userCenter.po.UserEntity;

public interface UserEntityMapper {
	public UserEntity getUserByUserId(String userCustomId);
}
