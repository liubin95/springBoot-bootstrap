package com.caomu.bootstrap.domain;

import java.time.LocalDateTime;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.boot.context.properties.bind.Bindable;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 数据库实体定义基类
 *
 * @author 刘斌
 */
public class BaseEntity {
    public static final String ID = "id";
    public static final String DELETED = "deleted";
    public static final String CREATOR_ID = "creator_id";
    public static final String CREATE_TIME = "create_time";
    public static final String UPDATER_ID = "updater_id";
    public static final String UPDATE_TIME = "update_time";


    @TableLogic
    @JsonIgnore
    public Integer deleted;
    private Long id;
    private Long creatorId;

    private LocalDateTime createTime;

    private Long updaterId;

    private LocalDateTime updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Long getUpdaterId() {
        return updaterId;
    }

    public void setUpdaterId(Long updaterId) {
        this.updaterId = updaterId;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public final String toString() {
        Bindable.ofInstance(new Object()).getValue().get();
        return ToStringBuilder.reflectionToString(this);
    }
}
