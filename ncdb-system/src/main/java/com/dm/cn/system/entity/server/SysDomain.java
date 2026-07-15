package com.dm.cn.system.entity.server;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dm.cn.common.core.web.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author dameng
 * @TableName SYS_DOMAIN
 * @date 2023/05/08
 */
@TableName(value = "SYS_DOMAIN")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysDomain extends BaseEntity {
    /**
     * 主键ID
     */
    @TableId(value = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 服务器地址
     */
    private String serverHost;

    /**
     * 服务端口
     */
    private Integer serverPort;

    /**
     * 是否开启SSL(0 关闭 1 开启)
     */
    private String sslEnable;

    /**
     * 基准目录
     */
    private String baseDn;

    /**
     * 部门过滤
     */
    private String deptFilter;

    /**
     * 用户过滤
     */
    private String userFilter;

    /**
     * 管理员账号
     */
    private String adminAccount;

    /**
     * 管理员密码
     */
    private String adminPass;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 修改人
     */
    private String updateUser;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 是否开启定时同步任务
     */
    private String syncEnable;

    /**
     * 任务cron表达式
     */
    private String syncCron;
    /**
     * ad域地址
     */
    @TableField(exist = false)
    private String domainAddress;

    /**
     * 已映射用户数量
     */
    @TableField(exist = false)
    private Integer userNo;

    /**
     * 属性映射列表
     */
    @TableField(exist = false)
    private List<SysDomainMapping> mappingList;

    private static final long serialVersionUID = 1L;
}
