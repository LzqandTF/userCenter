package junit;

import com.yijiawang.web.platform.userCenter.dao.UserInterestMapper;
import com.yijiawang.web.platform.userCenter.dao.WxUserInfoMapper;
import com.yijiawang.web.platform.userCenter.po.UserInterest;
import com.yijiawang.web.platform.userCenter.po.WxUserInfo;
import com.yijiawang.web.platform.userCenter.service.UserInterestService;
import com.yijiawang.web.platform.userCenter.vo.InterestCountVO;
import com.yijiawang.web.platform.userCenter.vo.InterestListItemVO;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by xy on 16/4/6.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/spring.xml" })
public class UserInterestTest {

    @Autowired
    private UserInterestService userInterestService;
    @Autowired
    private WxUserInfoMapper wxUserInfoMapper;
    @Autowired
    private UserInterestMapper userInterestMapper;

    //@Test
    public void testSetUserInterest() {
        UserInterest userInterest = new UserInterest();

        userInterest.setUserId("456");
        userInterest.setStatus((byte)1);
        userInterest.setType((byte)1);
        userInterest.setEntityId("lot_00001");

        userInterestService.setUserInterest(userInterest);
    }

   //@Test
    public void testInterestList() {
        List<InterestListItemVO> list = userInterestService.getInterestList("12345", (byte)1, 0L, 20);
        if (list != null && list.size() > 0) {
            System.out.println(list.size());
        }
    }
    //@Test
    public void testlist() {
    	//List<WxUserInfo> list = userInterestService.getInterestListByEntityId("lot_00001", "1");
    	List list = wxUserInfoMapper.getInterestListByEntityId("lot_00001", (byte)1);
    	System.out.println();
    }

   // @Test
    public void testUserInterestStatus() {
        Byte status = userInterestService.getUserInterestStatus("123456", (byte)1, "lot_00001");
        System.out.println(status);
    }

    //@Test
    public void testInterestMeList() {
        List list = userInterestService.getInterestMeList("678", 0L, 20);
        System.out.println("  ");
    }
    @Test
    public void testInterestCount() {
    	InterestCountVO vo = userInterestMapper.getInterestCount("33");
    	System.out.println(vo);
    }
}
