package com.dm.cn.base.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.dm.cn.base.constant.AlertRuleConstant;
import com.dm.cn.base.constant.SysColumnUserConstant;
import com.dm.cn.base.constant.SysMessageConstant;
import com.dm.cn.base.entity.model.BaseModel;
import com.dm.cn.base.entity.vo.AlertConfigVO;
import com.dm.cn.base.entity.vo.AlertParametersVO;
import com.dm.cn.base.service.AlertBaseService;
import com.dm.cn.base.service.AlertConfigService;
import com.dm.cn.base.service.SysMessageService;
import com.dm.cn.common.core.constant.Constants;
import com.dm.cn.common.core.constant.NumberConstants;
import com.dm.cn.common.core.constant.SymbolConstants;
import com.dm.cn.common.core.utils.StringUtils;
import com.dm.cn.common.utils.I18nMessageUtil;
import com.dm.cn.common.utils.ThreadPoolManager;
import com.dm.cn.system.constant.MessageTipConstant;
import com.dm.cn.system.service.ISysMessageTipService;
import com.dm.framework.common.util.BeanUtil;
import com.dm.framework.common.util.CollectionUtil;
import com.dm.framework.common.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 告警抽象基础类接口
 *
 * @author root
 * @date 2023-03-20
 */
@Service
@Slf4j
public class AlertBaseServiceImpl implements AlertBaseService {

    private static final int CHECK_LENGTH = 1000;

    /**
     * 告警数据缓存，由于持续时间不同，这里分为严重、警告两类缓存
     */
    private final Map<String,List<TimeOutCache>> map = new ConcurrentHashMap<>(16);

    /**
     * 告警数据起始时间缓存
     */
    private final Map<String, Date> dateMap = new ConcurrentHashMap<>(16);

    @Resource
    private AlertConfigService alertConfigService;

    @Resource
    private SysMessageService sysMessageService;

    @Resource
    private ISysMessageTipService sysMessageTipService;

    @Override
    public void calcAlert(BaseModel baseModel) {
        ThreadPoolManager.getInstance().execute(()->calcAlertTask(baseModel));
    }

    @Override
    public void calcAlert(BaseModel baseModel, String ruleIdOrCode) {
        if (StringUtil.isNotEmpty(ruleIdOrCode)) {
            ThreadPoolManager.getInstance().execute(() -> calcAlertTask(baseModel, ruleIdOrCode));
        } else {
            ThreadPoolManager.getInstance().execute(() -> calcAlertTask(baseModel));
        }
    }

    @Override
    public void calcInstanceAlert(BaseModel baseModel,  String ruleIdOrCode) {
        ThreadPoolManager.getInstance().execute(() -> calcInstanceAlertTask(baseModel, ruleIdOrCode));
    }

    private void calcAlertTask(BaseModel baseModel) {
        List<AlertConfigVO> alertConfigVoList = alertConfigService.getListByKey(baseModel.getClass().getName(), baseModel.getAgentIp(), baseModel.getPort());
        if (CollectionUtil.isEmpty(alertConfigVoList)) {
            log.info("{} , 告警配置获取错误 ", BeanUtil.toJsonString(baseModel));
            return;
        }
        for (AlertConfigVO alertConfigVo : alertConfigVoList) {
            //国际化
            String memoEn = sysMessageTipService.getMessageTip(Constants.EN, "T_ALERT_RULE", alertConfigVo.getRuleId().toString(), "memo");
            alertConfigVo.setMemoEn(memoEn);
            baseAlert(alertConfigVo, baseModel);
        }
    }

