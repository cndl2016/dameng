package com.dm.cn.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dm.cn.system.entity.server.SysDomain;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author root
 * @Entity com.dm.cn.system.domain.SysDomain
 * @date 2023/05/08
 */
public interface SysDomainMapper extends BaseMapper<SysDomain> {

    /**
     * 获取域列表
     * @param domain
     * @return
     */
    List<SysDomain> selectDomainList(SysDomain domain);

    /**
     * 获取域列表
     *
     * @param domainId 域id
     * @return {@link List}<{@link SysDomain}>
     */
    Integer countDomainUser(@Param("domainId") Long domainId);

    /**
     * 检查域唯一
     *
     * @param domain
     * @return {@link List}<{@link SysDomain}>
     */
    SysDomain checkDomainUnique(SysDomain domain);

    /**
     * 新增域
     *
     * @param domain
     * @return {@link List}<{@link SysDomain}>
     */
    int insertDomain(SysDomain domain);

    /**
     * 修改域
     *
     * @param domain
     * @return {@link List}<{@link SysDomain}>
     */
    int updateDomain(SysDomain domain);

    /**
     * 查询域详细信息
     *
     * @param domainId
     * @return {@link List}<{@link SysDomain}>
     */
    SysDomain selectDomainById(@Param("domainId") Long domainId);

    /**
     * 删除域
     *
     * @param domainId 部门ID
     * @return int
     */
    int deleteDomainById(@Param("domainId") Long domainId);

    /**
     * 获取开启了任务的域
     *
     * @return List<SysDomain>
     */
    List<SysDomain> selectDomainTaskList();
}




