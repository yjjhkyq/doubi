package com.x.core.web.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.x.core.constant.Constants;
import com.x.core.utils.ApiAssetUtil;
import com.x.core.utils.DateUtils;
import com.x.core.utils.SecurityUtils;
import com.x.core.web.api.ResultCode;
import com.x.core.web.page.PageDomain;
import com.x.core.web.page.PageHelper;
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
        long currentCustomerId = getCurrentCustomerIdAndNotCheckLogin();
        ApiAssetUtil.isTrue(currentCustomerId > 0, ResultCode.UNAUTHORIZED);
        return currentCustomerId;
    }

    protected long getCurrentCustomerIdAndNotCheckLogin(){
        long currentCustomerId = SecurityUtils.getCurrentCustomerId();
        return currentCustomerId;
    }

    protected long getPage(){
        PageDomain pageDomain = PageHelper.buildPageRequest();
        return pageDomain.getPageNum();
    }

    protected long getDefaultFrontendPageSize(){
        return Constants.DEFAULT_FRONTEND_PAGE_SIZE;
    }

    protected PageDomain getPageDomain(){
        return PageHelper.buildPageRequest();
    }

    protected IPage buildIPageRequest(){
        return PageHelper.buildIPageRequest();
    }
}
