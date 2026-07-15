package com.dm.cn.system.mapper;


import com.dm.cn.system.entity.server.SysLogininfor;

import java.util.List;


/**
 * 系统访问日志情况信息 数据层
 *
 * @author dameng
 */
public interface SysLogininforMapper {
    /**
     * 新增系统登录日志
     *
     * @param loginInfo
     * @return
     */
    int insertLogininfor(SysLogininfor loginInfo);

    /**
     * 查询系统登录日志集合
     *
     * @param loginInfo 访问日志对象
     * @return 登录记录集合
     */
    List<SysLogininfor> selectLogininforList(SysLogininfor loginInfo);

    /**
     * 批量删除系统登录日志
     *
     * @param infoIds 需要删除的登录日志ID
     * @return 结果
     */
    int deleteLogininforByIds(Long[] infoIds);

    /**
     * 清空系统登录日志
     *
     * @return 结果
     */
    int cleanLogininfor();


    /**
     * 获取登录用户的信息
     *
     * @param username
     * @return {@link SysLogininfor}
     */
    SysLogininfor getLastLoginUser(String username);
}
