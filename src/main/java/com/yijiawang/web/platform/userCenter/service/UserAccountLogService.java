package com.yijiawang.web.platform.userCenter.service;

import java.util.List;

/**
 * Created by xy on 16/5/30.
 * 用于异步记录用户流水, 用于账户变化的日志服务
 */
public interface UserAccountLogService {

    void asyncLoggerUserAccount(List<String> logObject);

}
