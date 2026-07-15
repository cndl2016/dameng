package com.dm.cn.base.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dm.cn.base.constant.SysMessageConstant;
import com.dm.cn.base.entity.param.AlertNoticeMessageParam;
import com.dm.cn.base.entity.server.AlertNoticeMessage;
import com.dm.cn.base.mapper.AlertNoticeMessageMapper;
import com.dm.cn.base.service.AlertNoticeMessageService;
import com.dm.cn.common.core.constant.Constants;
import com.dm.cn.common.core.constant.NumberConstants;
import com.dm.cn.common.enums.CommonEnum;
import com.dm.cn.common.enums.SendMethodEnum;
import com.dm.cn.common.utils.validate.ParamValidate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author huangzizhong
 * @description 针对表【T_ALERT_NOTICE_MESSAGE(告警通知消息记录表)】的数据库操作Service实现
 * @createDate 2025-02-13 09:23:07
 */
@Service
public class AlertNoticeMessageServiceImpl extends ServiceImpl<AlertNoticeMessageMapper, AlertNoticeMessage>
        implements AlertNoticeMessageService {

    @Override
    public IPage<AlertNoticeMessage> getPage(AlertNoticeMessageParam param) {
        IPage<AlertNoticeMessage> page = new Page<>(param.getPageNum(), param.getPageSize());

        // 通知时间查询
        String beginTime = String.valueOf(param.getParams().get(Constants.QUERY_PARAM_BEGIN_TIME));
        String endTime = String.valueOf(param.getParams().get(Constants.QUERY_PARAM_END_TIME));

        return lambdaQuery().eq(ParamValidate.validate(param.getAlertLevel()), AlertNoticeMessage::getAlertLevel, param.getAlertLevel())
                .and(ParamValidate.validate(param.getTitle()), wrapper -> {
                    wrapper.like(AlertNoticeMessage::getTitle, param.getTitle()).or().like(AlertNoticeMessage::getTitleEn, param.getTitle());
                })
                .ge(ParamValidate.validate(beginTime), AlertNoticeMessage::getNoticeTime, beginTime)
                .le(ParamValidate.validate(endTime), AlertNoticeMessage::getNoticeTime, endTime)
                .orderByDesc(AlertNoticeMessage::getNoticeTime)
                .page(page);
    }

    @Override
    public List<AlertNoticeMessage> export(AlertNoticeMessageParam param) {
        List<AlertNoticeMessage> dataList = getPage(param).getRecords();
        // 将结果翻译成中文
        if (!CollectionUtils.isEmpty(dataList)) {
            dataList.forEach(item -> {
                item.setNoticeMethodDesc(SendMethodEnum.getDesc(item.getNoticeMethod()));
                item.setAlertLevelDesc(item.getAlertLevel().equals(SysMessageConstant.LEVEL_FATAL) ? "高风险" : "低风险");
                item.setIsSuccessDesc(NumberConstants.NUMBER_ONE.equals(item.getIsSuccess()) ? CommonEnum.SUCCESS.getDesc() : CommonEnum.ERROR.getDesc());
            });
        }

        return dataList;
    }
}