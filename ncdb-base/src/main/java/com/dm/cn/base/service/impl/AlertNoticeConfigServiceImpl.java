package com.dm.cn.base.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dm.cn.base.entity.server.AlertConfig;
import com.dm.cn.base.entity.server.AlertNoticeConfig;
import com.dm.cn.base.entity.vo.NoticeConfigVO;
import com.dm.cn.base.mapper.AlertNoticeConfigMapper;
import com.dm.cn.base.service.AlertNoticeConfigService;
import com.dm.cn.common.core.utils.IdWorker;
import com.dm.framework.common.util.CollectionUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huangzizhong
 * @description 针对表【T_ALERT_NOTICE_CONFIG(告警项通知方式配置表)】的数据库操作Service实现
 * @createDate 2025-02-11 09:18:35
 */
@Service
public class AlertNoticeConfigServiceImpl extends ServiceImpl<AlertNoticeConfigMapper, AlertNoticeConfig>
        implements AlertNoticeConfigService {

    @Override
    public void save(AlertConfig alertConfig, List<NoticeConfigVO> selectNoticeList) {
        // 1.删除已经配置的(后续操作不执行就相当于是取消了配置信息)
        remove(new LambdaQueryWrapper<AlertNoticeConfig>().eq(AlertNoticeConfig::getAlertConfigId, alertConfig.getId()));
        if (CollUtil.isNotEmpty(selectNoticeList)) {
            // 2.组装要保存的数据对象
            List<AlertNoticeConfig> configList = new ArrayList<>();
            for (NoticeConfigVO configVo : selectNoticeList) {
                AlertNoticeConfig noticeConfig = new AlertNoticeConfig();
                noticeConfig.setId(IdWorker.getId().nextId());
                noticeConfig.setObjectId(alertConfig.getObjectId());
                noticeConfig.setAlertConfigId(alertConfig.getId());
                noticeConfig.setAlertRuleId(alertConfig.getRuleId());
                noticeConfig.setAlarmMessageId(configVo.getAlarmMessageId());
                noticeConfig.setRecipient(configVo.getRecipient());
                configList.add(noticeConfig);
            }
            // 3.批量保存
            saveBatch(configList);
        }
    }

}