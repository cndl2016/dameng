package com.dm.cn.device.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dm.cn.common.config.NcdbApiConfig;
import com.dm.cn.common.core.constant.Constants;
import com.dm.cn.common.core.constant.MgConstants;
import com.dm.cn.common.core.constant.NumberConstants;
import com.dm.cn.common.core.constant.SymbolConstants;
import com.dm.cn.common.core.exception.ServiceException;
import com.dm.cn.common.core.utils.IdWorker;
import com.dm.cn.common.core.utils.bean.DbTypeBean;
import com.dm.cn.common.core.web.domain.AjaxResult;
import com.dm.cn.common.enums.DeviceEnum;
import com.dm.cn.common.param.DeviceNodeParam;
import com.dm.cn.common.security.utils.SecurityUtils;
import com.dm.cn.common.utils.I18nMessageUtil;
import com.dm.cn.device.entity.param.RegionParam;
import com.dm.cn.device.entity.server.Device;
import com.dm.cn.device.entity.server.DeviceNode;
import com.dm.cn.device.entity.vo.DeviceNodeVO;
import com.dm.cn.device.entity.vo.DeviceResourceVo;
import com.dm.cn.device.mapper.DeviceNodeMapper;
import com.dm.cn.device.service.DeviceNodeService;
import com.dm.cn.device.service.DeviceService;
import com.dm.cn.device.service.SshdService;
import com.dm.cn.system.constant.MessageTipConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * 针对【T_DEVICE_NODE（设备节点表）】的数据库操作Service实现
 *
 * @author dameng
 * @date 2024/10/15
 */
