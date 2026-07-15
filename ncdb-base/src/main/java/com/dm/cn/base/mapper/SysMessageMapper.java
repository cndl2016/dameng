package com.dm.cn.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dm.cn.base.entity.param.AlarmCountParam;
import com.dm.cn.base.entity.server.SysMessage;
import com.dm.cn.base.entity.vo.SysMessageVO;
import com.dm.cn.common.param.SysMessageParam;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统消息、监控告警表数据访问对象
 *
 * @author Auto-Coder
 * @date 2023-03-02
 */
public interface SysMessageMapper extends BaseMapper<SysMessage> {

    /**
     * 查询告警历史记录
     *
     * @param page         分页
     * @param param        查询参数
     * @param time         时间
     * @param userNameList 数据权限用户集合
     * @return {@link IPage}<{@link SysMessageVO}>
     */
    IPage<SysMessageVO> getSysMessagePageList(IPage<SysMessageVO> page,
                                              @Param("param") SysMessageParam param,
                                              @Param("time") LocalDateTime time,
                                              @Param("userNameList") List<String> userNameList);

    /**
     * 查询近一天内的告警信息
     *
     * @param param 查询参数
     * @param userNameList 用户权限
     * @param queryTime    查询时间
     * @return
     */
    List<SysMessageVO> getSysMessageVoList(@Param("param") AlarmCountParam param,
                                           @Param("userNameList") List<String> userNameList,
                                           @Param("queryTime") String queryTime);
}