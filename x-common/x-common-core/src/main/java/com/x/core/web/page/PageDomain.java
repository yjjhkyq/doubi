package com.x.core.web.page;


import org.springframework.util.StringUtils;

/**
 * 分页数据
 * 
 */
public class PageDomain
{
    /** 当前记录起始索引 */
    private Integer pageNum;

    /** 每页显示记录数 */
    private Integer pageSize;

    /** 排序列 */
    private String orderByColumn;
    /** 排序的方向 "desc" 或者 "asc". */

    private String isAsc;

    private String cursorStr;

    private long cursorLong;

    public String getOrderBy()
    {
        if (StringUtils.isEmpty(orderByColumn))
        {
            return "";
        }
        return orderByColumn + " " + isAsc;
    }

    public Integer getPageNum()
    {
        return pageNum;
    }

    public void setPageNum(Integer pageNum)
    {
        this.pageNum = pageNum;
    }

    public Integer getPageSize()
    {
        return pageSize;
    }

    public void setPageSize(Integer pageSize)
    {
        this.pageSize = pageSize;
    }

    public String getOrderByColumn()
    {
        return orderByColumn;
    }

    public void setOrderByColumn(String orderByColumn)
    {
        this.orderByColumn = orderByColumn;
    }

    public String getIsAsc()
    {
        return isAsc;
    }

    public void setIsAsc(String isAsc)
    {
        this.isAsc = isAsc;
    }

    public String getCursorStr() {
        return cursorStr;
    }

    public void setCursorStr(String cursorStr) {
        this.cursorStr = cursorStr;
    }

    public long getCursorLong() {
        return cursorLong;
    }

    public void setCursorLong(long cursorLong) {
        this.cursorLong = cursorLong;
    }
}
