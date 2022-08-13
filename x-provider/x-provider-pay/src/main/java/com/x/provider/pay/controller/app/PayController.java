package com.x.provider.pay.controller.app;


import com.x.core.utils.BeanUtil;
import com.x.core.web.api.R;
import com.x.core.web.controller.BaseFrontendController;
import com.x.provider.api.pay.enums.PayMethodEnum;
import com.x.provider.api.pay.enums.PaymentStatusEnum;
import com.x.provider.api.pay.model.dto.CreateOrderDTO;
import com.x.provider.pay.factory.vo.PayVOFactory;
import com.x.provider.pay.model.bo.payment.CreateOrderBO;
import com.x.provider.pay.model.bo.payment.CreateOrderResultBO;
import com.x.provider.pay.model.bo.payment.QueryOrderResultBO;
import com.x.provider.pay.model.domain.asset.AssetCoin;
import com.x.provider.pay.model.domain.asset.AssetVip;
import com.x.provider.pay.model.domain.order.Order;
import com.x.provider.pay.model.domain.product.Product;
import com.x.provider.pay.model.vo.*;
import com.x.provider.pay.service.asset.AssetCoinService;
import com.x.provider.pay.service.asset.AssetVipService;
import com.x.provider.pay.service.order.OrderService;
import com.x.provider.pay.service.payment.PaymentService;
import com.x.provider.pay.service.product.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "支付服务")
@Validated
@RestController
@RequestMapping("/app/pay")
public class PayController extends BaseFrontendController {

    private final AssetCoinService assetCoinService;
    private final ProductService<Product> productService;
    private final PaymentService paymentService;
    private final OrderService orderService;
    private final PayVOFactory payVOFactory;
    private final AssetVipService assetVipService;

    public PayController(AssetCoinService assetCoinService,
                         ProductService<Product> productService,
                         PaymentService paymentService,
                         OrderService orderService,
                         PayVOFactory payVOFactory,
                         AssetVipService assetVipService){
        this.assetCoinService = assetCoinService;
        this.productService = productService;
        this.paymentService = paymentService;
        this.orderService = orderService;
        this.payVOFactory = payVOFactory;
        this.assetVipService = assetVipService;
    }

    @ApiOperation("获得用户资产")
    @GetMapping("/asset/coin/get")
    public R<AssetVO> getAsset() {
        AssetCoin asset = assetCoinService.getAsset(getCurrentCustomerId());
        AssetVip assetVip = assetVipService.get(getCurrentCustomerId());
        AssetVO assetVO = AssetVO.builder()
                .assetCoin(BeanUtil.prepare(asset, AssetCoinVO.class))
                .assetVip(BeanUtil.prepare(assetVip, AssetVipVO.class))
                .build();
        return R.ok(assetVO);
    }

    @ApiOperation("查询产品列表 购买产品步骤1")
    @GetMapping("/product/list")
    public R<List<ProductVO>> listProduct(@ApiParam(value = "产品类型 商品类型 1 金币，用户充值 2 vip", required = true) @Min(1) @RequestParam Integer productType) {
        List<Product> productList = this.productService.listProduct(productType).stream().sorted(Comparator.comparing(Product::getDisplayOrder)).collect(Collectors.toList());
        return R.ok(BeanUtil.prepare(productList, ProductVO.class));
    }

    @ApiOperation("查询支付方式 购买产品步骤2")
    @GetMapping("/pay/method")
    public R<List<PayMethodVO>> listPayMethod() {
        List<PayMethodVO> payMethodList = paymentService.listPayMethod().stream().map(item -> PayMethodVO.builder().payMethod(item.getValue()).build()).collect(Collectors.toList());
        return R.ok(payMethodList);
    }

    @ApiOperation("创建订单 购买产品步骤3")
    @PostMapping("/order/create")
    public R<CreateOrderResultVO> createOrder(@RequestBody @Validated CreateOrderVO createOrderVO) throws IOException {
        CreateOrderDTO createOrderDTO = payVOFactory.prepare(createOrderVO, getCurrentCustomerId());
        createOrderDTO.setPaymentStatus(PaymentStatusEnum.USERPAYING.getValue());
        Order order = orderService.createOrder(createOrderDTO);
        CreateOrderResultBO createOrderBO = paymentService.createOrder(CreateOrderBO.builder().order(order).payMethodEnum(PayMethodEnum.valueOf(createOrderVO.getPayMethod())).build());
        return R.ok(BeanUtil.prepare(createOrderBO, CreateOrderResultVO.class));
    }

    @ApiOperation("查询订单信息，可以通过此接口查询订单支付信息 购买产品步骤4")
    @GetMapping("/order/get")
    public R<OrderVO> listProduct(@ApiParam("订单号") @Validated @NotBlank String orderNo) throws IOException {
        Order order = orderService.getOrder(orderNo);
        if (PaymentStatusEnum.USERPAYING.getValue().equals(order.getOrderStatus())){
            QueryOrderResultBO queryOrderResultBO = paymentService.queryOrder(PayMethodEnum.valueOf(order.getPayMethodId()), order.getOrderNo());
            order.setPaymentStatus(queryOrderResultBO.getPaymentStatus());
        }
        return R.ok(BeanUtil.prepare(order, OrderVO.class));
    }
}
