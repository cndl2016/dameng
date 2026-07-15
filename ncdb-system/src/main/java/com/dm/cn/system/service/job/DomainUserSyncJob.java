package com.dm.cn.system.service.job;

import com.dm.cn.common.core.domain.SysUser;
import com.dm.cn.common.core.exception.ServiceException;
import com.dm.cn.common.core.utils.SpringUtils;
import com.dm.cn.system.entity.param.SyncUserParam;
import com.dm.cn.system.service.ISysUserService;
import com.dm.cn.system.service.SysDomainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.naming.NamingException;
import java.util.stream.Collectors;

/**
 * 域用户信息同步任务
 *
 * @author dameng
 * @date 2023/05/16
 */
@Component
public class DomainUserSyncJob {

    private static final Logger log = LoggerFactory.getLogger(DomainUserSyncJob.class);

    /**
     * 用户同步任务（quartz逻辑调用）
     *
     * @param jobParam 任务参数
     */
    public void userSyncJob(String jobParam) {
        try {
            Long domainId = Long.valueOf(jobParam);
            SyncUserParam param = new SyncUserParam();
            param.setDomainId(domainId);
            param.setUserIdList(SpringUtils.getBean(ISysUserService.class).getSyncUserList(domainId)
                    .stream().map(SysUser::getUserId).collect(Collectors.toList()));
            SpringUtils.getBean(SysDomainService.class).syncDomainUser(param);
        } catch (NamingException e) {
            log.error("DomainUserSyncJob exception: {}", e);
            throw new ServiceException(e.getMessage());
        }
    }
}
