package junit;

import com.yijiawang.web.platform.userCenter.dao.WxUserInfoMapper;
import com.yijiawang.web.platform.userCenter.param.AccountCheckParam;
import com.yijiawang.web.platform.userCenter.po.AccountCheck;
import com.yijiawang.web.platform.userCenter.po.PencraftGame;
import com.yijiawang.web.platform.userCenter.po.PencraftGameVote;
import com.yijiawang.web.platform.userCenter.service.PencraftGameService;
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

import java.util.*;

/**
 * Created by xy on 16/5/12.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/spring.xml"})
public class UserTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserInsurePriceService userInsurePriceService;
    @Autowired
    private WxUserInfoMapper userInfoMapper;
    @Autowired
    private UserLevelService userLevelService;


    @Autowired
    private PencraftGameService pencraftGameService;

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
        vo1.setGroupId((byte) 1);
        vo1.setQuestionId("q_1_2");
        vo1.setUserAnswer("哈哈哈");
        list.add(vo1);
        UserProtectQuestionVO vo2 = new UserProtectQuestionVO();
        vo2.setGroupId((byte) 2);
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
        System.out.println("result==" + result);
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
        Map contractMap = (Map) configMap.get("contract");
        System.out.println(configMap);
        System.out.println(contractMap);
    }

    @Test
    public void testAddPen() {
        // 添加报名信息
        PencraftGame ins = new PencraftGame();
        ins.setOpenId("o027xw-elQrTV_8diCB8kZcwus18");
        ins.setUserId("00011609201447vMKLU1dB");
        ins.setUserGameId(generateShortUUID());
        ins.setUserName("袁晓东");
        ins.setUserTel("18810024193");
        ins.setUserIntro("我是袁晓东啊，就是袁晓东啊");
        ins.setWorksName("八骏图");
        ins.setTranslation("八骏图八骏图八骏图八骏图八骏图八骏图八骏图八骏图八骏图八骏图八骏图");
        pencraftGameService.add(ins);

    }

    @Test
    public void testGetVoters() {
        List<PencraftGameVote> votes = pencraftGameService.getVoters(0, "");
        System.out.println("");
    }

    @Test
    public void testGetTopList() {
        List<PencraftGameVote> votes = pencraftGameService.getTopList();
        System.out.println("");
    }

    @Test
    public void testVote() {
        Long c = pencraftGameService.vote("02", "userid_0001");
        System.out.println("返回结果：" + c);
    }

    public static String[] chars = new String[]{"a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"};

    /**
     * 生成短8位uuid
     *
     * @return
     */
    public static String generateShortUUID() {
        StringBuffer shortBuffer = new StringBuffer();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(chars[x % 0x3E]);
        }
        return shortBuffer.toString();
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

    @Test
    public void testByte() {
        Byte a = 1;
        Byte b = 1;
        System.out.println(a == b);
        System.out.println(a.intValue() == b.intValue());

        Integer c = 30;
        Integer d = 4;
        Integer e = 2;
        System.out.println(c / d);
        System.out.println((c / d) * e);
        System.out.println(Integer.valueOf((c / d) * e));

    }

    @Test
    public void test1() {
        int illegalCount = 4;
        illegalCount = illegalCount % 3 + 1;
        System.out.println(illegalCount);
        illegalCount = 2;
        illegalCount = illegalCount % 3 == 0 ? 3 : illegalCount % 3;
        System.out.println(illegalCount);
        illegalCount = 5;
        illegalCount = illegalCount % 3 == 0 ? 3 : illegalCount % 3;
        System.out.println(illegalCount);
        illegalCount = 6;
        illegalCount = illegalCount % 3 == 0 ? 3 : illegalCount % 3;
        System.out.println(illegalCount);
    }
}
