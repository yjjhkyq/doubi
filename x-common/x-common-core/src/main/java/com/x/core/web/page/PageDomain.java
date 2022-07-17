package com.x.core.web.page;


import org.springframework.util.StringUtils;

/**
 * 分页数据
 * 
 */
public class PageDomain
{
    /** 每页显示记录数 */
    private int pageSize;

    /** 排序列 */
    private String orderByColumn;
    /** 排序的方向 "desc" 或者 "asc". */

    private String isAsc;

    private long cursor;

    public String getOrderBy()
    {
        if (StringUtils.isEmpty(orderByColumn))
        {
            return "";
        }
        return orderByColumn + " " + isAsc;
    }

    public int getPageNum()
    {
        int pageNumber = (int)cursor / pageSize;
        return pageNumber == 0 ? 1 : pageNumber ;
    }

    public int getPageSize()
    {
        return pageSize;
    }

    public void setPageSize(int pageSize)
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

    public long getCursor() {
        return cursor;
    }

    public void setCursor(long cursor) {
        this.cursor = cursor;
    }
}