    private void calcAlertTask(BaseModel baseModel, String ruleIdOrCode) {
        String key = baseModel.getClass().getName() + StringUtil.trim(baseModel.getAgentIp(), "") + (null != baseModel.getPort() ? baseModel.getPort() : "");
        AlertConfigVO alertConfigVo = alertConfigService.getAlertByRuleIdOrCode(key, ruleIdOrCode);
        if (alertConfigVo == null) {
            log.info("{} , 告警配置获取错误 ", BeanUtil.toJsonString(baseModel));
            return;
        }
        //国际化
        String memoEn = sysMessageTipService.getMessageTip(Constants.EN, "T_ALERT_RULE", alertConfigVo.getRuleId().toString(), "memo");
        alertConfigVo.setMemoEn(memoEn);
        alertConfigVo.setObjectId(Long.parseLong(baseModel.getId()));
        alertConfigVo.setObjectName(I18nMessageUtil.getMessage(MessageTipConstant.DEVICE) + " " + baseModel.getAgentIp());
        baseAlert(alertConfigVo, baseModel);
    }

    /**
     * 计算实例告警
     * @param baseModel
     * @param ruleIdOrCode
     */
    private void calcInstanceAlertTask(BaseModel baseModel, String ruleIdOrCode) {
        AlertConfigVO alertConfigVo;
        String objectName;
        String baseKey = baseModel.getClass().getName() + baseModel.getInstanceId();
        if (StringUtils.isBlank(baseModel.getDbName())) {
            // 节点告警处理
            alertConfigVo = alertConfigService.getAlertByRuleIdOrCode(baseKey, ruleIdOrCode);
            objectName = String.format(Constants.NODE_MONITOR_TITLE, baseModel.getInstanceName(), baseModel.getNodeName(), baseModel.getAgentIp(), baseModel.getPort());
            if (StringUtils.isNotBlank(baseModel.getPid())){
                objectName = String.format(Constants.INSTANCE_PID_MONITOR_TITLE, baseModel.getInstanceName(), baseModel.getPid());
            } else if (StringUtils.isNotBlank(baseModel.getQueryId())){
                objectName = String.format(Constants.INSTANCE_QUERY_ID_MONITOR_TITLE, baseModel.getInstanceName(), baseModel.getQueryId());
            }
        } else {
            // 数据库告警 先获取数据库配置项
            String dbKey = baseModel.getClass().getName() + baseModel.getInstanceId() + baseModel.getDbName();
            alertConfigVo = alertConfigService.getAlertByRuleIdOrCode(dbKey, ruleIdOrCode);
            if (ObjectUtils.isEmpty(alertConfigVo)) {
                // 数据库告警配置项未空 则获取实例配置项
                alertConfigVo = alertConfigService.getAlertByRuleIdOrCode(baseKey, ruleIdOrCode);
            }
            objectName = String.format(Constants.INSTANCE_DB_MONITOR_TITLE, baseModel.getInstanceName(), baseModel.getDbName());
        }
        if (alertConfigVo == null) {
            log.info("{} , 告警配置获取错误 ", BeanUtil.toJsonString(baseModel));
            return;
        }
        alertConfigVo.setAlarmCondition(baseModel.getAlarmCondition());
        //国际化
        String memoEn = sysMessageTipService.getMessageTip(Constants.EN, "T_ALERT_RULE", alertConfigVo.getRuleId().toString(), "memo");
        alertConfigVo.setMemoEn(memoEn);
        alertConfigVo.setObjectId(Long.parseLong(baseModel.getId()));
        alertConfigVo.setObjectName(objectName);
        alertConfigVo.setObjectDbName(baseModel.getDbName());
        baseAlert(alertConfigVo, baseModel);
    }

