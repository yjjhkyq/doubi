package com.x.provider.pay.controller.frontend;


import com.x.core.utils.BeanUtil;
import com.x.core.web.api.R;
import com.x.core.web.controller.BaseFrontendController;
import com.x.core.web.page.PageList;
import com.x.provider.pay.model.domain.Asset;
import com.x.provider.pay.model.domain.Transaction;
import com.x.provider.pay.model.query.AssetQuery;
import com.x.provider.pay.model.vo.AssetVO;
import com.x.provider.pay.service.PayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "支付服务")
@RestController
@RequestMapping("/frontend/pay")
public class PayController extends BaseFrontendController {

    private final PayService payService;

    public PayController(PayService payService){
        this.payService = payService;
    }

    @ApiOperation("获得用户资产")
    @GetMapping("/asset/get")
    public R<AssetVO> getAsset() {
        Asset asset = payService.getAsset(AssetQuery.builder().customerId(getCurrentCustomerId()).build());
        return R.ok(BeanUtil.prepare(asset, AssetVO.class));
    }
}
