package com.x.provider.pay.controller.frontend;


import com.x.core.web.page.PageList;
import com.x.provider.pay.annotation.PayToken;
import com.x.provider.pay.model.domain.Bill;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "订单服务")
@RestController
@RequestMapping("/frontend/bill")
public class BillController {

    @ApiOperation("获得订单")
    @GetMapping("/")
    @PayToken
    public PageList<Bill> getBill() {
        return null;
    }
}
