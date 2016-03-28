package junit;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.yijiawang.web.platform.userCenter.po.WxUserInfo;
import com.yijiawang.web.platform.userCenter.service.WxUserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/spring.xml" })
public class WxUserInfoTest {
	@Autowired
	private WxUserService wxUserService;

	@Test
	public void main(){
		testWxUser();
	}
	
	public void testWxUser() {
		WxUserInfo user = wxUserService.getUserByOpenId("12345");
		System.out.println(user.getNickname());
	}

}
