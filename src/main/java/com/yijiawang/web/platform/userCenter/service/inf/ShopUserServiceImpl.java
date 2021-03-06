package com.yijiawang.web.platform.userCenter.service.inf;

import com.yijiawang.web.platform.userCenter.cache.JedisPoolManager;
import com.yijiawang.web.platform.userCenter.cache.UserCacheNameSpace;
import com.yijiawang.web.platform.userCenter.dao.UserInfoMapper;
import com.yijiawang.web.platform.userCenter.dao.WxUserInfoMapper;
import com.yijiawang.web.platform.userCenter.po.UserInfo;
import com.yijiawang.web.platform.userCenter.po.WxUserInfo;
import com.yijiawang.web.platform.userCenter.service.ShopUserService;
import com.yijiawang.web.platform.userCenter.service.UserLevelService;
import com.yijiawang.web.platform.userCenter.util.AppConfig;
import com.yijiawang.web.platform.userCenter.util.JsonUtils;
import com.yijiawang.web.platform.userCenter.vo.UserLevelVO;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xy on 2016/12/15.
 */
@Service("shopUserService")
public class ShopUserServiceImpl implements ShopUserService {

    private org.apache.commons.logging.Log log = LogFactory.getLog(UserServiceImpl.class);

    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private WxUserInfoMapper wxUserInfoMapper;
    @Autowired
    private JedisPoolManager jedisPoolManager;
    @Autowired
    private UserLevelService userLevelService;

    @Override
    public String setUser2ShopRedis(String openId, String userId) {

        UserInfo userInfo = null;
        WxUserInfo wxUserInfo = null;
        if (openId != null && openId.length() > 0) {
            userInfo = userInfoMapper.getUserByOpenId(openId);
            wxUserInfo = wxUserInfoMapper.selectWxUserInfoByOpenId(openId);
        } else if (userId != null && userId.length() > 0) {
            userInfo = userInfoMapper.getUserByUserId(userId);
            wxUserInfo = wxUserInfoMapper.selectWxUserInfoByUserId(userId);
        }
        if (wxUserInfo != null && userInfo != null) {
            String key = String.format(UserCacheNameSpace.USER_DATA_SHOP_KEY, wxUserInfo.getOpenId());
            Map<String,Object> map = new HashMap<>();
            map.put("wxUserInfo", wxUserInfo);
            map.put("userInfo", userInfo);
            UserLevelVO buyer = userLevelService.getUserLevelBuyer(wxUserInfo.getUserId());
            map.put("buyer", buyer);
            UserLevelVO saler = userLevelService.getUserLevelSaler(wxUserInfo.getUserId());
            map.put("saler", saler);
            String json = JsonUtils.convertToStr(map);
            jedisPoolManager.set(key, json);
            jedisPoolManager.expire(key, UserCacheNameSpace.USER_DATA_SHOP_KEY_EXPIRE_SENCONDS);
            notifyShop(wxUserInfo.getUserId());
            return json;
        }
        return null;
    }

    private void notifyShop(String userId) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpget = new HttpGet(AppConfig.getString("vipuserurl") + userId);
            httpclient.execute(httpget);
        } catch (Exception e) {
            log.error(e);
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                log.error(e);
            }
        }
    }

}
