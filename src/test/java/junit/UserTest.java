package junit;

import com.yijiawang.web.platform.userCenter.dao.WxUserInfoMapper;
import com.yijiawang.web.platform.userCenter.param.AccountCheckParam;
import com.yijiawang.web.platform.userCenter.po.AccountCheck;
import com.yijiawang.web.platform.userCenter.service.UserInsurePriceService;
import com.yijiawang.web.platform.userCenter.service.UserLevelService;
import com.yijiawang.web.platform.userCenter.service.UserService;
import com.yijiawang.web.platform.userCenter.type.PayType;
import com.yijiawang.web.platform.userCenter.type.TradeType;
import com.yijiawang.web.platform.userCenter.vo.UserProtectQuestionVO;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xy on 16/5/12.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/spring.xml" })
public class UserTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserInsurePriceService userInsurePriceService;
    @Autowired
    private WxUserInfoMapper userInfoMapper;
    @Autowired
    private UserLevelService userLevelService;

    //@Test
    public void getProtectQuestionTest() {
        List<String> openIds = userInfoMapper.getAllOpenId(1);
        System.out.println(openIds);
    }

    //@Test
    public void saveUserProtectInfoTest() {
        String userId = "31229a9e544c431d9d9b294064ac1f2b";
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

    //@Test
    public void updateUserPayPasswordTest() {
        String userId = "31229a9e544c431d9d9b294064ac1f2b";
        String oldPassword = "e3ceb5881a0a1fdaad01296d7554868d";
        String newPassword = "e3ceb5881a0a1fdaad01296d7554868d";
        int result = userService.updateUserPayPassword(userId, oldPassword, newPassword);
        System.out.println("result=="+result);
    }

    //@Test
    public void testAddAccountCheck() {
        Map<String, String> param = new HashMap<>();
        param.put("tran_id", "123456789");
        param.put("open_id", "o55jRw-AL3H2sUmRdv6u8ocdxn3U");
        param.put("title", "保证金!!!");
        param.put("trade_type", TradeType.INSURE_PAY.value().toString());
        param.put("trade_amount", "1");
        param.put("pay_type", PayType.WEIXIN.value().toString());
        param.put("lot_id", "5dda2035c980412c99dfc1e78848aecc");
        long result = userService.addAccountCheck(param);
        System.out.println("  ");
    }

    //@Test
    public void verifyAccountCheck() {
        AccountCheckParam param = new AccountCheckParam();
        // 流水号
        param.setTranId("4006972001201606288026169226");
        // 资金流向
        param.setType(0);
        // 金额
        Double amount = Double.parseDouble("2.00");
        param.setTradeAmount(amount.intValue() * 100);
        // 交易类型
        List<Integer> tradeTypeList = new ArrayList<>();

        tradeTypeList.add(TradeType.TOPUP.value());
        tradeTypeList.add(TradeType.ORDER_PAY.value());
        tradeTypeList.add(TradeType.INSURE_PAY.value());
        param.setTradeTypeList(tradeTypeList);
        // 验证平台账目表中是否有此项交易记录
        List<AccountCheck> accountCheckList = userService.verifyAccountCheck(param);

        System.out.println("  ");
    }

    //@Test
    public void selectAllBalanceSumTest() {
        System.out.println("用户余额总额：" + userService.selectAllBalanceSum());
    }

   // @Test
    public void selectAllFrozenMoneySumTest() {
        System.out.println("用户冻结总额：" + userService.selectAllFrozenMoneySum());
    }

    //@Test
    public void selectLotingInsurePriceSumTest() {
        System.out.println("正在热拍拍品保证金总额：" + userInsurePriceService.selectLotingInsurePriceSum());
    }

    @Test
    public void testUserLevel() {
        //userLevelService.initUserLevelBuyer();
        System.out.println(userLevelService.getUserLevelBuyer("122344"));
        userLevelService.initUserLevelSaler();
        System.out.println(userLevelService.getUserLevelSaler("122344"));
    }

    @Test
    public void testjson() {
        String json = "{\"contract\":{\"switch\":1,\"type\":1},\"returnGoods\":{\"switch\":1,\"type\":0}}";
        ObjectMapper mapper = new ObjectMapper();
        Map configMap = null;
        try {
            configMap = mapper.readValue(json, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map contractMap = (Map)configMap.get("contract");
        System.out.println(configMap);
        System.out.println(contractMap);
    }
    
    @Test
    public void testDoubleAndInteger() {
        String a = "0.2";
        System.out.println(a);
        double ab = Double.valueOf(a);
        System.out.println(ab);
        Integer c = 25;
        System.out.println(c);
        System.out.println(new Double(ab * c));
        System.out.println(new Double(ab * c).intValue());
        System.out.println(new Double(3.2).intValue());
        System.out.println(new Double(3.6).intValue());
        
    }
}
