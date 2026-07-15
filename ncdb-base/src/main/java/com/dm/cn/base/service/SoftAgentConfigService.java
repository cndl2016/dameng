package com.dm.cn.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dm.cn.base.entity.server.SoftAgentConfig;
import com.dm.cn.common.param.SoftAgentConfigParam;

/**
 * @author dameng
 * @description 针对表【SYS_SOFT_AGENT_CONFIG(数据采集频率配置表)】的数据库操作Service
 * @createDate 2025-01-24 10:19:52
 */
public interface SoftAgentConfigService extends IService<SoftAgentConfig> {

    /**
     * 采集频率分页查询查询
     *
     * @param param 查询参数
     * @return boolean
     */
    IPage<SoftAgentConfig> getPage(SoftAgentConfigParam param);

    /**
     * 修改采集频率
     *
     * @param softAgentConfig 采集频率对象
     * @return
     */
    boolean update(SoftAgentConfig softAgentConfig);

}