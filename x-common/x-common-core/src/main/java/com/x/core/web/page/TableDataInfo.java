package com.x.core.web.page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 表格分页数据对象
 * 
 * @author ruoyi
 */
public class TableDataInfo<T> implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 总记录数 */
    private long total;

    /** 列表数据 */
    private List<T> list;

    private boolean hasMore;

    private long pageSize;

    private String cursor;


    /**
     * 表格数据对象
     */
    public TableDataInfo()
    {
        this.list = new ArrayList<>();
    }

    /**
     * 分页
     * 
     * @param list 列表数据
     * @param total 总记录数
     */
    public TableDataInfo(List<T> list, long total, long pageSize)
    {
        this.list = list;
        this.total = total;
        this.pageSize = pageSize;
        this.hasMore = this.list.size() > 0 ? true : false;
    }

    /**
     * 分页
     *
     * @param list 列表数据
     */
    public TableDataInfo(List<T> list, boolean hasMore, String cursor)
    {
        this.list = list;
        this.hasMore = hasMore;
        this.cursor = cursor;
    }

    public long getTotal()
    {
        return total;
    }

    public void setTotal(long total)
    {
        this.total = total;
    }

    public List<T> getList()
    {
        return list;
    }

    public void setList(List<T> list)
    {
        this.list = list;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    public <D> TableDataInfo<D> prepare(Function<T,D> prepare){
        TableDataInfo<D> tableDataInfo = new TableDataInfo<>();
        tableDataInfo.total = total;
        tableDataInfo.hasMore = hasMore;
        tableDataInfo.pageSize = pageSize;
        List<D> resultList = new ArrayList<>(list.size());
        list.forEach(item -> {
            resultList.add(prepare.apply(item));
        });
        tableDataInfo.setList(resultList);
        return tableDataInfo;
    }
}