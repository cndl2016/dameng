package com.dm.cn.base.entity.vo;

import com.dm.cn.base.constant.AlertParametersConstant;
import com.dm.framework.common.constant.EnumInterface;
import com.dm.framework.common.model.DictVO;
import com.dm.framework.common.util.StringUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 告警规则配置表表单模型对象
 *
 * @author Auto-Coder
 * @date 2023-03-16
 */
@Data
public class AlertParametersVO {

    /**
     * 配置项类型
     * on-off开关，
     * on-off-value开关阈值，
     * on-off-value-time开关阀值时间，
     * on-off-time开关时间
     */
    private String configType;

    /**
     * 严重阈值
     */
    private Double fatal;

    /**
     * 严重阈值最终值，根据阈值单位换算获得，如果是时分秒，单位换算成秒
     */
    private Double fatalVal;

    /**
     * 严重阈值开关
     */
    private Boolean fatalStatus;

    /**
     * 警告阈值
     */
    private Double warning;

    /**
     * 警告阈值最终值，根据阈值单位换算获得，如果是时分秒，单位换算成秒
     */
    private Double warningVal;

    /**
     * 警告阈值开关
     */
    private Boolean warningStatus;

    /**
     * 持续时间,单位分钟
     */
    private Integer duration;

    /**
     * 持续时间单位：时h、分m、秒s
     */
    private String durationUnit;

    /**
     * 持续时间最终值，单位秒
     */
    private Integer durationVal;

    /**
     * 阈值单位，如个、百分比、小时、天
     */
    private String valueUnit;

    /**
     * 阈值单位中文
     */
    private String valueUnitCn;

    /**
     * 阈值单位英文
     */
    private String valueUnitEn;

    /**
     * 阈值MAX，如果过null，最大值为阈值单位的默认值
     */
    private Double valueMax;

    /**
     * 阈值可选单位
     */
    private List<DictVO> kinds;

    /**
     * 错误状态，yes严重，no正常；状态类独有字段，阈值类无
     */
    private String errorStatus;

    /**
     * 条件逻辑符
     */
    private String logic;

    /**
     * 条件描述
     */
    private String memo;

    /**
     * 条件列名
     */
    private String column;

    public void initParamDatas() {
        // 持续时间最终值
        if (StringUtil.isNotEmpty(durationUnit) && null != duration) {
            AlertParametersConstant.DurationUnit durUnit = EnumInterface.get(this.durationUnit, AlertParametersConstant.DurationUnit.values());
            setDurationVal(duration * durUnit.getToSec());
        }
        if (StringUtil.isNotEmpty(this.valueUnit)) {
            AlertParametersConstant.ValueUnit valueUnit = EnumInterface.get(this.valueUnit, AlertParametersConstant.ValueUnit.values());
            if (null == valueUnit) {
                return;
            }
            setValueUnitCn(valueUnit.getLabel());
            setValueUnitEn(valueUnit.getLabelEn());
            // 阈值最大值
            if (null == valueMax) {
                setValueMax(Double.valueOf(valueUnit.getDef()));
            }
            // 严重阈值最终值
            setFatalVal(initVal(fatal, valueUnit));
            // 警告阈值最终值
            setWarningVal(initVal(warning, valueUnit));
            // 阈值可选单位
            List<DictVO> kinds = new ArrayList<>();
            AlertParametersConstant.ValueUnit[] valueUnits = AlertParametersConstant.ValueUnit.values();
            for (AlertParametersConstant.ValueUnit dict : valueUnits) {
                if (valueUnit.getKind().equals(dict.getKind())) {
                    DictVO dictVo = new DictVO();
                    dictVo.setValue(dict.getValue());
                    dictVo.setLabel(dict.getLabel());
                    kinds.add(dictVo);
                }
            }
            if (kinds.size() > 1) {
                setKinds(kinds);
            }
        }
    }

    public Double initVal(Double val, AlertParametersConstant.ValueUnit valueUnit) {
        if (null != val) {
            if (AlertParametersConstant.ValueUnit.PERCENT.eq(valueUnit.getValue())) {
                return val/100;
            } else if (AlertParametersConstant.ValueUnit.HOUR.eq(valueUnit.getValue())) {
                return val * 60 *60;
            } else if (AlertParametersConstant.ValueUnit.MINUTE.eq(valueUnit.getValue())) {
                return val * 60;
            } else {
                return val;
            }
        } else {
            return null;
        }
    }

}

