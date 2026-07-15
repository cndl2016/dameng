package com.dm.cn.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dm.cn.base.entity.server.SysJobLog;
import com.dm.cn.common.core.web.domain.AjaxResult;
import com.dm.cn.common.param.DeviceMonitorParam;
import com.dm.cn.device.entity.vo.DeviceMonitorInfoVO;

/**
 * 针对【SYS_JOB_LOG（定时任务调度日志表）】的数据库操作Service
 *
 * @author dameng
 * @date 2024/10/18
 */
public interface SysJobLogService extends IService<SysJobLog> {

    /**
     * 获取设备监控Echart数据
     *
     * @param param 查询参数
     * @return {@link AjaxResult}
     * @author cn 2024/07/11
     */
    DeviceMonitorInfoVO getMonitorDataForEchart(DeviceMonitorParam param);

    /**
     * 清除日志记录(7天之前的)
     */
    void clearExpiredLog();

}