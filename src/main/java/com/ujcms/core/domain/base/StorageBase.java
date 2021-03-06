package com.ujcms.core.domain.base;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.lang.Nullable;

/**
 * This class was generated by MyBatis Generator.
 *
 * @author MyBatis Generator
 */
public class StorageBase {
    /**
     * 数据库表名
     */
    public static final String TABLE_NAME = "storage";

    /**
     * 发布点ID
     */
    @NotNull
    private Integer id = 0;

    /**
     * 站点ID
     */
    @Nullable
    private Integer siteId;

    /**
     * 名称
     */
    @Length(max = 50)
    @NotNull
    private String name = "";

    /**
     * 描述
     */
    @Length(max = 300)
    @Nullable
    private String description;

    /**
     * 类型(1:HTML存储,2:附件存储)
     */
    @NotNull
    private Short type = 0;

    /**
     * 存储模式(0:本地服务器,1:FTP,2:MinIO,3:阿里云,4:腾讯云,5:七牛云)
     */
    @NotNull
    private Short mode = 0;

    /**
     * 共享范围(0:本站私有,1:子站点共享,2:全局共享)
     */
    @NotNull
    private Short scope = 0;

    /**
     * 存储路径
     */
    @Length(max = 255)
    @Nullable
    private String path;

    /**
     * 访问路径
     */
    @Length(max = 255)
    @Nullable
    private String url;

    /**
     * 属性集
     */
    @Length(max = 1000)
    @Nullable
    private String attrs;

    /**
     * 排列顺序
     */
    @NotNull
    private Short order = 32767;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Nullable
    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(@Nullable Integer siteId) {
        this.siteId = siteId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    public Short getType() {
        return type;
    }

    public void setType(Short type) {
        this.type = type;
    }

    public Short getMode() {
        return mode;
    }

    public void setMode(Short mode) {
        this.mode = mode;
    }

    public Short getScope() {
        return scope;
    }

    public void setScope(Short scope) {
        this.scope = scope;
    }

    @Nullable
    public String getPath() {
        return path;
    }

    public void setPath(@Nullable String path) {
        this.path = path;
    }

    @Nullable
    public String getUrl() {
        return url;
    }

    public void setUrl(@Nullable String url) {
        this.url = url;
    }

    @Nullable
    public String getAttrs() {
        return attrs;
    }

    public void setAttrs(@Nullable String attrs) {
        this.attrs = attrs;
    }

    public Short getOrder() {
        return order;
    }

    public void setOrder(Short order) {
        this.order = order;
    }
}