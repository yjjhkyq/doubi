package com.x.core.web.controller;

import com.x.core.utils.SecurityUtils;

public class BaseFrontendController extends BaseController{
    protected String getBearAuthorizationToken(){
        return SecurityUtils.getBearAuthorizationToken();
    }
}
