package com.dm.cn.system.config.security;

import com.dm.cn.common.core.constant.NumberConstants;
import com.dm.cn.common.core.constant.TokenConstants;
import com.dm.cn.common.core.utils.ServletUtils;
import com.dm.cn.common.core.utils.SpringUtils;
import com.dm.cn.common.core.utils.StringUtils;
import com.dm.cn.common.utils.LogUtils;
import com.dm.cn.common.utils.ip.AddressUtils;
import com.dm.cn.common.utils.ip.IpUtils;
import com.dm.cn.system.entity.server.SysLogininfor;
import com.dm.cn.system.entity.server.SysOperLog;
import com.dm.cn.system.service.ISysLoginInforService;
import com.dm.cn.system.service.ISysOperLogService;
import eu.bitwalker.useragentutils.UserAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

/**
 * 异步工厂（产生任务用）
 *
 * @author dameng.cn
 */
public class AsyncFactory {
    private static final Logger log = LoggerFactory.getLogger(AsyncFactory.class);

    /**
     * 记录登录信息
     *
     * @param username 用户名
     * @param status   状态
     * @param message  消息
     * @param args     列表
     * @return 任务task
     */
    public static TimerTask recordLogininfor(final String username, final String status, final String message,
                                             final Object... args) {
        final UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
        final String ip = IpUtils.getIpAddr();
        return new TimerTask() {
            @Override
            public void run() {
                String address = AddressUtils.getRealAddressByIp(ip);
                StringBuilder s = new StringBuilder();
                s.append(LogUtils.getBlock(ip));
                s.append(address);
                s.append(LogUtils.getBlock(username));
                s.append(LogUtils.getBlock(status));
                s.append(LogUtils.getBlock(message));
                // 打印信息到日志
                log.info(s.toString(), args);
                // 获取客户端操作系统
                String os = userAgent.getOperatingSystem().getName();
                // 获取客户端浏览器
                String browser = userAgent.getBrowser().getName();
                // 封装对象
                SysLogininfor loginInfo = new SysLogininfor();
                loginInfo.setUserName(username);
                loginInfo.setIpaddr(ip);
                loginInfo.setLoginLocation(address);
                loginInfo.setBrowser(browser);
                loginInfo.setOs(os);
                loginInfo.setMsg(message);
                // 日志状态
                if (StringUtils.equalsAny(status, TokenConstants.LOGIN_SUCCESS, TokenConstants.LOGOUT, TokenConstants.REGISTER)) {
                    loginInfo.setStatus(NumberConstants.ZERO_STRING);
                } else if (TokenConstants.LOGIN_FAIL.equals(status)) {
                    loginInfo.setStatus(NumberConstants.ONE_STRING);
                }
                // 插入数据
                SpringUtils.getBean(ISysLoginInforService.class).insertLoginInfor(loginInfo);
            }
        };
    }

    /**
     * 操作日志记录
     *
     * @param operLog 操作日志信息
     * @return 任务task
     */
    public static TimerTask recordOper(final SysOperLog operLog) {
        return new TimerTask() {
            @Override
            public void run() {
                SpringUtils.getBean(ISysOperLogService.class).insertOperlog(operLog);
            }
        };
    }
}
