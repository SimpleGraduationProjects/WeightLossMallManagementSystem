package com.ujcms.core.domain.base;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

/**
 * This class was generated by MyBatis Generator.
 *
 * @author MyBatis Generator
 */
public class ArticleImageBase {
    /**
     * 数据库表名
     */
    public static final String TABLE_NAME = "article_image";

    /**
     * 文章ID
     */
    @NotNull
    private Integer articleId = 0;

    /**
     * 图片URL
     */
    @Length(max = 255)
    @NotNull
    private String url = "";

    /**
     * 图片描述
     */
    @Length(max = 1000)
    @NotNull
    private String description = "";

    /**
     * 排序
     */
    @NotNull
    private Short order = 32767;

    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Short getOrder() {
        return order;
    }

    public void setOrder(Short order) {
        this.order = order;
    }
}