package com.x.provider.customer.controller.admin;

import com.x.core.web.api.R;
import com.x.core.web.controller.BaseAdminController;
import com.x.provider.api.customer.model.event.FollowEvent;
import com.x.provider.customer.component.KafkaConsumer;
import com.x.provider.customer.model.ao.UserNamePasswordRegisterAO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "用户管理端服务")
@RestController
@RequestMapping("/admin/customer")
public class CustomerAdminController extends BaseAdminController {

    private final KafkaConsumer kafkaConsumer;

    public CustomerAdminController(KafkaConsumer kafkaConsumer){
        this.kafkaConsumer = kafkaConsumer;
    }

    @ApiOperation(value = "触发关注事件")
    @PostMapping("/trigger/follow/event")
    public R<Void> register(@RequestBody @Validated FollowEvent followEvent){
        kafkaConsumer.onFollowEvent(followEvent);
        return R.ok();
    }
}
