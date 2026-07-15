package com.dm.cn.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dm.cn.common.param.DeviceMonitorParam;
import com.dm.cn.common.param.DeviceParam;
import com.dm.cn.device.entity.server.Device;
import com.dm.cn.device.entity.vo.DeviceInstanceNumVO;
import com.dm.cn.device.entity.vo.DeviceNodeCountVO;
import com.dm.cn.device.entity.vo.SysJobLogVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 针对【T_DEVICE（设备表）】的数据库操作Mapper
 *
 * @author dameng
 * @date 2024/10/15
 */
public interface DeviceMapper extends BaseMapper<Device> {


    /**
     * 查询设备分页列表
     *
     * @param page      分页对象
     * @param param     查询参数
     * @param beginTime 查询起始时间
     * @param endTime   查询终止时间
     * @return {@link IPage}<{@link Device}>
     */
    IPage<Device> getDeviceList(IPage<Device> page, @Param("params") DeviceParam param, @Param("beginTime") String beginTime, @Param("endTime") String endTime);

    /**
     * 获取设备监控列表
     *
     * @param param 查询参数
     * @return {@link List}<{@link Device}>
     * @author cn 2024/07/10
     */
    List<Device> getMonitorList(@Param("params") DeviceMonitorParam param);

    /**
     * 根据设备节点id查询设备所属组织全路径
     *
     * @param deviceNodeId 设备节点id
     * @param isH2         数据源是否为h2
     * @return {@link String}
     */
    String getDevicePath(@Param("deviceNodeId") long deviceNodeId, @Param("isH2") boolean isH2);

    /**
     * 根据当前设备节点id，查询其所有后代节点的id
     *
     * @param deviceNodeId 设备节点id
     * @param isH2         数据源是否为h2
     * @return {@link List}<{@link String}>
     */
    List<String> getChildIdList(@Param("deviceNodeId") long deviceNodeId, @Param("isH2") boolean isH2);

    /**
     * 获取实例节点添加时 设备下拉列表数据
     *
     * @return yth 2024/06/24
     */
    List<Device> getNodeDeviceList();

    /**
     * 获取设备监控列表
     *
     * @param deviceIp 设备ip
     * @return
     */
    List<Device> getDeviceByIp(@Param("deviceIp") String deviceIp);

    /**
     * 获取设备节点信息
     *
     * @param deviceList ip 范围
     * @return {@link List}<{@link Device}>
     */
    List<Device> getDeviceIpInfoList(@Param("deviceList") List<String> deviceList);

    /**
     * 根据告警项和在线设备获取前十设备信息
     *
     * @param deviceId 在线设备(这里，实际上传入的是ip)
     * @return
     */
    List<SysJobLogVO> getDeviceMessageById(@Param("deviceId") Long deviceId);

}