package com.dm.cn.device.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dm.cn.common.core.web.domain.AjaxResult;
import com.dm.cn.common.param.DeviceNodeParam;
import com.dm.cn.device.entity.param.RegionParam;
import com.dm.cn.device.entity.server.DeviceNode;
import com.dm.cn.device.entity.vo.DeviceNodeVO;
import com.dm.cn.device.entity.vo.DeviceResourceVo;

import java.util.List;

/**
 * 针对【T_DEVICE_NODE（设备节点表）】的数据库操作Service
 *
 * @author dameng
 * @date 2024/10/15
 */
public interface DeviceNodeService extends IService<DeviceNode> {

    /**
     * 设备节点树查询
     *
     * @param param 设备节点树查询参数（根节点、架构类型）
     * @return {@link List}<{@link DeviceNodeVO}>
     */
    List<DeviceNodeVO> getDeviceNodeTree(DeviceNodeParam param);

    /**
     * 保存或修改设备节点
     *
     * @param vo 设备节点视图对象
     * @return boolean
     */
    boolean saveOrUpdateDeviceNode(DeviceNodeVO vo);

    /**
     * 删除设备节点
     *
     * @param id 设备节点id
     */
    void deleteDeviceNode(Long id);

    /**
     * 获取设备监控节点树
     *
     * @param param 查询参数
     * @return {@link AjaxResult}
     * @author cn 2024/07/11
     */
    List<DeviceNodeVO> getDeviceMonitorTree(DeviceNodeParam param);

    /**
     * 在线设备节点树查询
     *
     * @param param 查询参数
     * @return {@link List}<{@link DeviceNodeVO}>
     */
    DeviceResourceVo getDeviceOnlineTree(DeviceNodeParam param);

    /**
     * 地域节点树查询
     *
     * @param param 地域节点树查询参数
     * @return {@link List}<{@link DeviceNodeVO}>
     */
    List<DeviceNodeVO> getRegionTree(RegionParam param);
}
