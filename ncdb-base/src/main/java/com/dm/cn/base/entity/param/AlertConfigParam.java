package com.dm.cn.base.entity.param;

import com.baomidou.mybatisplus.annotation.TableField;
import com.dm.cn.base.entity.server.AlertConfig;
import com.dm.cn.base.entity.vo.AlertParametersVO;
import com.dm.cn.base.entity.vo.NoticeConfigVO;
import lombok.Data;

import java.util.List;

/**
 * 告警规则保存入参
 *
 * @author dameng
 */
@Data
public class AlertConfigParam {

    /**
     * 告警规则主表对象
     */
    private AlertConfig alertConfig;

    /**
     * 告警规则参数信息
     */
    private List<AlertParametersVO> alertParameters;

    /**
     * 配置的告警通知信息
     */
    private List<NoticeConfigVO> selectNoticeList;

    /**
     * 监控对象类型 instance/device
     */
    private String alertType;

    private String dbName;
}