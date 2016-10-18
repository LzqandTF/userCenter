package com.yijiawang.web.platform.userCenter.service.inf;

import com.yijiawang.web.platform.userCenter.dao.PencraftGameMapper;
import com.yijiawang.web.platform.userCenter.po.PencraftGame;
import com.yijiawang.web.platform.userCenter.po.PencraftGameVote;
import com.yijiawang.web.platform.userCenter.service.PencraftGameService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by yxd on 2016/9/21.
 */
@Service("pencraftGameService")
public class PencraftGameServiceImpl implements PencraftGameService {

    @Autowired
    private PencraftGameMapper pencraftGameMapper;

    @Override
    public int add(PencraftGame param) {
        return pencraftGameMapper.insert(param);
    }

    @Override
    public PencraftGame selectByTel(String userTel) {
        return pencraftGameMapper.selectByTel(userTel);
    }

    @Override
    public PencraftGame selectByOpenId(String openId) {
        return pencraftGameMapper.selectByOpenId(openId);
    }

	@Override
	public List<PencraftGameVote> getVoters(Integer page, String voterUserId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer vote(String userNum, String voterUserId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PencraftGameVote> getTopList() {
		// TODO Auto-generated method stub
		return null;
	}
}
