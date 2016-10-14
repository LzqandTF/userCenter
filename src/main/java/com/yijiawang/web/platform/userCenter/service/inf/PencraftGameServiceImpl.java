package com.yijiawang.web.platform.userCenter.service.inf;

import com.yijiawang.web.platform.userCenter.dao.PencraftGameMapper;
import com.yijiawang.web.platform.userCenter.dao.PencraftGameVoteLogMapper;
import com.yijiawang.web.platform.userCenter.dao.PencraftGameVoteMapper;
import com.yijiawang.web.platform.userCenter.po.PencraftGame;
import com.yijiawang.web.platform.userCenter.po.PencraftGameVote;
import com.yijiawang.web.platform.userCenter.po.PencraftGameVoteLog;
import com.yijiawang.web.platform.userCenter.service.PencraftGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by yxd on 2016/9/21.
 */
@Service("pencraftGameService")
public class PencraftGameServiceImpl implements PencraftGameService {

    @Autowired
    private PencraftGameMapper pencraftGameMapper;
    @Autowired
    private PencraftGameVoteMapper pencraftGameVoteMapper;
    @Autowired
    private PencraftGameVoteLogMapper pencraftGameVoteLogMapper;

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
        List<PencraftGameVote> votes = pencraftGameVoteMapper.getVoters(page);
        if (votes != null && votes.size() > 0) {
            PencraftGameVoteLog log;
            for (PencraftGameVote vote : votes) {
                if (!StringUtils.isEmpty(voterUserId)) {
                    log = pencraftGameVoteLogMapper.selectByGameIdAndNum(vote.getUserGameId(), voterUserId);
                    if (log != null) {
                        vote.setVoteFlag(true);
                    } else {
                        vote.setVoteFlag(false);
                    }
                } else {
                    vote.setVoteFlag(true);
                }
            }
        }
        return votes;
    }

    @Override
    public Integer vote(String userNum, String voterUserId) {
        Integer res;

        // 获取投票信息
        PencraftGameVote pencraftGameVote = pencraftGameVoteMapper.getVoterByUserNum(userNum);
        if (pencraftGameVote != null) {

            // 验证此用户今天是否已经给当前选手投过票了
            PencraftGameVoteLog log = pencraftGameVoteLogMapper.selectByGameIdAndNum(pencraftGameVote.getUserGameId(), voterUserId);
            if (log != null) {
                return -1;
            }

            // 更新选手投票数
            PencraftGameVote upVote = new PencraftGameVote();
            upVote.setId(pencraftGameVote.getId());
            upVote.setVoteNum(pencraftGameVote.getVoteNum() + 1);
            upVote.setUpdateTime(new Date());
            res = pencraftGameVoteMapper.updateByPrimaryKeySelective(upVote);
            if (res > 0) {
                // 记录投票信息
                PencraftGameVoteLog insLog = new PencraftGameVoteLog();
                insLog.setPlayerUserId(pencraftGameVote.getUserId());
                insLog.setUserGameId(pencraftGameVote.getUserGameId());
                insLog.setVoterUserId(voterUserId);
                insLog.setCreateTime(new Date());
                res = pencraftGameVoteLogMapper.insert(insLog);
                if (res < 0) {
                    res = -4;
                } else {
                    res = upVote.getVoteNum();
                }
            } else {
                res = -3;
            }
        } else {
            res = -2;
        }
        return res;
    }

    @Override
    public List<PencraftGameVote> getTopList() {
        return pencraftGameVoteMapper.getTopList();
    }
}
