package com.dm.cn.system.service.impl;

import com.dm.cn.common.core.utils.bean.DbTypeBean;
import com.dm.cn.system.entity.server.SysOperLog;
import com.dm.cn.system.mapper.SysOperLogMapper;
import com.dm.cn.system.service.ISysOperLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 操作日志 服务层处理
 *
 * @author dameng
 */
@Service
public class SysOperLogServiceImpl implements ISysOperLogService {
    @Resource
    private SysOperLogMapper operLogMapper;

    @Resource
    private DbTypeBean dbTypeBean;

    /**
     * 新增操作日志
     *
     * @param operLog 操作日志对象
     * @return 结果
     */
    @Override
    public int insertOperlog(SysOperLog operLog) {
        return operLogMapper.insertOperlog(operLog);
    }

    /**
     * 查询系统操作日志集合
     *
     * @param operLog 操作日志对象
     * @return 操作日志集合
     */
    @Override
    public List<SysOperLog> selectOperLogList(SysOperLog operLog) {
        operLog.setH2(dbTypeBean.isH2());
        return operLogMapper.selectOperLogList(operLog);
    }

    /**
     * 批量删除系统操作日志
     *
     * @param operIds 需要删除的操作日志ID
     * @return 结果
     */
    @Override
    public int deleteOperLogByIds(Long[] operIds) {
        return operLogMapper.deleteOperLogByIds(operIds);
    }

    /**
     * 清空操作日志
     */
    @Override
    public void cleanOperLog() {
        operLogMapper.cleanOperLog();
    }
}
