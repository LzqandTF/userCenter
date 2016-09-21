package com.yijiawang.web.platform.userCenter.service.inf;

import com.yijiawang.web.platform.userCenter.dao.PencraftGameMapper;
import com.yijiawang.web.platform.userCenter.po.PencraftGame;
import com.yijiawang.web.platform.userCenter.service.PencraftGameService;
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
}
