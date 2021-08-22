package com.x.core.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

public class BaseEntity  implements Serializable {
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private Date createdOnUtc;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedOnUtc;

    public Date getCreatedOnUtc() {
        return createdOnUtc;
    }

    public void setCreatedOnUtc(Date createdOnUtc) {
        this.createdOnUtc = createdOnUtc;
    }

    public Date getUpdatedOnUtc() {
        return updatedOnUtc;
    }

    public void setUpdatedOnUtc(Date updatedOnUtc) {
        this.updatedOnUtc = updatedOnUtc;
    }
}
