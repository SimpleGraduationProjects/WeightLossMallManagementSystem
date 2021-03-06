package com.ujcms.core.domain.base;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.lang.Nullable;

/**
 * This class was generated by MyBatis Generator.
 *
 * @author MyBatis Generator
 */
public class ChannelBase {
    /**
     * 数据库表名
     */
    public static final String TABLE_NAME = "channel";

    /**
     * 栏目ID
     */
    @NotNull
    private Integer id = 0;

    /**
     * 站点ID
     */
    @NotNull
    private Integer siteId = 0;

    /**
     * 上级栏目ID
     */
    @Nullable
    private Integer parentId;

    /**
     * 栏目模型ID
     */
    @NotNull
    private Integer channelModelId = 0;

    /**
     * 文章模型ID
     */
    @NotNull
    private Integer articleModelId = 0;

    /**
     * 名称
     */
    @Length(max = 50)
    @NotNull
    private String name = "";

    /**
     * 别名
     */
    @Length(max = 50)
    @NotNull
    private String alias = "";

    /**
     * 是否导航菜单
     */
    @NotNull
    private Boolean nav = true;

    /**
     * 类型(1:常规栏目,2:单页栏目,3:转向链接,4:链接到第一篇文章,5:链接到第一个子栏目)
     */
    @NotNull
    private Short type = 1;

    /**
     * 层级
     */
    @NotNull
    private Short depth = 1;

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

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    @Nullable
    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(@Nullable Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getChannelModelId() {
        return channelModelId;
    }

    public void setChannelModelId(Integer channelModelId) {
        this.channelModelId = channelModelId;
    }

    public Integer getArticleModelId() {
        return articleModelId;
    }

    public void setArticleModelId(Integer articleModelId) {
        this.articleModelId = articleModelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Boolean getNav() {
        return nav;
    }

    public void setNav(Boolean nav) {
        this.nav = nav;
    }

    public Short getType() {
        return type;
    }

    public void setType(Short type) {
        this.type = type;
    }

    public Short getDepth() {
        return depth;
    }

    public void setDepth(Short depth) {
        this.depth = depth;
    }

    public Short getOrder() {
        return order;
    }

    public void setOrder(Short order) {
        this.order = order;
    }
}