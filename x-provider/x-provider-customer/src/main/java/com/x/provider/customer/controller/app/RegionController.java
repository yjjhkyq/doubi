package com.x.provider.customer.controller.app;

import com.x.core.utils.BeanUtil;
import com.x.core.web.api.R;
import com.x.core.web.controller.BaseFrontendController;
import com.x.provider.customer.model.vo.RegionVO;
import com.x.provider.customer.service.RegionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "用户服务")
@Validated
@RestController
@RequestMapping("/app/region")
public class RegionController extends BaseFrontendController {

    private final RegionService regionService;

    public RegionController(RegionService regionService){
        this.regionService = regionService;
    }

    @ApiOperation(value = "获取某个国家及国家下的地区信息")
    @GetMapping("/list")
    public R<List<RegionVO>> register(@ApiParam(value = "国家id 86 中国") Integer countryId, @ApiParam(value = "层级 0 国家 1 省 2 市区 地区层级 <= leLevel ") Integer leLevel){
        return R.ok(BeanUtil.prepare(regionService.listRegion(countryId, leLevel), RegionVO.class));
    }

}
