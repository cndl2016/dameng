package com.dm.cn.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dm.cn.common.core.domain.SysUser;
import com.dm.cn.system.entity.param.SyncTaskParam;
import com.dm.cn.system.entity.param.SyncUserParam;
import com.dm.cn.system.entity.server.SysDomain;

import javax.naming.NamingException;
import java.util.List;

/**
 * @author root
 * @date 2023/05/08
 */
public interface SysDomainService extends IService<SysDomain> {

    /**
     * 获取域列表
     * @param domain
     * @return
     */
    List<SysDomain> selectDomainList(SysDomain domain);

    /**
     * 检查域地址唯一
     *
     * @param domain
     * @return {@link String}
     */
    String checkDomainUnique(SysDomain domain);

    /**
     * 新增域
     *
     * @param domain
     * @return {@link int}
     */
    int insertDomain(SysDomain domain);

    /**
     * 修改域
     *
     * @param domain
     * @return {@link int}
     */
    int updateDomain(SysDomain domain);

    /**
     * 查询域详细信息
     *
     * @param domainId
     * @return {@link List}<{@link SysDomain}>
     */
    SysDomain selectDomainById(Long domainId);

    /**
     * 删除域
     *
     * @param domainId 部门ID
     * @return int
     */
    int deleteDomainById(Long domainId);


    /**
     * 获取域用户选项
     *
     * @param domainId 域id
     * @return {@link List}<{@link SysUser}>
     */
    List<SysUser> getDomainUser(Long domainId);

    /**
     * 同步域用户属性
     *
     * @param param
     * @return boolean
     * @throws NamingException
     */
    boolean syncDomainUser(SyncUserParam param) throws NamingException;

    /**
     * 同步任务
     *
     * @param param
     * @return boolean
     */
    boolean handleSyncTask(SyncTaskParam param);
}
