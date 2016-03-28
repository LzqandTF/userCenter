package com.yijiawang.web.platform.userCenter.service.inf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yijiawang.web.platform.userCenter.dao.WxUserInfoMapper;
import com.yijiawang.web.platform.userCenter.po.WxUserInfo;
import com.yijiawang.web.platform.userCenter.service.WxUserService;
@Service("wxUserService")
public class WxUserServiceImpl implements WxUserService{
	
	@Autowired
	private WxUserInfoMapper wxUserInfoMapper;

	@Override
	public int insertSelective(WxUserInfo wxUserInfo) {
		return wxUserInfoMapper.insertSelective(wxUserInfo);
	}

	@Override
	public int updateByPrimaryKey(WxUserInfo wxUserInfo) {
		return wxUserInfoMapper.updateByPrimaryKey(wxUserInfo);
	}

	@Override
	public WxUserInfo getUserByOpenId(String openId) {
		return wxUserInfoMapper.selectByPrimaryKey(openId);
	}

}
