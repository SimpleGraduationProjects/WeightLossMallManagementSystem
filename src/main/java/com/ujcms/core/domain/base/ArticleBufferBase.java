package com.ujcms.core.domain.base;

import javax.validation.constraints.NotNull;

/**
 * This class was generated by MyBatis Generator.
 *
 * @author MyBatis Generator
 */
public class ArticleBufferBase {
    /**
     * 数据库表名
     */
    public static final String TABLE_NAME = "article_buffer";

    /**
     * 文章ID
     */
    @NotNull
    private Integer id = 0;

    /**
     * 评论次数
     */
    @NotNull
    private Integer comments = 0;

    /**
     * 下载次数
     */
    @NotNull
    private Integer downloads = 0;

    /**
     * 收藏次数
     */
    @NotNull
    private Integer favorites = 0;

    /**
     * 顶
     */
    @NotNull
    private Integer ups = 0;

    /**
     * 踩
     */
    @NotNull
    private Integer downs = 0;

    /**
     * 浏览次数
     */
    @NotNull
    private Long views = 0L;

    /**
     * 日浏览次数
     */
    @NotNull
    private Integer dayViews = 0;

    /**
     * 周浏览次数
     */
    @NotNull
    private Integer weekViews = 0;

    /**
     * 月浏览次数
     */
    @NotNull
    private Integer monthViews = 0;

    /**
     * 季浏览次数
     */
    @NotNull
    private Integer quarterViews = 0;

    /**
     * 年浏览次数
     */
    @NotNull
    private Long yearViews = 0L;

    /**
     * 统计日
     */
    @NotNull
    private Integer statDay = 0;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getComments() {
        return comments;
    }

    public void setComments(Integer comments) {
        this.comments = comments;
    }

    public Integer getDownloads() {
        return downloads;
    }

    public void setDownloads(Integer downloads) {
        this.downloads = downloads;
    }

    public Integer getFavorites() {
        return favorites;
    }

    public void setFavorites(Integer favorites) {
        this.favorites = favorites;
    }

    public Integer getUps() {
        return ups;
    }

    public void setUps(Integer ups) {
        this.ups = ups;
    }

    public Integer getDowns() {
        return downs;
    }

    public void setDowns(Integer downs) {
        this.downs = downs;
    }

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    public Integer getDayViews() {
        return dayViews;
    }

    public void setDayViews(Integer dayViews) {
        this.dayViews = dayViews;
    }

    public Integer getWeekViews() {
        return weekViews;
    }

    public void setWeekViews(Integer weekViews) {
        this.weekViews = weekViews;
    }

    public Integer getMonthViews() {
        return monthViews;
    }

    public void setMonthViews(Integer monthViews) {
        this.monthViews = monthViews;
    }

    public Integer getQuarterViews() {
        return quarterViews;
    }

    public void setQuarterViews(Integer quarterViews) {
        this.quarterViews = quarterViews;
    }

    public Long getYearViews() {
        return yearViews;
    }

    public void setYearViews(Long yearViews) {
        this.yearViews = yearViews;
    }

    public Integer getStatDay() {
        return statDay;
    }

    public void setStatDay(Integer statDay) {
        this.statDay = statDay;
    }
}