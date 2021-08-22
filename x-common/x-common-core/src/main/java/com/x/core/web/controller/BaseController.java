package com.x.core.web.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.x.core.constant.Constants;
import com.x.core.utils.ApiAssetUtil;
import com.x.core.utils.DateUtils;
import com.x.core.utils.SecurityUtils;
import com.x.core.web.api.ResultCode;
import com.x.core.web.page.PageDomain;
import com.x.core.web.page.TableSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;
import java.util.Date;

/**
 * web层通用数据处理
 * 
 * @author ruoyi
 */
public class BaseController
{
    protected final Logger logger = LoggerFactory.getLogger(BaseController.class);

    /**
     * 将前台传递过来的日期格式的字符串，自动转化为Date类型
     */
    @InitBinder
    public void initBinder(WebDataBinder binder)
    {
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport()
        {
            @Override
            public void setAsText(String text)
            {
                setValue(DateUtils.parseDate(text));
            }
        });
    }

    protected long getCurrentCustomerId(){
        long currentCustomerId = SecurityUtils.getCurrentCustomerId();
        ApiAssetUtil.isTrue(currentCustomerId > 0, ResultCode.UNAUTHORIZED);
        return currentCustomerId;
    }
//    /**
//     * 设置请求分页数据
//     */
//    protected void startPage()
//    {
//        PageDomain pageDomain = TableSupport.buildPageRequest();
//        Integer pageNum = pageDomain.getPageNum();
//        Integer pageSize = pageDomain.getPageSize();
//        if (pageNum != null && pageSize != null)
//        {
//            String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
//            PageHelper.startPage(pageNum, pageSize, orderBy);
//        }
//    }

    protected long getPage(){
        PageDomain pageDomain = TableSupport.buildPageRequest();
        return pageDomain.getPageNum();
    }

    protected long getDefaultFrontendPageSize(){
        return Constants.DEFAULT_FRONTEND_PAGE_SIZE;
    }

//    /**
//     * 响应请求分页数据
//     */
//    @SuppressWarnings({ "rawtypes", "unchecked" })
//    protected TableDataInfo getDataTable(List<?> list)
//    {
//        TableDataInfo rspData = new TableDataInfo();
//        rspData.setRows(list);
//        rspData.setTotal(new PageInfo(list).getTotal());
//        return rspData;
//    }

    protected IPage buildIPageRequest(){
        return TableSupport.buildIPageRequest();
    }
}
