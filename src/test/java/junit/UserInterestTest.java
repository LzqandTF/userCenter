package junit;

import com.yijiawang.web.platform.userCenter.po.UserInterest;
import com.yijiawang.web.platform.userCenter.service.UserInterestService;
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

    //@Test
    public void testSetUserInterest() {
        UserInterest userInterest = new UserInterest();

        userInterest.setUserId("456");
        userInterest.setStatus("0");
        userInterest.setType("1");
        userInterest.setEntityId("lot_00001");

        userInterestService.setUserInterest(userInterest);
    }

   // @Test
    public void testInterestList() {
        List<InterestListItemVO> list = userInterestService.getInterestList("456", "1");
        if (list != null && list.size() > 0) {
            System.out.println(list.size());
        }
    }
    @Test
    public void testlist() {
    	List<UserInterest> list = userInterestService.getInterestListByEntityId("lot_00001", "1");
    	System.out.println();
    }

}
