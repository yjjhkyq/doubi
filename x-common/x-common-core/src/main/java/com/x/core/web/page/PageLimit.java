package com.x.core.web.page;


/**
 * @author: liushenyi
 * @date: 2022/08/12/16:03
 */
public class PageLimit {

    private Integer rows;
    private Long offset;

    public PageLimit(Long offset, Integer rows){
        this.rows = rows;
        this.offset = offset;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }
}
