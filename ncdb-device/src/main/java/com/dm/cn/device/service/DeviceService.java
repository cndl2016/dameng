package com.dm.cn.device.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dm.cn.common.param.DeviceMonitorParam;
import com.dm.cn.common.param.DeviceParam;
import com.dm.cn.common.param.EnableParam;
import com.dm.cn.device.entity.server.Device;
import com.dm.cn.device.entity.vo.DeviceInstanceNum;
import com.dm.cn.device.entity.vo.DeviceMonitorInfoVO;
import com.dm.cn.device.entity.vo.DeviceTopChartVO;

import java.util.List;
import java.util.Map;

/**
 * 针对【T_DEVICE（设备表）】的数据库操作Service
 *
 * @author dameng
 * @date 2024/10/15
 */
public interface DeviceService extends IService<Device> {

    /**
     * 查询设备列表
     *
     * @param device 设备查询参数
     * @return {@link IPage}<{@link Device}>
     */
    IPage<Device> getListDevice(DeviceParam device);

    /**
     * 设备启用禁用
     *
     * @param enableParam 启停用参数
     * @return boolean
     * @author cn 2024/07/10
     */
    boolean enableDevice(EnableParam enableParam);

    /**
     * 删除设备 设备id
     *
     * @param id 设备id
     * @return boolean
     * @author cn 2024/07/10
     */
    boolean deleteDeviceById(Long id);

    /**
     * 保存或更新设备
     *
     * @param device 设备信息
     * @return boolean
     * @author cn 2024/07/10
     */
    boolean saveOrUpdateDevice(Device device);

    /**
     * 获取设备监控列表
     *
     * @param param 查询参数
     * @return {@link List}<{@link DeviceMonitorInfoVO}>
     * @author cn 2024/07/11
     */
    List<DeviceMonitorInfoVO> getMonitorList(DeviceMonitorParam param);

    /**
     * 根据设备节点id查询设备所属组织全路径
     *
     * @param deviceNodeId 设备节点id
     * @return {@link String}
     */
    String getDevicePath(long deviceNodeId);

    /**
     * 根据当前设备节点id，查询其所有后代节点的id
     *
     * @param deviceNodeId 设备节点id
     * @param isH2         数据源是否为h2
     * @return {@link List}<{@link String}>
     */
    List<String> getChildIdList(long deviceNodeId, boolean isH2);

    /**
     * 检查当前设备是否已经存在端口
     *
     * @param ip   设备ip
     * @param port 设备端口
     * @return boolean
     */
    boolean checkPortIsExist(String ip, Integer port);

    /**
     * 重复多次检查当前设备是否已经存在端口
     *
     * @param ip   设备ip
     * @param port 设备端口
     * @return boolean
     */
    boolean checkPortIsExistRetry(String ip, Integer port);

    /**
     * 检查进程是否存在
     *
     * @param ip       设备ip
     * @param fileName INI文件名称
     * @return boolean
     */
    boolean checkProgressIsExist(String ip, String fileName);

    /**
     * 添加节点 下拉列表设备信息
     * 获取连接正常，且启用中的设备
     *
     * @return {@link List}<{@link String}>
     */
    List<String> getNodeDeviceIpList();

    /**
     * 检查指定的设备是否被禁用
     *
     * @param deviceList 设备ip列表
     */
    void verifyDeviceInitMessage(List<String> deviceList);

    /**
     * 检查IP
     *
     * @param deviceList 设备IP列表
     * @param ipStart    起始ip
     * @param ipEnd      终止ip
     * @param deviceRoom 机房信息
     * @return {@link List}<{@link String}>
     */
    List<String> verifyIpInitMessage(List<String> deviceList, String ipStart, String ipEnd, Long... deviceRoom);

    /**
     * 校验端口范围
     *
     * @param portStart 起始端口
     * @param portEnd   终止端口
     */
    void verifyPortInitMessage(Integer portStart, Integer portEnd);

    /**
     * 获取设备节点信息
     *
     * @param deviceList ip 范围
     * @return {@link List}<{@link Device}>
     */
    List<Device> getDeviceIpInfoList(List<String> deviceList);

    /**
     * 首页获取设备前十的信息
     *
     * @return {@link DeviceTopChartVO}
     */
    DeviceTopChartVO getDeviceTopChart();

    /**
     * 测试连接
     *
     * @param deviceList 设备列表
     * @return boolean
     */
    boolean testPing(List<Device> deviceList);
}