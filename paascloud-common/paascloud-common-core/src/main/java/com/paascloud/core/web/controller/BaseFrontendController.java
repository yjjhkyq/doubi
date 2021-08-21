package com.paascloud.core.web.controller;

import com.paascloud.core.utils.SecurityUtils;

public class BaseFrontendController extends BaseController{
    protected String getBearAuthorizationToken(){
        return SecurityUtils.getBearAuthorizationToken();
    }
}
