package com.x.core.web.page;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 表格分页数据对象
 * 
 */
public class PageList<T> implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 总记录数 */
    private long total;

    /** 列表数据 */
    private List<T> list;

    private boolean hasMore;

    private long pageSize;

    private long cursor;

    /**
     * 表格数据对象
     */
    public PageList()
    {
        this.list = new ArrayList<>();
        this.hasMore = false;
    }

    /**
     * 分页
     * 
     * @param list 列表数据
     * @param total 总记录数
     */
    public PageList(List<T> list, long total, long pageSize)
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
    public PageList(List<T> list, int pageSize, boolean hasMore, Long cursor)
    {
        this.list = list;
        this.hasMore = hasMore;
        this.cursor = cursor;
    }

    public PageList(List<T> list, int pageSize, long cursor)
    {
        this.list = list;
        this.hasMore = list.size() == pageSize;
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

    public long getCursor() {
        return cursor;
    }

    public void setCursor(long cursor) {
        this.cursor = cursor;
    }

    @JsonIgnore
    public boolean isEmptyList(){
        return CollectionUtils.isEmpty(this.list);
    }

    public <D> PageList<D> map(Function<T,D> prepare){
        PageList<D> pageList = new PageList<>();
        pageList.total = total;
        pageList.hasMore = hasMore;
        pageList.pageSize = pageSize;
        pageList.cursor = cursor;
        List<D> resultList = new ArrayList<>(list.size());
        list.forEach(item -> {
            resultList.add(prepare.apply(item));
        });
        pageList.setList(resultList);
        return pageList;
    }

    public static  <D,T> PageList<D> map(Page<T> page, Function<T, D> map){
        PageList<D> pageList = new PageList<>();
        pageList.setTotal(page.getTotalElements());
        pageList.setHasMore(page.getContent().size() > 0);
        pageList.setPageSize(page.getSize());
        List<D> resultList = new ArrayList<>(page.getContent().size());
        page.getContent().forEach(item -> {
            resultList.add(map.apply(item));
        });
        pageList.setList(resultList);
        return pageList;
    }

    public static <D, T> PageList<D> map(PageList<T> source, Function<T, D> map){
        PageList<D> result = new PageList<>();
        result.setHasMore(source.hasMore);
        result.setPageSize(source.getPageSize());
        result.setTotal(source.getTotal());
        result.setCursor(source.getCursor());
        List<D> resultList = new ArrayList<>(source.getList().size());
        source.getList().forEach(item ->{
            resultList.add(map.apply(item));
        });
        result.setList(resultList);
        return result;
    }

    public static <D, T> PageList<D> map(PageList<T> source, List<D> dest){
        PageList<D> result = new PageList<>();
        result.setHasMore(source.hasMore);
        result.setPageSize(source.getPageSize());
        result.setTotal(source.getTotal());
        result.setCursor(source.getCursor());
        result.setList(dest);
        return result;
    }

    public static <D, T> PageList<D> mapToEmptyListPage(PageList<T> source){
        PageList<D> result = new PageList<>();
        result.setHasMore(source.hasMore);
        result.setPageSize(source.getPageSize());
        result.setTotal(source.getTotal());
        result.setCursor(source.getCursor());
        result.setList(new ArrayList<>());
        return result;
    }

    public <D> PageList<D> map(List<D> dest){
        PageList<D> result = new PageList<>();
        result.setHasMore(this.hasMore);
        result.setPageSize(this.getPageSize());
        result.setTotal(this.getTotal());
        result.setCursor(this.getCursor());
        result.setList(dest);
        return result;
    }
}