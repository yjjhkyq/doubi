package com.x.provider.pay.service.checkout;

import com.x.provider.api.pay.model.dto.CreateTransactionDTO;
import com.x.provider.api.pay.model.dto.TransactionDTO;
import com.x.provider.pay.model.bo.payment.PayResultBO;
import com.x.provider.pay.model.domain.order.Order;
import com.x.provider.pay.service.asset.AssetCoinService;
import com.x.provider.pay.service.order.OrderService;
import com.x.provider.pay.service.shipping.ShippingService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CheckoutServiceImpl implements CheckoutService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderService orderService;
    private final AssetCoinService assetService;
    private final ShippingService shippingService;

    public CheckoutServiceImpl(KafkaTemplate<String, Object> kafkaTemplate,
                               OrderService orderService,
                               AssetCoinService assetService,
                               ShippingService shippingService){
        this.kafkaTemplate = kafkaTemplate;
        this.orderService = orderService;
        this.assetService = assetService;
        this.shippingService = shippingService;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TransactionDTO transaction(CreateTransactionDTO transaction) {
        if (transaction.getOrder() != null){
            Order orderEntity = orderService.createOrder(transaction.getOrder());
            transaction.setOrderId(orderEntity.getId());
        }
        Long transactionId = assetService.transaction(transaction);
        return TransactionDTO.builder().id(transactionId).build();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void pay(PayResultBO payResult) {
        if (!payResult.getSuccess()){
            return;
        }
        Order order = orderService.getOrder(payResult.getOrderNo());
        if(!orderService.pay(order, payResult.getPayMethod(), payResult.getPaymentStatus())){
            return;
        }
        shippingService.shipping(order);
    }
}
