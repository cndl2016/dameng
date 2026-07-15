package com.dm.cn.system.entity.vo;

import com.dm.cn.common.core.utils.StringUtils;
import lombok.Data;

/**
 * 路由显示信息
 *
 * @author dameng
 */
@Data
public class MetaVO {

    /**
     * 设置该路由在侧边栏和面包屑中展示的名字
     */
    private String title;

    /**
     * 设置该路由在侧边栏和面包屑中展示的名字-英文
     */
    private String titleEn;

    /**
     * 设置该路由的图标，对应路径src/assets/icons/svg
     */
    private String icon;

    /**
     * 设置为true，则不会被 <keep-alive>缓存
     */
    private Boolean noCache;

    /**
     * 内链地址（http(s)://开头）
     */
    private String link;

    public MetaVO() {
    }

    public MetaVO(String title, String titleEn, String icon) {
        this.title = title;
        this.titleEn = titleEn;
        this.icon = icon;
    }

    public MetaVO(String title, String titleEn, String icon, String link) {
        this.title = title;
        this.titleEn = titleEn;
        this.icon = icon;
        this.link = link;
    }

    public MetaVO(String title, String titleEn, String icon, boolean noCache, String link) {
        this.title = title;
        this.titleEn = titleEn;
        this.icon = icon;
        this.noCache = noCache;
        if (StringUtils.ishttp(link)) {
            this.link = link;
        }
    }

}