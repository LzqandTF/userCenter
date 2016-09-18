package junit;


import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.yijiawang.web.platform.userCenter.po.UserInfo;
import com.yijiawang.web.platform.userCenter.po.WxUserInfo;
import com.yijiawang.web.platform.userCenter.service.UserService;
import com.yijiawang.web.platform.userCenter.service.WxUserService;
import com.yijiawang.web.platform.userCenter.vo.XUserVO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/spring.xml" })
public class WxUserInfoTest {
	@Autowired
	private WxUserService wxUserService;
	@Autowired
	private UserService userService;

	//@Test
	public void main(){
		testWxUser();
	}
	
	public void testWxUser() {
	
	}

	//@Test
	public void testSelectWxuserByUserId() {
		WxUserInfo wxUserInfo = wxUserService.getUserByUserId("12345");
		System.out.println("   ");
	}
	@Test
	public void testxuser(){
	userService.changeUserStatusById("11","222","0");
	}
	

}