    private void baseAlert(AlertConfigVO alertConfigVo, BaseModel baseModel) {
        AlertParametersVO alertParameters = new AlertParametersVO();
        //alertConfigVo.getAlertParameters()里面只有一个有效值
        if (CollectionUtil.isEmpty(alertConfigVo.getAlertParameters())) {
            return;
        } else {
            for (AlertParametersVO alertParametersVo : alertConfigVo.getAlertParameters()) {
                if (alertParametersVo == null) {
                    continue;
                }
                alertParameters = alertParametersVo;
            }
        }
        if (StringUtil.isEmpty(alertParameters.getColumn())) {
            return;
        }

        boolean fatalBool = false;
        boolean warnBool = false;
        StrAnex strAnex = new StrAnex();
        //英文
        StrAnex strAnexEn = new StrAnex();
        if (AlertRuleConstant.STATUS_YES.equals(alertConfigVo.getStatus())) {
            //新版需求：规则有严重和告警开关：fatalStatus、warningStatus
            boolean fatalStatus = alertParameters.getFatalStatus()!=null?alertParameters.getFatalStatus():false;
            boolean warningStatus = alertParameters.getWarningStatus()!=null?alertParameters.getWarningStatus():false;

            //fatalStatus、warningStatus都是关的，则直接跳过
            if (fatalStatus || warningStatus) {
                //采集值处理
                Object val = getValueByObject(baseModel, alertParameters.getColumn());
                val = checkAndChangeValue(val, alertParameters.getLogic());
                //如果是空，return
                if (val == null) {
                    return;
                }
                //如果是空字符串，也return
                if (val instanceof String && StringUtil.isEmpty((String) val)) {
                    return;
                }
                //处理严重
                if (fatalStatus) {
                    fatalBool = checkFatalValue(alertParameters, val);
                    if (fatalBool) {
                        strAnex.addStr(val.toString(), alertParameters, SysMessageConstant.LEVEL_FATAL,alertConfigVo.getMemo(), baseModel.getDemo(), baseModel.getDemoOne(), baseModel.getDemoTwo(), baseModel.getDemoThree(), Constants.ZH);
                        strAnexEn.addStr(val.toString(), alertParameters, SysMessageConstant.LEVEL_FATAL,alertConfigVo.getMemoEn(), baseModel.getDemo(), baseModel.getDemoOne(), baseModel.getDemoTwo(), baseModel.getDemoThree(), Constants.EN);
                    }
                }

                //处理警告
                if (warningStatus && !fatalBool) {
                    warnBool = checkWarnValue(alertParameters, val);
                    if (warnBool) {
                        strAnex.addStr(val.toString(), alertParameters, SysMessageConstant.LEVEL_WARNING,alertConfigVo.getMemo(), baseModel.getDemo(), baseModel.getDemoOne(), baseModel.getDemoTwo(), baseModel.getDemoThree(), Constants.ZH);
                        strAnexEn.addStr(val.toString(), alertParameters, SysMessageConstant.LEVEL_FATAL,alertConfigVo.getMemoEn(), baseModel.getDemo(), baseModel.getDemoOne(), baseModel.getDemoTwo(), baseModel.getDemoThree(), Constants.EN);
                    }
                }
            }
        }
        if (fatalBool) {
            if (checkDurationTime(alertConfigVo, alertParameters, baseModel.getIsEndRightNow())) {
                createAlertMessage(alertConfigVo, strAnex.toString(), strAnexEn.toString(), SysMessageConstant.LEVEL_FATAL, baseModel.getIsEndRightNow());
            }
        } else if (warnBool) {
            if (checkDurationTime(alertConfigVo, alertParameters, baseModel.getIsEndRightNow())) {
                createAlertMessage(alertConfigVo, strAnex.toString(), strAnexEn.toString(), SysMessageConstant.LEVEL_WARNING, baseModel.getIsEndRightNow());
            }
        } else {
            deleteDurationTime(alertConfigVo, alertParameters);
            // 如果是立即结束的告警则不需要去执行删除告警及发送消息
            if (!baseModel.getIsEndRightNow()) {
                deleteAlertMessage(alertConfigVo);
            }
        }
    }

