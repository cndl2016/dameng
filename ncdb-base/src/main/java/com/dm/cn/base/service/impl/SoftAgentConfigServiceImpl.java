package com.dm.cn.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dm.cn.base.entity.server.SoftAgentConfig;
import com.dm.cn.base.mapper.SoftAgentConfigMapper;
import com.dm.cn.base.service.SoftAgentConfigService;
import com.dm.cn.common.core.constant.Constants;
import com.dm.cn.common.core.domain.LoginUser;
import com.dm.cn.common.core.exception.ServiceException;
import com.dm.cn.common.core.utils.StringUtils;
import com.dm.cn.common.enums.ModelTypeEnum;
import com.dm.cn.common.param.SoftAgentConfigParam;
import com.dm.cn.common.security.utils.SecurityUtils;
import com.dm.cn.common.utils.I18nMessageUtil;
import com.dm.cn.common.utils.validate.ParamValidate;
import com.dm.cn.quartz.entity.server.SysJob;
import com.dm.cn.quartz.service.SysJobService;
import com.dm.cn.system.constant.MessageTipConstant;
import com.dm.cn.system.service.ISysMessageTipService;
import com.dm.framework.common.util.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author dameng
 * @description 针对表【SYS_SOFT_AGENT_CONFIG(数据采集频率配置表)】的数据库操作Service实现
 * @createDate 2025-01-24 10:19:52
 */
@Service
public class SoftAgentConfigServiceImpl extends ServiceImpl<SoftAgentConfigMapper, SoftAgentConfig> implements SoftAgentConfigService {

    private static final Logger log = LoggerFactory.getLogger(SoftAgentConfigServiceImpl.class);

    @Resource
    private SysJobService sysJobService;

    @Resource
    private ISysMessageTipService sysMessageTipService;


    @Override
    public IPage<SoftAgentConfig> getPage(SoftAgentConfigParam param) {
        IPage<SoftAgentConfig> page = new Page<>(param.getPageNum(), param.getPageSize());

        // 关键字查询
        String keywords = param.getKeywords();

        IPage<SoftAgentConfig> iPage = lambdaQuery().like(ParamValidate.validate(keywords), SoftAgentConfig::getName, keywords)
                .orderByAsc(SoftAgentConfig::getId)
                .page(page);

        List<SoftAgentConfig> recordsList = new ArrayList<>();
        recordsList.addAll(iPage.getRecords());
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (!CollectionUtils.isEmpty(recordsList)) {
            for (SoftAgentConfig agentConfig : recordsList) {
                // 国际化处理
                if (Constants.EN.equals(loginUser.getLanguage())) {
                    String nameEn = sysMessageTipService.getMessageTip(Constants.EN, "SYS_SOFT_AGENT_CONFIG", agentConfig.getId().toString(), "name");
                    agentConfig.setName(nameEn);
                    agentConfig.setRemark(nameEn);
                    agentConfig.setGroupsName(agentConfig.getGroups());
                } else {
                    agentConfig.setGroupsName(ModelTypeEnum.getDesc(agentConfig.getGroups().toUpperCase(Locale.ROOT)));
                }
            }
        }

        IPage<SoftAgentConfig> result = new Page<>();
        result.setCurrent(iPage.getCurrent());
        result.setPages(iPage.getPages());
        result.setSize(iPage.getSize());
        result.setTotal(iPage.getTotal());
        result.setRecords(recordsList);

        return result;
    }

    @Override
    public boolean update(SoftAgentConfig softAgentConfig) {
        // 1.采集频率对象是否存在
        SoftAgentConfig save = lambdaQuery().eq(SoftAgentConfig::getId, softAgentConfig.getId()).one();
        if (ObjectUtils.isEmpty(softAgentConfig.getId()) || null == save) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.COMMON_RECORD_NOT_EXIST, softAgentConfig.getId()));
        }

        // 2.判断groups参数合法性
        String groups = softAgentConfig.getGroups();
        if (StringUtils.isEmpty(groups) || !ModelTypeEnum.isExistByName(groups.toUpperCase(Locale.ROOT))) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.UNKNOWN_MODEL_TYPE));
        }

        // 3.保存
        softAgentConfig.setName(save.getName());
        softAgentConfig.setRemark(save.getRemark());
        updateById(softAgentConfig);

        // 4.更新job表
        SysJob sysJob = sysJobService.getOne(new LambdaQueryWrapper<SysJob>().eq(SysJob::getJobName, softAgentConfig.getCode()));
        sysJob.setCronExpression(softAgentConfig.getCronExpression());
        sysJobService.updateById(sysJob);
        // 5.刷新任务
        try {
            sysJobService.updateJob(sysJob);
        } catch (Exception e) {
            log.error("刷新job失败，param：{}，error: {}", BeanUtil.toJsonString(softAgentConfig), e);
        }
        return true;
    }

}