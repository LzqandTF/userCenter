package com.yijiawang.web.platform.userCenter.dao;

import java.util.List;

import com.yijiawang.web.platform.userCenter.po.InsurePriceInfo;
import com.yijiawang.web.platform.userCenter.po.UserInfo;

import org.apache.ibatis.annotations.Param;

public interface InsurePriceInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(InsurePriceInfo record);

    int insertSelective(InsurePriceInfo record);

    InsurePriceInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(InsurePriceInfo record);

    int updateByPrimaryKey(InsurePriceInfo record);

    //------ 自定义操作
    int updateInsurePriceInfoStatus(@Param("userId") String userId, @Param("lotId") String lotId, @Param("status") Byte status);

    UserInfo getLotSalerInfo(String lotId);

    InsurePriceInfo selectByTranId(String tranId);
    
    InsurePriceInfo selectByOpenIdAndLotId(@Param("openId")String openId, @Param("lotId")String lotId);
    
    List<InsurePriceInfo> getInsurePriceListByLotId(@Param("lotId")String lotId, @Param("type")Integer type);
    
    int updateInsurePrice(@Param("lotId")String lotId, @Param("openId")String openId);

    /**
     * 获取正在热拍拍品保证金总额
     */
    Long selectLotingInsurePriceSum();
}