    private Object checkAndChangeValue(Object val, String logic) {
        if (val == null) {
            return null;
        }
        if (SysColumnUserConstant.CONDITION_GT.equals(logic) || SysColumnUserConstant.CONDITION_GE.equals(logic) ||
                SysColumnUserConstant.CONDITION_LT.equals(logic) || SysColumnUserConstant.CONDITION_LE.equals(logic)) {
            if (!(val instanceof Number)) {
                try {
                    return Double.valueOf(val.toString());
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return val;
    }

    private Object getValueByObject(Object object, String column) {
        Class<?> clazz = object.getClass();
        try {
            Field field = clazz.getDeclaredField(column);
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            return null;
        }
    }

    private Object dealResult(Object result) {
        if (result instanceof Integer || result instanceof Long) {
            return result;
        } else if (result instanceof Float || result instanceof Double) {
            double d = Double.parseDouble(result.toString());
            BigDecimal bg = BigDecimal.valueOf(d);
            return bg.setScale(2, RoundingMode.HALF_UP).doubleValue();
        }
        return result;
    }

    private Object getResultValue(AlertParametersVO alertParameters, List<Object> history) {
        if (CollectionUtil.isEmpty(history)) {
            return null;
        }
        String logic = alertParameters.getLogic();
        if (StringUtil.isEmpty(logic)) {
            return null;
        }
        //监控类，数值
        if (SysColumnUserConstant.CONDITION_GT.equals(logic) || SysColumnUserConstant.CONDITION_GE.equals(logic) ||
                SysColumnUserConstant.CONDITION_LT.equals(logic) || SysColumnUserConstant.CONDITION_LE.equals(logic)) {
            List<Double> historyDouble = new ArrayList<>();
            for (Object val : history) {
                if (val instanceof Number) {
                    historyDouble.add(((Number) val).doubleValue());
                }
            }
            return getAvgValue(historyDouble);
        }
        //状态类，字符串
        String errorStatus = alertParameters.getErrorStatus();
        if (StringUtil.isEmpty(errorStatus)) {
            return null;
        }
        int i = 0;
        int j = 0;
        String errorStr = "";
        String rightStr = "";
        if (SysColumnUserConstant.CONDITION_EQ.equals(logic)) {
            for (Object val : history) {
                if (val instanceof String) {
                    if (errorStatus.equalsIgnoreCase((String) val)) {
                        i++;
                        errorStr = (String) val;
                    } else {
                        j++;
                        rightStr = (String) val;
                    }
                }
            }
        }
        if (SysColumnUserConstant.CONDITION_NE.equals(logic)) {
            for (Object val : history) {
                if (val instanceof String) {
                    if (!errorStatus.equalsIgnoreCase((String) val)) {
                        i++;
                        errorStr = (String) val;
                    } else {
                        j++;
                        rightStr = (String) val;
                    }
                }
            }
        }
        //持续时间内，状态一直异常则返回异常状态
        if (i>0 && j == 0) {
            return errorStr;
        } else {
            return rightStr;
        }
    }

    public void putMap(String configId,String column,Object value,Integer second){
        String key = generateKey(configId,column);
        List<TimeOutCache> list = map.get(key);
        if(list == null){
            list = new LinkedList<>();
            map.put(key,list);
        }else{
            checkAndUpdateList(list);
        }
        list.add(new TimeOutCache(value,1000L * second));
    }

    public List<Object> getValues(String configId,String column){
        String key = generateKey(configId,column);
        List<TimeOutCache> list = map.get(key);
        if(CollectionUtil.isEmpty(list)){
            return new LinkedList<>();
        }
        List<Object> values = new LinkedList<>();
        Iterator<TimeOutCache> it = list.iterator();
        while (it.hasNext()){
            TimeOutCache timeOutCache = it.next();
            Object value = timeOutCache.getValue(false);
            if(value == null){
                it.remove();
            }else{
                values.add(value);
            }
        }

        return values;
    }

    private String generateKey(String configId,String column){
        if(StringUtil.isNotEmpty(configId) || StringUtil.isNotEmpty(column)) {
            return configId + "." + column;
        }
        return configId;
    }

    private void checkAndUpdateList(List<TimeOutCache> list){
        if(list == null || list.size() < CHECK_LENGTH){
            return;
        }
        Iterator<TimeOutCache> it = list.iterator();
        while (it.hasNext()){
            TimeOutCache timeOutCache = it.next();
            Object value = timeOutCache.getValue(false);
            if(value == null){
                it.remove();
            }else{
                break;
            }
        }
    }

    private boolean checkFatalValue(AlertParametersVO alertParametersVo, Object value) {
        if (SysColumnUserConstant.CONDITION_EQ.equals(alertParametersVo.getLogic())) {
            if (alertParametersVo.getErrorStatus() == null) {
                return false;
            }
            return value.equals(alertParametersVo.getErrorStatus());
        } else if (SysColumnUserConstant.CONDITION_NE.equals(alertParametersVo.getLogic())) {
            if (alertParametersVo.getErrorStatus() == null) {
                return false;
            }
            return !value.equals(alertParametersVo.getErrorStatus());
        } else if (SysColumnUserConstant.CONDITION_GT.equals(alertParametersVo.getLogic())) {
            if (alertParametersVo.getFatal() == null) {
                return false;
            }
            if (!(value instanceof Number)) {
                return false;
            }
            return changeNumberToDouble((Number) value) > alertParametersVo.getFatal();
        } else if (SysColumnUserConstant.CONDITION_GE.equals(alertParametersVo.getLogic())) {
            if (alertParametersVo.getFatal() == null) {
                return false;
            }
            if (!(value instanceof Number)) {
                return false;
            }
            return changeNumberToDouble((Number) value) >= alertParametersVo.getFatal();
        } else if (SysColumnUserConstant.CONDITION_LT.equals(alertParametersVo.getLogic())) {
            if (alertParametersVo.getFatal() == null) {
                return false;
            }
            if (!(value instanceof Number)) {
                return false;
            }
            return changeNumberToDouble((Number) value) < alertParametersVo.getFatal();
        } else if (SysColumnUserConstant.CONDITION_LE.equals(alertParametersVo.getLogic())) {
            if (alertParametersVo.getFatal() == null) {
                return false;
            }
            if (!(value instanceof Number)) {
                return false;
            }
            return changeNumberToDouble((Number) value) <= alertParametersVo.getFatal();
        }
        return false;
    }

    private boolean checkWarnValue(AlertParametersVO alertParametersVo, Object value) {
        if (SysColumnUserConstant.CONDITION_EQ.equals(alertParametersVo.getLogic())) {
            if (alertParametersVo.getErrorStatus() == null) {
                return false;
            }
            return value.equals(alertParametersVo.getErrorStatus());
        } else if (SysColumnUserConstant.CONDITION_NE.equals(alertParametersVo.getLogic())) {
            if (alertParametersVo.getErrorStatus() == null) {
                return false;
            }
            return !value.equals(alertParametersVo.getErrorStatus());
        } else if (SysColumnUserConstant.CONDITION_GT.equals(alertParametersVo.getLogic())) {
            if (alertParametersVo.getWarning() == null) {
                return false;
            }
            if (!(value instanceof Number)) {
                return false;
            }
            return changeNumberToDouble((Number) value) > alertParametersVo.getWarning();
        } else if (SysColumnUserConstant.CONDITION_GE.equals(alertParametersVo.getLogic())) {
            if (alertParametersVo.getWarning() == null) {
                return false;
            }
            if (!(value instanceof Number)) {
                return false;
            }
            return changeNumberToDouble((Number) value) >= alertParametersVo.getWarning();
        } else if (SysColumnUserConstant.CONDITION_LT.equals(alertParametersVo.getLogic())) {
            if (alertParametersVo.getWarning() == null) {
                return false;
            }
            if (!(value instanceof Number)) {
                return false;
            }
            return changeNumberToDouble((Number) value) < alertParametersVo.getWarning();
        } else if (SysColumnUserConstant.CONDITION_LE.equals(alertParametersVo.getLogic())) {
            if (alertParametersVo.getWarning() == null) {
                return false;
            }
            if (!(value instanceof Number)) {
                return false;
            }
            return changeNumberToDouble((Number) value) <= alertParametersVo.getWarning();
        }
        return false;
    }

    /**
     * 检查持续时间是否达标
     * @param alertConfigVo
     * @param alertParametersVo
     * @param isEndRightNow
     * @return
     */
    private boolean checkDurationTime(AlertConfigVO alertConfigVo, AlertParametersVO alertParametersVo, Boolean isEndRightNow) {
        if (isEndRightNow) {
            return true;
        }
        if (alertParametersVo.getDuration() == null) {
            return true;
        }
        // 如果第一次判断 记录起始时间
        String key = generateMapKey(alertConfigVo, alertParametersVo.getColumn());
        if (!dateMap.containsKey(key)) {
            dateMap.put(key, new Date());
            // 如果持续事件为0 返回true
            return alertParametersVo.getDuration() == NumberConstants.ZERO;
        }
        // 判断当前时间与起始时间差距
        Date startDate = dateMap.get(key);
        int betweenTime = (int) DateUtil.between(startDate, new Date(), DateUnit.MINUTE, true);
        if (betweenTime >= alertParametersVo.getDuration()) {
            return true;
        } else {
            return false;
        }
    }

    private String generateMapKey(AlertConfigVO alertConfigVo, String column){
        return StringUtils.isEmpty(alertConfigVo.getObjectDbName()) ?
                String.format(Constants.COMMON_MONITOR_KEY, alertConfigVo.getObjectId(), column) :
                String.format(Constants.INSTANCE_DB_MONITOR_KEY, alertConfigVo.getObjectId(), alertConfigVo.getObjectDbName(), column);
    }

    /**
     * 清除持续时间map里面的key
     * @param alertConfigVo
     * @param alertParametersVo
     * @return
     */
    private void deleteDurationTime(AlertConfigVO alertConfigVo, AlertParametersVO alertParametersVo) {
        String key = generateMapKey(alertConfigVo, alertParametersVo.getColumn());
        if (dateMap.containsKey(key)) {
            dateMap.remove(key);
        }
    }

    private Double getAvgValue(List<Double> history) {
        return history.stream().mapToDouble(it -> it).sum() / history.size();
    }

    private Double changeNumberToDouble(Number number) {
        return new BigDecimal(number.toString()).doubleValue();
    }

    private void createAlertMessage(AlertConfigVO alertConfigVo, String str, String strEn, String level, Boolean isEndRightNow) {
        sysMessageService.createAlertMessage(alertConfigVo.getAlarmCondition(), alertConfigVo.getRuleId(), alertConfigVo.getRuleName(), alertConfigVo.getTableName(),
                alertConfigVo.getObjectId(), alertConfigVo.getObjectName(), SysMessageConstant.KIND_ALERT, level, new Date(), str, strEn, isEndRightNow, alertConfigVo.getId(), alertConfigVo.getObjectDbName());
    }

    private void deleteAlertMessage(AlertConfigVO alertConfigVo) {
        sysMessageService.deleteAlertMessage(alertConfigVo.getRuleId(), alertConfigVo.getObjectId(), SysMessageConstant.KIND_ALERT, null, alertConfigVo.getObjectDbName());
    }

    /**
     * 字符串合并
     */
    private class StrAnex {
        private List<String> strList = new ArrayList<>();
        private void addStr(String strValue, AlertParametersVO alertParametersVo, String level, String memo, String demo, String demoOne, String demoTwo, String demoThree, String lang){
            if (StringUtil.isEmpty(memo)) {
                return;
            }
            // 国际化
            if (memo.contains(AlertRuleConstant.MESSAGE_CUR)) {
                if (Constants.EN.equals(lang)) {
                    memo = memo.replace(AlertRuleConstant.MESSAGE_CUR, strValue + alertParametersVo.getValueUnitEn());
                } else {
                    memo = memo.replace(AlertRuleConstant.MESSAGE_CUR, strValue + alertParametersVo.getValueUnitCn());
                }
            }
            if (memo.contains(AlertRuleConstant.MESSAGE_LEVEL)) {
                if (Constants.EN.equals(lang)) {
                    memo = memo.replace(AlertRuleConstant.MESSAGE_LEVEL, level);
                } else {
                    memo = memo.replace(AlertRuleConstant.MESSAGE_LEVEL, getLevelCn(level));
                }
            }
            if (memo.contains(AlertRuleConstant.MESSAGE_LEVEL_VALUE)) {
                if (Constants.EN.equals(lang)) {
                    memo = memo.replace(AlertRuleConstant.MESSAGE_LEVEL_VALUE, getLimitValue(level,alertParametersVo) + alertParametersVo.getValueUnitEn());
                } else {
                    memo = memo.replace(AlertRuleConstant.MESSAGE_LEVEL_VALUE, getLimitValue(level,alertParametersVo) + alertParametersVo.getValueUnitCn());
                }
            }
            if (memo.contains(AlertRuleConstant.MESSAGE_DEMO_VALUE)) {
                memo = memo.replace(AlertRuleConstant.MESSAGE_DEMO_VALUE,  StringUtil.trim(demo,""));
            }
            if (memo.contains(AlertRuleConstant.MESSAGE_DEMO_ONE_VALUE)) {
                memo = memo.replace(AlertRuleConstant.MESSAGE_DEMO_ONE_VALUE,  StringUtil.trim(demoOne,""));
            }
            if (memo.contains(AlertRuleConstant.MESSAGE_DEMO_TWO_VALUE)) {
                memo = memo.replace(AlertRuleConstant.MESSAGE_DEMO_TWO_VALUE,  StringUtil.trim(demoTwo,""));
            }
            if (memo.contains(AlertRuleConstant.MESSAGE_DEMO_THREE_VALUE)) {
                memo = memo.replace(AlertRuleConstant.MESSAGE_DEMO_THREE_VALUE,  StringUtil.trim(demoThree,""));
            }
            strList.add(memo);
        }

        private String getLimitValue(String level, AlertParametersVO alertParametersVo) {
            if (SysColumnUserConstant.CONDITION_NE.equals(alertParametersVo.getLogic()) || SysColumnUserConstant.CONDITION_EQ.equals(alertParametersVo.getLogic())) {
                return alertParametersVo.getErrorStatus();
            } else {
                return SysMessageConstant.LEVEL_FATAL.equals(level) ? BigDecimal.valueOf(alertParametersVo.getFatal()).toString() : BigDecimal.valueOf(alertParametersVo.getWarning()).toString();
            }
        }

        private String getLevelCn(String level) {
            String levelCn = "低风险";
            if (SysMessageConstant.LEVEL_FATAL.equals(level)) {
                levelCn = "高风险";
            }
            return levelCn;
        }

        @Override
        public String toString() {
            return String.join("\n", strList);
        }
    }

    class TimeOutCache{
        private Object value;
        private long period;
        private long nanos;
        public TimeOutCache(Object value,long nanos){
            this.value = value;
            this.nanos = nanos;
            this.period = System.currentTimeMillis() + nanos;
        }
        public Object getValue(boolean update){
            if(period == -1){
                return null;
            }else if(period == 0){
                return value;
            }else{
                long millis = System.currentTimeMillis();
                if(millis > this.period){
                    return null;
                }else if(update){
                    this.period = millis + nanos;
                }
                return value;
            }
        }
    }
}
