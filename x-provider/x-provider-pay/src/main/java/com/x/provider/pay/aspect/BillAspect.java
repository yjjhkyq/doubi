package com.x.provider.pay.aspect;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.x.core.exception.ApiException;
import com.x.provider.pay.annotation.GenerateBill;
import com.x.provider.pay.model.domain.Bill;
import com.x.provider.pay.service.BillService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;

@Component
@Aspect
public class BillAspect {

    @Autowired
    private BillService billService;

    @Pointcut("@annotation(com.x.provider.pay.annotation.GenerateBill)")
    public void generateBillPointCut(){
    }

    @Around("generateBillPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object res = null;
        try {
            // 执行逻辑, 保证无错误后执行
            res = joinPoint.proceed();
            // 拿到当前方法
            Signature sig = joinPoint.getSignature();
            MethodSignature methodSignature = null;
            if (!(sig instanceof MethodSignature)) {
                throw new IllegalArgumentException("该注解只能用于方法");
            }
            methodSignature = (MethodSignature) sig;
            Object target = joinPoint.getTarget();
            Method currentMethod = target.getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());

            long customerId = 0;
            long toCustomerId = 0;
            BigDecimal amount = BigDecimal.valueOf(0);
            String comment = null;
            // 获得用户id
            try {
                for (int i = 0; i < methodSignature.getParameterNames().length; i++) {
                    if (methodSignature.getParameterNames()[i].equals("customerId")) {
                        customerId = (long) joinPoint.getArgs()[i];
                    }
                    if (methodSignature.getParameterNames()[i].equals("toCustomerId")) {
                        toCustomerId = (long) joinPoint.getArgs()[i];
                    }
                    if (methodSignature.getParameterNames()[i].equals("amount")) {
                        amount = (BigDecimal) joinPoint.getArgs()[i];
                    }
                    if (methodSignature.getParameterNames()[i].equals("comment")) {
                        comment = (String) joinPoint.getArgs()[i];
                    }
                }
            } catch (Exception e) {
                throw new ApiException("请正确输入各项参数");
            }
            // 判断是否拿到正确的参数
            // 这里认为customerId = 0是系统发起的
            if (toCustomerId == 0) {
                throw new ApiException("未正确输入订单产生者参数");
            }
            if (customerId == 0) {
                comment = "系统";
            }

            // 拿到注解
            GenerateBill generateBillAnnotation = currentMethod.getAnnotation(GenerateBill.class);

            String billSN = IdWorker.getTimeId();

            Bill bill = Bill.builder()
                    .serialNumber(billSN)
                    .customerId(customerId)
                    .toCustomerId(toCustomerId)
                    .amount(amount)
                    .type(generateBillAnnotation.billType())
                    .status(generateBillAnnotation.billStatus())
                    .comment(comment)
                    .build();

            System.out.println(bill);

            billService.insert(bill);

            return bill;

        } catch (Throwable e) {
            throw e;
        }


    }



}
