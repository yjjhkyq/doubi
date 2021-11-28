package com.x.provider.pay.annotation;

import com.x.provider.pay.enums.bill.BillStatus;
import com.x.provider.pay.enums.bill.BillType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GenerateBill {

    BillType billType();

    BillStatus billStatus() default BillStatus.OPENING;

}
