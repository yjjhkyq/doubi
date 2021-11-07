package com.x.core.web.page;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.x.core.utils.ServletUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 表格数据处理
 * 
 * @author ruoyi
 */
public class TableSupport
{
    private static final String CURSOR = "cursor";

    /**
     * 当前记录起始索引
     */
    public static final String PAGE = "page";

    /**
     * 每页显示记录数
     */
    public static final String PAGE_SIZE = "pageSize";

    /**
     * 排序列
     */
    public static final String ORDER_BY_COLUMN = "orderByColumn";

    /**
     * 排序的方向 "desc" 或者 "asc".
     */
    public static final String IS_ASC = "isAsc";

    private static final Integer DEFAULT_PAGE = 1;

    private static final Integer DEFAULT_PAGE_SIZE = 20;
    /**
     * 封装分页对象
     */
    public static PageDomain getPageDomain()
    {
        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageNum(ServletUtils.getParameterToInt(PAGE, DEFAULT_PAGE));
        pageDomain.setPageSize(ServletUtils.getParameterToInt(PAGE_SIZE, DEFAULT_PAGE_SIZE));
        pageDomain.setOrderByColumn(ServletUtils.getParameter(ORDER_BY_COLUMN));
        pageDomain.setIsAsc(ServletUtils.getParameter(IS_ASC));
        return pageDomain;
    }

    public static PageDomain buildPageRequest()
    {
        return getPageDomain();
    }

    public static IPage buildIPageRequest(){
        PageDomain pageDomain = getPageDomain();
        return buildIPageRequest(pageDomain);
    }

    public static IPage buildIPageRequest(PageDomain pageDomain){
        return new Page<>(pageDomain.getPageNum(), pageDomain.getPageSize(), false);
    }

    public static IPage buildIPageRequest(CursorPageRequest cursorPageRequest){
        return new Page<>(0, cursorPageRequest.getPageSize(), false);
    }

    public static <T, R> TableDataInfo<R> buildTableDataInfo(IPage<T> page, Function<T, R> funcation){
        List<R> result = new ArrayList<R>(page.getRecords().size());
        page.getRecords().forEach(s -> {
            result.add(funcation.apply(s));
        });
        return new TableDataInfo<R>(result, page.getTotal(), page.getSize());
    }

    public static <R> TableDataInfo<R> buildTableDataInfo(PageDomain pageDomain, List<R> source){
        return new TableDataInfo<R>(source, 0, pageDomain.getPageSize());
    }

    public static <T, R> TableDataInfo<R> buildTableDataInfo(PageDomain pageDomain, List<T> source, Function<T, R> funcation){
        List<R> result = new ArrayList<R>(source.size());
        source.forEach(s -> {
            result.add(funcation.apply(s));
        });
        return new TableDataInfo<R>(result, 0, pageDomain.getPageSize());
    }
}
