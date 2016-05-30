package com.yijiawang.web.platform.userCenter.service.impl;

import com.yijiawang.web.platform.userCenter.service.UserAccountLogService;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by xy on 16/5/30.
 */
public class UserAcountLogServiceImpl implements UserAccountLogService {

    private static ExecutorService logUserAcountPool = new ThreadPoolExecutor(30, Integer.MAX_VALUE, 0L, TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>(), new ThreadPoolExecutor.CallerRunsPolicy());

    private Logger logger = Logger.getLogger("accountCheck");

    @Override
    public void asyncLoggerUserAccount(final List<String> logObject) {
        logUserAcountPool.execute(new Runnable() {
            @Override
            public void run() {
                for (String log : logObject) {
                    logger.info(log);
                }
            }
        });
    }
}
