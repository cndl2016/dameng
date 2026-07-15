package com.dm.cn.common.core.web.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.dm.cn.common.core.constant.Constants;
import com.dm.cn.common.core.constant.SymbolConstants;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Entity基类
 *
 * @author dameng
 */
@Data
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 搜索值
     */
    private String searchValue;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 搜索值
     */
    @TableField(exist = false)
    private long pageSize;

    /**
     * 创建者
     */
    @TableField(exist = false)
    private long pageNum;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 备注
     */
    private String remark;

    @TableField(exist = false)
    private boolean isH2;

    @TableField(exist = false)
    private String language;

    /**
     * 请求参数
     * 暂时只有查询开始时间与结束时间2个元素
     */
    @TableField(exist = false)
    private Map<String, Object> params;

    /**
     * todo:wys 数据库类型
     */
    private String dbType;

    /**
     * 排序列
     */
    @TableField(exist = false)
    private String sortField;

    /**
     * 排序顺序
     */
    @TableField(exist = false)
    private String sortOrder;

    public Map<String, Object> getParams() {
        if (params == null) {
            //暂时只有查询开始时间与结束时间2个元素
            params = new HashMap<>(2);
            params.put("beginTime", null);
            params.put("endTime", null);
        }
        return params;
    }

    public void setParams(Map<String, Object> params) {
        if (params != null) {
            String beginTime = String.valueOf(params.get("beginTime"));
            String endTime = String.valueOf(params.get("endTime"));
            if (beginTime == null || "".equals(beginTime) || Constants.STR_NULL.equals(beginTime)) {
                beginTime = "";
                endTime = "";
            }
            //已包含时分秒信息 不再重复添加
            else if (!beginTime.contains(SymbolConstants.COLON_DELIMITER)) {
                beginTime += " 00:00:00";
                endTime += " 23:59:59";
            }
            params.put("beginTime", beginTime);
            params.put("endTime", endTime);
        }
        this.params = params;
    }
}

