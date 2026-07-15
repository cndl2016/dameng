package com.dm.cn.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dm.cn.base.entity.model.DeviceNormal;
import com.dm.cn.base.entity.server.AlertConfig;

import java.util.List;

/**
 * 告警规则配置表数据访问对象
 *
 * @author Auto-Coder
 * @date 2023-03-16
 */
public interface AlertConfigMapper extends BaseMapper<AlertConfig> {

    /**
     * 获得所有设备和数据库节点
     *
     * @return list
     */
    List<DeviceNormal> getAllDevices();

}