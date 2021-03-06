package com.yijiawang.web.platform.userCenter.service.inf;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yijiawang.web.platform.userCenter.dao.InsurePriceInfoMapper;
import com.yijiawang.web.platform.userCenter.po.InsurePriceInfo;
import com.yijiawang.web.platform.userCenter.service.UserInsurePriceService;

@Service("userInsurePriceService")
public class UserInsurePriceServiceImpl implements UserInsurePriceService{
	@Autowired
	private InsurePriceInfoMapper insurePriceInfoMapper;
	
	@Override
	public boolean isPayInsurePrice(String openId, String lotId) {	
		InsurePriceInfo insurePriceInfo = insurePriceInfoMapper.selectByOpenIdAndLotId(openId, lotId);
		return insurePriceInfo == null? false : true;
	}

	@Override
	public Long selectLotingInsurePriceSum() {
		return insurePriceInfoMapper.selectLotingInsurePriceSum();
	}

	@Override
	public List<InsurePriceInfo> getInsurePriceListByLotId(String lotId,
			Integer type) {
		return insurePriceInfoMapper.getInsurePriceListByLotId(lotId, type);
	}

	@Override
	public int updateInsurePrice(String lotId, String openId) {
		return insurePriceInfoMapper.updateInsurePrice(lotId, openId);
	}

    @Override
    public InsurePriceInfo getLotInsurePriceByUserId(String lotId, String userId) {
        return insurePriceInfoMapper.getLotInsurePriceByUserId(lotId, userId);
    }
}
