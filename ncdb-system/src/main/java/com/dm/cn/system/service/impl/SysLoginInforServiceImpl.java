package com.dm.cn.system.service.impl;

import com.dm.cn.common.core.utils.bean.DbTypeBean;
import com.dm.cn.system.entity.server.SysLogininfor;
import com.dm.cn.system.mapper.SysLogininforMapper;
import com.dm.cn.system.service.ISysLoginInforService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 系统访问日志情况信息 服务层处理
 *
 * @author dameng
 */
@Service
public class SysLoginInforServiceImpl implements ISysLoginInforService {

    @Resource
    private SysLogininforMapper loginInforMapper;

    @Resource
    private DbTypeBean dbTypeBean;

    /**
     * 新增系统登录日志
     *
     * @param loginInfo 访问日志对象
     */
    @Override
    public int insertLoginInfor(SysLogininfor loginInfo) {
        loginInfo.setH2(dbTypeBean.isH2());
        return loginInforMapper.insertLogininfor(loginInfo);
    }

    /**
     * 查询系统登录日志集合
     *
     * @param loginInfo 访问日志对象
     * @return 登录记录集合
     */
    @Override
    public List<SysLogininfor> selectLoginInforList(SysLogininfor loginInfo) {
        loginInfo.setH2(dbTypeBean.isH2());
        return loginInforMapper.selectLogininforList(loginInfo);
    }

    /**
     * 批量删除系统登录日志
     *
     * @param infoIds 需要删除的登录日志ID
     * @return 结果
     */
    @Override
    public int deleteLoginInforByIds(Long[] infoIds) {
        return loginInforMapper.deleteLogininforByIds(infoIds);
    }

    /**
     * 清空系统登录日志
     */
    @Override
    public void cleanLoginInfor() {
        loginInforMapper.cleanLogininfor();
    }


    /**
     * 获取登录用户的信息
     *
     * @param username
     * @return {@link SysLogininfor}
     */
    @Override
    public SysLogininfor getLastLoginUser(String username) {
        return loginInforMapper.getLastLoginUser(username);
    }
}
