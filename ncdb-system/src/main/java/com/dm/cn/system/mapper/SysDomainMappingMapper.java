package com.dm.cn.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dm.cn.system.entity.server.SysDomainMapping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author dameng
 * @Entity com.dm.cn.system.domain.SysDomainMapping
 */
public interface SysDomainMappingMapper extends BaseMapper<SysDomainMapping> {
    /**
     * 新增用户属性映射
     *
     * @param mappingList
     * @return 结果
     */
    int batchDomainMapping(List<SysDomainMapping> mappingList);

    /**
     * 查询用户属性映射
     *
     * @param domainId
     * @return 结果
     */
    List<SysDomainMapping> selectByDomainId(@Param("domainId") Long domainId);

    /**
     * 删除用户属性映射
     *
     * @param domainId
     * @return 结果
     */
    int batchDeleteByDomainId(@Param("domainId") Long domainId);
}




