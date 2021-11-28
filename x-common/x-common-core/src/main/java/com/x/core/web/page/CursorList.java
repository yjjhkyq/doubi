package com.x.core.web.page;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 表格分页数据对象
 * 
 * @author ruoyi
 */
@ApiModel
public class CursorList<T> implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 列表数据 */
    @ApiModelProperty(value = "列表数据")
    private List<T> list;

    @ApiModelProperty(value = "true 还有更多记录 反着false")
    private boolean hasMore;

    @ApiModelProperty(value = "用于下一页请求的cursor")
    private String cursor;

    /**
     * 表格数据对象
     */
    public CursorList()
    {
        this.list = new ArrayList<>();
        this.hasMore = false;
        this.cursor = "0";
    }


    /**
     *
     * @param list 列表数据
     */
    public CursorList(List<T> list, Long cursor)
    {
        this.list = list;
        this.hasMore = this.list.size() >= 0 ? true : false;
        this.cursor = cursor == null ? "" : cursor.toString();
    }

    /**
     *
     * @param list 列表数据
     */
    public CursorList(List<T> list, String cursor)
    {
        this.list = list;
        this.hasMore = this.list.size() >= 0 ? true : false;
        this.cursor = cursor;
    }

    /**
     * 分页
     *
     * @param list 列表数据
     */
    public CursorList(List<T> list, boolean hasMore, String cursor)
    {
        this.list = list;
        this.hasMore = hasMore;
        this.cursor = cursor;
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

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    public <D> CursorList<D> prepare(List<D> list){
        return new CursorList<>(list, cursor);
    }
    public <D> CursorList<D> prepare(Function<T,D> prepare){
        CursorList<D> tableDataInfo = new CursorList<>();
        tableDataInfo.hasMore = hasMore;
        tableDataInfo.cursor = cursor;
        List<D> resultList = new ArrayList<>(list.size());
        list.forEach(item -> {
            D apply = prepare.apply(item);
            if (apply != null) {
                resultList.add(apply);
            }
        });
        tableDataInfo.setList(resultList);
        return tableDataInfo;
    }
}