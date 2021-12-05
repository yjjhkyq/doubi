package com.x.provider.cms.controller.admin;

import com.x.core.web.api.R;
import com.x.provider.cms.service.SearchService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController(value = "通用业务管理端")
@RequestMapping("/admin/search")
public class SearchAdminController {

    private final SearchService searchService;

    public SearchAdminController(SearchService searchService){
        this.searchService = searchService;
    }

    @ApiOperation(value = "初始化股票")
    @GetMapping("/security/init")
    public R<Void> initSecurity(){
        searchService.initSecurityList();
        return R.ok();
    }

    @ApiOperation(value = "初始化话题")
    @GetMapping("/topic/init")
    public R<Void> initTopic(){
        searchService.initTopicList();
        return R.ok();
    }
}
