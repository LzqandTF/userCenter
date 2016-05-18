package junit;

import com.yijiawang.web.platform.userCenter.service.UserService;
import com.yijiawang.web.platform.userCenter.vo.UserProtectQuestionVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xy on 16/5/12.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/spring.xml" })
public class UserTest {

    @Autowired
    private UserService userService;

    @Test
    public void getProtectQuestionTest() {
        String userId = "31229a9e544c431d9d9b294064ac1f2b";

        List<UserProtectQuestionVO> list = userService.getProtectQuestion(userId);

        System.out.println("  ");
    }

    @Test
    public void saveUserProtectInfoTest() {
        String userId = "4c572546b2ec481e96ccfa48d354ad39";
        String paypwd = "123456";
        List<UserProtectQuestionVO> list = new ArrayList<>();
        UserProtectQuestionVO vo1 = new UserProtectQuestionVO();
        vo1.setGroupId((byte)1);
        vo1.setQuestionId("q_1_2");
        vo1.setUserAnswer("哈哈哈");
        list.add(vo1);
        UserProtectQuestionVO vo2 = new UserProtectQuestionVO();
        vo2.setGroupId((byte)2);
        vo2.setQuestionId("q_2_4");
        vo2.setUserAnswer("kkkkkkkk");
        list.add(vo2);
        int result = userService.saveUserProtectInfo(userId, paypwd, list);
        System.out.println("   ");
    }

    @Test
    public void updateUserPayPasswordTest() {
        String userId = "31229a9e544c431d9d9b294064ac1f2b";
        String oldPassword = "e3ceb5881a0a1fdaad01296d7554868d";
        String newPassword = "e3ceb5881a0a1fdaad01296d7554868d";
        int result = userService.updateUserPayPassword(userId, oldPassword, newPassword);
        System.out.println("result=="+result);
    }
}
