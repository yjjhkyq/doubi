package com.x.provider.pay.service.order;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.x.core.utils.BeanUtil;
import com.x.provider.api.pay.enums.OrderStatusEnum;
import com.x.provider.api.pay.enums.PayMethodEnum;
import com.x.provider.api.pay.enums.PaymentStatusEnum;
import com.x.provider.api.pay.model.dto.CreateOrderDTO;
import com.x.provider.pay.mapper.OrderItemMapper;
import com.x.provider.pay.mapper.OrderMapper;
import com.x.provider.pay.model.domain.order.Order;
import com.x.provider.pay.model.domain.order.OrderItem;
import com.x.provider.pay.model.query.order.OrderItemQuery;
import com.x.provider.pay.model.query.order.OrderQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    public OrderServiceImpl(OrderMapper orderMapper,
                            OrderItemMapper orderItemMapper){
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
    }

    @Override
    public String createOrderNo() {
        return IdUtil.fastSimpleUUID();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Order createOrder(CreateOrderDTO createOrderAO){
        Order order = BeanUtil.prepare(createOrderAO, Order.class);
        order.setOrderNo(createOrderNo());
        order.setPaymentStatus(createOrderAO.getPaymentStatus());
        order.setPayMethodId(createOrderAO.getPayMethodId());
        if (PaymentStatusEnum.SUCCESS.getValue().equals(createOrderAO.getPaymentStatus())){
            order.setPaidDate(new Date());
            order.setOrderStatus(OrderStatusEnum.COMPLETE.getValue());
        }
        orderMapper.insert(order);
        if (!CollectionUtils.isEmpty(createOrderAO.getOrderItemList())){
            List<OrderItem> orderItemList = BeanUtil.prepare(createOrderAO.getOrderItemList(), OrderItem.class);
            orderItemList.forEach(item ->{
                item.setOrderId(order.getId());
                orderItemMapper.insert(item);
            });
        }
        return order;
    }

    @Override
    public boolean pay(String orderNo, PayMethodEnum payMethodEnum, PaymentStatusEnum paymentStatusEnum) {
        Order order = orderMapper.selectOne(buildOrderQuery(OrderQuery.builder().orderNo(orderNo).build()));
        if (!Set.of(PaymentStatusEnum.NOTPAY.getValue(), PaymentStatusEnum.USERPAYING.getValue()).contains(paymentStatusEnum.getValue())){
            return false;
        }
        order.setPaymentStatus(paymentStatusEnum.getValue());
        order.setPayMethodId(payMethodEnum.getValue());
        order.setOrderStatus(OrderStatusEnum.COMPLETE.getValue());
        orderMapper.updateById(order);
        return true;
    }

    @Override
    public boolean pay(Order order, PayMethodEnum payMethodEnum, PaymentStatusEnum paymentStatusEnum) {
        if (!Set.of(PaymentStatusEnum.NOTPAY.getValue(), PaymentStatusEnum.USERPAYING.getValue()).contains(paymentStatusEnum.getValue())){
            return false;
        }
        order.setPaymentStatus(paymentStatusEnum.getValue());
        order.setPayMethodId(payMethodEnum.getValue());
        order.setOrderStatus(OrderStatusEnum.COMPLETE.getValue());
        orderMapper.updateById(order);
        return true;
    }

    @Override
    public List<OrderItem> listOrderItem(Long orderId) {
        return listOrderItem(OrderItemQuery.builder().orderId(orderId).build());
    }

    @Override
    public Order getOrder(String orderNo) {
        return get(OrderQuery.builder().orderNo(orderNo).build());
    }

    private Order get(OrderQuery orderQuery){
        return orderMapper.selectOne(buildOrderQuery(orderQuery));
    }

    private List<OrderItem> listOrderItem(OrderItemQuery orderItemQuery){
        return orderItemMapper.selectList(buildOrderItemQuery(orderItemQuery));
    }

    private LambdaQueryWrapper<Order> buildOrderQuery(OrderQuery orderQuery){
        LambdaQueryWrapper<Order> query = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(orderQuery.getOrderNo())){
            query = query.eq(Order::getOrderNo, orderQuery.getOrderNo());
        }
        return query;
    }

    private LambdaQueryWrapper<OrderItem> buildOrderItemQuery(OrderItemQuery orderItemQuery){
        LambdaQueryWrapper<OrderItem> query = new LambdaQueryWrapper<>();
        if (orderItemQuery.getOrderId() != null){
            query = query.eq(OrderItem::getOrderId, orderItemQuery.getOrderId());
        }
        return query;
    }
}
