package com.dm.cn.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dm.cn.base.entity.param.AlarmCountParam;
import com.dm.cn.base.entity.server.SysMessage;
import com.dm.cn.base.entity.vo.AlertConfigVO;
import com.dm.cn.base.entity.vo.SysMessageVO;
import com.dm.cn.common.param.SysMessageParam;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 系统消息、监控告警表: 逻辑接口
 *
 * @author Auto-Coder
 * @date 2025-02-10
 */
public interface SysMessageService extends IService<SysMessage> {

    /**
     * 根据参数查询告警历史
     *
     * @param param 告警历史参数
     * @return {@link String}
     */
    IPage<SysMessageVO> getSysMessagePageList(SysMessageParam param);

    /**
     * 导出告警历史记录
     *
     * @param param 告警历史参数
     * @return {@link List}<{@link SysMessageVO}>
     */
    List<SysMessageVO> exportSysMessageList(SysMessageParam param);

    /**
     * 根据告警规则创建告警记录
     *
     * @param ruleId         告警规则id
     * @param alarmCondition 告警项
     * @param title          标题
     * @param tableName      表名
     * @param objectId       资源ID
     * @param resourceName   资源名称
     * @param kind           消息类型
     * @param level          告警级别
     * @param createTime     创建时间
     * @param messageContent 消息内容
     * @param messageContentEn 消息内容英文
     * @param isEndRightNow 是否立即结束
     * @param alertId 告警配置id
     * @param objectDbName 数据库对象名称
     * @return
     */
    Long createAlertMessage(String alarmCondition, Long ruleId, String title, String tableName, Long objectId, String resourceName,
                            String kind, String level, Date createTime, String messageContent, String messageContentEn, Boolean isEndRightNow, Long alertId, String objectDbName);
    /**
     * 保存告警记录
     *
     * @param ruleId         告警规则id
     * @param alarmCondition 告警项
     * @param title          标题
     * @param tableName      表名
     * @param objectId       资源ID
     * @param resourceName   资源名称
     * @param kind           消息类型
     * @param level          告警级别
     * @param createTime     创建时间
     * @param messageContent 消息内容
     * @param messageContentEn 消息内容英文
     * @param messageId      消息id
     * @param activeState    消息状态
     * @param objectDbName    数据库对象
     * @return
     */
    SysMessage saveSysMessage(String alarmCondition, Long ruleId, String title, String tableName, Long objectId, String resourceName, String kind, String level, Date createTime, String messageContent, String messageContentEn, Long messageId, String activeState, String objectDbName);

    /**
     * 获得最近的告警消息
     *
     * @param ruleId   规则id
     * @param objectId 对象id
     * @param kind     消息类型
     * @param level    警告级别
     * @param objectDbName   数据库对象
     * @return SysMessage
     */
    SysMessage getLatelyAlert(Long ruleId, Long objectId, String kind, String level, String objectDbName);

    /**
     * 告警消息设为过期death
     *
     * @param ruleId   告警规则id
     * @param objectId 绑定对象id
     * @param kind     消息类型
     * @param level    警告级别
     * @param objectDbName    数据库对象
     */
    void deleteAlertMessage(Long ruleId, Long objectId, String kind, String level, String objectDbName);

    /**
     * 首页查询告警信息
     * @param param 查询参数
     * @return @return {@link Map}<{@link String}, {@link Object}>
     */
    Map<String, Object> getSysMessageData(AlarmCountParam param);

    /**
     * 发送告警通知（短信 邮件 ...）
     * @param sysMessage   告警消息对象
     * @param alertFlag    是否为解除告警
     */
    void noticeMessage(SysMessage sysMessage, boolean alertFlag);

    /**
     * 根据对象id删除历史告警
     * @param instanceId
     * @param nodeIds
     */
    void deleteByObjectId(Long instanceId, List<Long> nodeIds);
}