@Service
public class DeviceNodeServiceImpl extends ServiceImpl<DeviceNodeMapper, DeviceNode>
        implements DeviceNodeService {

    private static final Logger log = LoggerFactory.getLogger(DeviceNodeServiceImpl.class);

    @Resource
    private DbTypeBean dbTypeBean;

    @Resource
    private DeviceService deviceService;

    @Resource
    private SshdService sshdService;

    @Resource
    private NcdbApiConfig config;

    /**
     * 设备节点树查询
     *
     * @param param 设备节点树查询参数（根节点、架构类型）
     * @return {@link List}<{@link DeviceNodeVO}>
     */
    @Override
    public List<DeviceNodeVO> getDeviceNodeTree(DeviceNodeParam param) {
        // 校验根节点是否为空
        if (ObjectUtils.isEmpty(param.getRootId())) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.COMMON_TREE_ROOT_NOT_NULL));
        }
        List<DeviceNodeVO> tree = new ArrayList<>();
        // 获取设备节点列表
        List<DeviceNode> deviceNodeList = lambdaQuery().list();
        // 获取其父节点为参数中根节点的设备节点
        DeviceNode root = deviceNodeList.stream().filter(x -> param.getRootId().equals(x.getUpperId())).findFirst().orElse(null);
        // 获取设备列表
        List<Device> deviceList = deviceService.lambdaQuery().list();
        if (root != null) {
            // 创建视图对象根节点并复制填充根节点信息
            DeviceNodeVO rootVo = new DeviceNodeVO();
            BeanUtils.copyProperties(root, rootVo);
            // 根节点层级为1
            rootVo.setLevel(NumberConstants.ONE);
            // 设备节点id全路径
            rootVo.setFullIdPath(String.valueOf(rootVo.getId()));
            // 设备节点名称全路径
            rootVo.setFullNamePath(rootVo.getNodeName());
            // 创建剩余设备节点的节点树结构
            createNodeTree(rootVo, deviceNodeList, deviceList);
            // 国际化
            String message = I18nMessageUtil.getMessage(MessageTipConstant.GLOBAL_RESOURCE);
            rootVo.setNodeName(message);
            tree.add(rootVo);
        }
        return tree;
    }

    /**
     * 保存或修改设备节点
     *
     * @param vo 设备节点视图对象
     * @return boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdateDeviceNode(DeviceNodeVO vo) {
        // 创建设备节点对象并复制填充节点信息
        DeviceNode node = new DeviceNode();
        BeanUtils.copyProperties(vo, node);
        // 获取设备节点的父级节点信息
        DeviceNode parent = lambdaQuery().eq(DeviceNode::getId, vo.getUpperId()).one();
        // 保存的设备节点层级 = 父节点层级 + 1
        node.setLevel(parent.getLevel() + NumberConstants.ONE);
        // 若入参节点id为空，则为设备节点新增，否则为修改
        if (node.getId() == null || NumberConstants.NUMBER_ZERO_LONG.equals(node.getId())) {
            // 设备节点新增，设置id，创建时间与创建人
            node.setId(IdWorker.getId().nextId());
            node.setCreateTime(new Date(System.currentTimeMillis() / NumberConstants.ONE_THOUSAND * NumberConstants.ONE_THOUSAND));
            node.setCreateUser(SecurityUtils.getUsername());
        } else {
            // 获取修改前设备节点信息
            DeviceNode oldNode = lambdaQuery().select(DeviceNode::getLevel).eq(DeviceNode::getId, vo.getId()).one();
            // 判断节点层级是否发生改变
            Integer levelDiff = oldNode.getLevel() - node.getLevel();
            // 若层级已改变：批量更新后代层级
            if (levelDiff != NumberConstants.ZERO) {
                List<String> childIdList = deviceService.getChildIdList(vo.getId(), dbTypeBean.isH2());
                if (!childIdList.isEmpty()) {
                    // 遍历后代设备节点，更新层级信息
                    childIdList.forEach(id -> {
                        DeviceNode child = getById(id);
                        child.setLevel(child.getLevel() - levelDiff);
                        saveOrUpdate(child);
                    });
                }
            }
        }
        // 设置修改时间与修改人
        node.setUpdateTime(new Date(System.currentTimeMillis() / NumberConstants.ONE_THOUSAND * NumberConstants.ONE_THOUSAND));
        node.setUpdateUser(SecurityUtils.getUsername());
        // 入库
        return saveOrUpdate(node);
    }

    /**
     * 删除设备节点
     *
     * @param id 设备节点id
     */
    @Override
    public void deleteDeviceNode(Long id) {
        // 校验要删除的设备节点id是否为0
        if (NumberConstants.NUMBER_ZERO_LONG.equals(id)) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.COMMON_ID_NOT_NULL));
        }
        // 获取该节点下的子节点
        List<String> idList = deviceService.getChildIdList(id, dbTypeBean.isH2());
        // 校验要删除的设备节点id是否存在设备
        for (String nodeId : idList) {
            if (deviceService.lambdaQuery().eq(Device::getNodeId, nodeId).exists()) {
                throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.DEVICE_EXIST_CHILD_DEVICE));
            }
        }
        // 数据库删除
        removeByIds(idList);
    }

    /**
     * 获取设备监控节点树
     *
     * @param param 查询参数
     * @return {@link AjaxResult}
     * @author cn 2024/07/11
     */
    @Override
    public List<DeviceNodeVO> getDeviceMonitorTree(DeviceNodeParam param) {
        if (ObjectUtils.isEmpty(param.getRootId())) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.COMMON_TREE_ROOT_NOT_NULL));
        }
        List<DeviceNodeVO> tree = new ArrayList<>();
        // 获取所有设备节点列表
        List<DeviceNode> list = lambdaQuery().list();
        // 获取根节点
        DeviceNode root = list.stream().filter(x -> param.getRootId().equals(x.getUpperId())).findFirst().orElse(null);
        // 获取设备列表
        List<Device> deviceList = deviceService.lambdaQuery()
                .eq(!ObjectUtils.isEmpty(param.getEnableStatus()), Device::getEnableStatus, param.getEnableStatus())
                .list();
        // 创建设备节点树
        if (root != null) {
            DeviceNodeVO rootVo = new DeviceNodeVO();
            BeanUtils.copyProperties(root, rootVo);
            rootVo.setLevel(NumberConstants.ONE);
            rootVo.setFullIdPath(String.valueOf(rootVo.getId()));
            rootVo.setFullNamePath(rootVo.getNodeName());
            createTree(rootVo, list, deviceList);
            // 国际化
            String message = I18nMessageUtil.getMessage(MessageTipConstant.GLOBAL_RESOURCE);
            rootVo.setNodeName(message);
            tree.add(rootVo);
        }
        return tree;
    }

    /**
     * 获取在线的设备节点树
     *
     * @param param 查询参数
     * @return {@link AjaxResult}
     * @author cn 2024/07/11
     */
    @Override
    public DeviceResourceVo getDeviceOnlineTree(DeviceNodeParam param) {
        DeviceResourceVo vo = new DeviceResourceVo();
        if (ObjectUtils.isEmpty(param.getRootId())) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.COMMON_TREE_ROOT_NOT_NULL));
        }
        List<DeviceNodeVO> tree = new ArrayList<>();
        List<DeviceNode> list = lambdaQuery().list();
        // 获取父级设备节点信息
        DeviceNode parentNode = list.stream().filter(x -> param.getRootId().equals(x.getId())).findFirst().orElse(null);
        // 获取父级下的设备信息
        List<Device> deviceList = deviceService.lambdaQuery()
                .eq(Device::getEnableStatus, DeviceEnum.ENABLE.getValue())
                .eq(Device::getConnStatus, DeviceEnum.CONN.getValue())
                .list();
        // 获取设备资源
        try {
            CompletableFuture.allOf(deviceList.stream()
                    .map(device -> CompletableFuture.supplyAsync(() -> {
                        getDeviceSourceMsg(device);
                        return device;
                    })).toArray(CompletableFuture[]::new)).get();
        } catch (ExecutionException | InterruptedException e) {
            log.error("getNodeConfigFileParam {}", e.getMessage());
            Thread.currentThread().interrupt();
            throw new ServiceException(String.format(MgConstants.GET_DEVICE_ONLINE_ERROR, e.getMessage()));
        }
        if (parentNode != null) {
            DeviceNodeVO parentVo = new DeviceNodeVO();
            BeanUtils.copyProperties(parentNode, parentVo);
            // 父级节点层级初始化为1
            parentVo.setLevel(NumberConstants.ONE);
            // 父级id路径
            parentVo.setFullIdPath(String.valueOf(parentVo.getId()));
            // 父级名称路径
            parentVo.setFullNamePath(parentVo.getNodeName());
            parentVo.setIsDevice(false);
            // 组装树结构
            createTree(parentVo, list, deviceList, param.getQueryArch());
            // 国际化
            String message = I18nMessageUtil.getMessage(MessageTipConstant.GLOBAL_RESOURCE);
            parentVo.setNodeName(message);
            tree.add(parentVo);
        }
        vo.setDeviceNodeTree(tree);
        vo.setMinDiskLimit(config.diskResource);
        vo.setMinMemLimit(config.memResource);
        vo.setDeviceList(deviceList);
        return vo;
    }

    @Override
    public List<DeviceNodeVO> getRegionTree(RegionParam param) {
        if (ObjectUtils.isEmpty(param.getRootId())) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.COMMON_TREE_ROOT_NOT_NULL));
        }
        List<DeviceNodeVO> tree = new ArrayList<>();
        List<DeviceNode> deviceNodeList = lambdaQuery().list();
        // 获取父级设备节点信息
        DeviceNode root = deviceNodeList.stream().filter(x -> param.getRootId().equals(x.getId())).findFirst().orElse(null);
        // 获取父级下的设备信息
        List<Device> deviceList = deviceService.lambdaQuery()
                .eq(Device::getEnableStatus, DeviceEnum.ENABLE.getValue())
                .eq(Device::getConnStatus, DeviceEnum.CONN.getValue())
                .list();
        if (root != null) {
            //封装节点树
            DeviceNodeVO rootVo = new DeviceNodeVO();
            BeanUtils.copyProperties(root, rootVo);
            // 父级节点层级初始化为1
            rootVo.setLevel(NumberConstants.ONE);
            rootVo.setFullIdPath(String.valueOf(rootVo.getId()));
            // 父级名称路径
            rootVo.setFullNamePath(rootVo.getNodeName());
            rootVo.setIsDevice(false);
            createRegionTree(rootVo, deviceNodeList, deviceList, param.getExcludeId(), param.getQueryArch());
            // 国际化
            String message = I18nMessageUtil.getMessage(MessageTipConstant.GLOBAL_RESOURCE);
            rootVo.setNodeName(message);
            tree.add(rootVo);
        }
        return tree;
    }

    /**
     * 创建当前父级设备节点树结构
     *
     * @param parentNode     父级设备节点
     * @param deviceNodeList 设备节点列表
     * @param deviceList     设备列表
     */
    private void createNodeTree(DeviceNodeVO parentNode, List<DeviceNode> deviceNodeList, List<Device> deviceList) {
        List<DeviceNodeVO> children = new ArrayList<>();
        // 获取父级设备节点的的下一层后代设备节点列表
        List<DeviceNode> childList = deviceNodeList.stream().filter(x -> parentNode.getId().equals(x.getUpperId())).collect(Collectors.toList());
        // 设置当前父级节点的后代设备数量
        parentNode.setDeviceNum((int) deviceList.stream().filter(x -> parentNode.getId().equals(x.getNodeId())).count());
        if (!childList.isEmpty()) {
            // 遍历当前父级节点的后代节点列表
            childList.forEach(node -> {
                // 创建视图对象根节点并复制填充根节点信息
                DeviceNodeVO vo = new DeviceNodeVO();
                BeanUtils.copyProperties(node, vo);
                // 节点层级+1
                vo.setLevel(parentNode.getLevel() + NumberConstants.ONE);
                // 设备节点id全路径
                vo.setFullIdPath(parentNode.getFullIdPath() + SymbolConstants.SLASH + vo.getId());
                // 设备节点名称全路径
                vo.setFullNamePath(parentNode.getFullNamePath() + SymbolConstants.SLASH + vo.getNodeName());
                // 递归调用，创建当前设备节点的后代树结构
                createNodeTree(vo, deviceNodeList, deviceList);
                // 递归返回后刷新节点上对应的设备数据量
                parentNode.setDeviceNum(parentNode.getDeviceNum() + vo.getDeviceNum());
                // 添加到后代节点列表中
                children.add(vo);
            });
        }
        // 更新当前父级节点的后代树结构
        parentNode.setChildren(children);
    }

    /**
     * 创建当前父级的设备以及设备节点的树结构
     *
     * @param parentNode     父级设备节点
     * @param deviceNodeList 设备节点列表
     * @param deviceList     设备列表
     * @param archType       设备架构类型
     */
    private void createTree(DeviceNodeVO parentNode, List<DeviceNode> deviceNodeList, List<Device> deviceList, String... archType) {
        List<DeviceNodeVO> children = new ArrayList<>();
        List<DeviceNode> childList = deviceNodeList.stream().filter(x -> parentNode.getId().equals(x.getUpperId())).collect(Collectors.toList());
        // 1.父级节点下的设备
        List<Device> nodeDeviceList = deviceList.stream()
                .filter(x -> parentNode.getId().equals(x.getNodeId()))
                .filter(x -> archType.length == NumberConstants.ZERO || ObjectUtils.isEmpty(archType[NumberConstants.ZERO]) || archType[NumberConstants.ZERO].equals(x.getArchType()))
                .collect(Collectors.toList());
        nodeDeviceList.forEach(device -> {
            DeviceNodeVO vo = new DeviceNodeVO();
            vo.setId(device.getId());
            vo.setUpperId(parentNode.getId());
            vo.setNodeName(device.getDeviceIp());
            // 设置层级信息
            vo.setLevel(parentNode.getLevel() + NumberConstants.ONE);
            // 设置是否为设备
            vo.setIsDevice(true);
            vo.setConnStatus(device.getConnStatus());
            vo.setArchType(device.getArchType());
            // 设置资源信息
            vo.setMemFree(device.getMemFree());
            vo.setCpuUsing(device.getCpuUsing());
            vo.setCpuCore(device.getCpuCore());
            vo.setDiskFree(device.getDiskFree());
            children.add(vo);
        });
        parentNode.setDeviceNum(nodeDeviceList.size());
        // 2.父级节点下的子节点
        if (!childList.isEmpty()) {
            childList.forEach(node -> {
                DeviceNodeVO vo = new DeviceNodeVO();
                BeanUtils.copyProperties(node, vo);
                // 节点层级+1
                vo.setLevel(parentNode.getLevel() + NumberConstants.ONE);
                // 设备节点id全路径
                vo.setFullIdPath(parentNode.getFullIdPath() + SymbolConstants.SLASH + vo.getId());
                // 设备节点名称全路径
                vo.setFullNamePath(parentNode.getFullNamePath() + SymbolConstants.SLASH + vo.getNodeName());
                // 设置是否为设备
                vo.setIsDevice(false);
                // 递归调用子节点下的树结构
                createTree(vo, deviceNodeList, deviceList, archType);
                // 递归返回后刷新节点上对应的设备数据量
                parentNode.setDeviceNum(parentNode.getDeviceNum() + vo.getDeviceNum());
                if (vo.getDeviceNum() > NumberConstants.ZERO) {
                    children.add(vo);
                }
            });
        }
        parentNode.setChildren(children);
    }

    /**
     * 构建区域节点树对象
     *
     * @param root       根节点
     * @param list       设备节点集合
     * @param deviceList 设备集合
     * @param excludeId  要排除的节点ID
     * @param queryType  设备架构类型
     */
    private void createRegionTree(DeviceNodeVO root, List<DeviceNode> list, List<Device> deviceList, Long excludeId, String queryType) {
        List<DeviceNodeVO> children = new ArrayList<>();
        List<DeviceNode> childList = list.stream().filter(x -> root.getId().equals(x.getUpperId())).collect(Collectors.toList());
        // 节点下的设备
        long deviceCount = deviceList.stream()
                .filter(x -> root.getId().equals(x.getNodeId()) && queryType.equals(x.getArchType()))
                .count();
        root.setDeviceNum(Math.toIntExact(deviceCount));
        // 节点下的子节点
        if (!childList.isEmpty()) {
            childList.forEach(node -> {
                if (ObjectUtils.isEmpty(excludeId) || !excludeId.equals(node.getId())) {
                    DeviceNodeVO vo = new DeviceNodeVO();
                    BeanUtils.copyProperties(node, vo);
                    // 节点层级+1
                    vo.setLevel(root.getLevel() + NumberConstants.ONE);
                    // 设备节点id全路径
                    vo.setFullIdPath(root.getFullIdPath() + SymbolConstants.SLASH + vo.getId());
                    // 设备节点名称全路径
                    vo.setFullNamePath(root.getFullNamePath() + SymbolConstants.SLASH + vo.getNodeName());
                    // 设置是否为设备
                    vo.setIsDevice(false);
                    // 递归调用子节点下的树结构
                    createRegionTree(vo, list, deviceList, excludeId, queryType);
                    // 递归返回后刷新节点上对应的设备数据量
                    root.setDeviceNum(root.getDeviceNum() + vo.getDeviceNum());
                    if (vo.getDeviceNum() > NumberConstants.ZERO) {
                        children.add(vo);
                    }
                }
            });
        }
        root.setChildren(children);
    }

    /**
     * 获取设备资源信息（cpu使用率、磁盘和内存剩余量）
     * @param device 设备
     */
    private void getDeviceSourceMsg(Device device) {
        // 获取设备的实时可用资源
        Map<String, Double> freeResource = sshdService.deviceFreeResource(device.getDeviceIp());
        device.setCpuUsing(String.valueOf(freeResource.get(Constants.CPU_USE)));
        device.setDiskFree(freeResource.get(Constants.DISK_FREE));
        device.setMemFree(freeResource.get(Constants.MEM_FREE));
    }

}