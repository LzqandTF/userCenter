package com.yijiawang.web.platform.userCenter.service.inf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yijiawang.web.platform.userCenter.cache.JedisPoolManager;
import com.yijiawang.web.platform.userCenter.dao.WxUserInfoMapper;
import com.yijiawang.web.platform.userCenter.po.WxUserInfo;
import com.yijiawang.web.platform.userCenter.service.WxUserService;
@Service("wxUserService")
public class WxUserServiceImpl implements WxUserService{
	
	@Autowired
	private WxUserInfoMapper wxUserInfoMapper;
	
    @SuppressWarnings("rawtypes")
	@Autowired
    private JedisPoolManager jedisPoolManager;

	@Override
	public int insertSelective(WxUserInfo wxUserInfo) {
		return wxUserInfoMapper.insertSelective(wxUserInfo);
	}

	@Override
	public int updateByPrimaryKey(WxUserInfo wxUserInfo) {
		return wxUserInfoMapper.updateByPrimaryKey(wxUserInfo);
	}
	
	@Override
	public int updateByPrimaryKeySelective(WxUserInfo wxUserInfo) {
		return wxUserInfoMapper.updateByPrimaryKeySelective(wxUserInfo);
	}

	@Override
	public WxUserInfo getUserByOpenId(String openId) {
		return wxUserInfoMapper.selectByPrimaryKey(openId);
	}

	@Override
	public WxUserInfo getUserByUserId(String userId) {
		return wxUserInfoMapper.selectWxUserInfoByUserId(userId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public WxUserInfo getUserByUnionid(String unionid) {
		WxUserInfo wxUser = (WxUserInfo) jedisPoolManager.getObject(unionid);
		if (wxUser==null) {
			wxUser = wxUserInfoMapper.selectWxUserInfoByUnionid(unionid);
			if (wxUser != null) {
				jedisPoolManager.putObject(unionid, wxUser, 60*60*24);
			}
		}
		return wxUser;
	}
}
