package com.x.provider.mc.controller.frontend;

import com.x.core.utils.BeanUtil;
import com.x.core.web.api.R;
import com.x.core.web.controller.BaseFrontendController;
import com.x.core.web.page.PageList;
import com.x.provider.api.customer.enums.CustomerOptions;
import com.x.provider.api.customer.model.ao.ListCustomerAO;
import com.x.provider.api.customer.model.dto.CustomerDTO;
import com.x.provider.api.customer.service.CustomerRpcService;
import com.x.provider.api.mc.enums.MessageTargetType;
import com.x.provider.api.mc.model.ao.SendMessageAO;
import com.x.provider.mc.model.ao.SendImAO;
import com.x.provider.mc.model.domain.Message;
import com.x.provider.mc.model.domain.MessageReadBadge;
import com.x.provider.mc.model.domain.MessageSenderSystem;
import com.x.provider.mc.model.vo.MessageCenterConnectInfoVO;
import com.x.provider.mc.model.vo.MessageReadBadgeVO;
import com.x.provider.mc.model.vo.MessageSenderVO;
import com.x.provider.mc.model.vo.MessageVO;
import com.x.provider.mc.service.MessageService;
import com.x.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Api(tags = "用户服务")
@RestController
@RequestMapping("/frontend/message/center")
public class McController extends BaseFrontendController {

    private final MessageService messageService;
    private final CustomerRpcService customerRpcService;

    public McController(MessageService messageService,
                        CustomerRpcService customerRpcService){
        this.messageService = messageService;
        this.customerRpcService = customerRpcService;
    }

    @ApiOperation(value = "获取消息发送人信息")
    @GetMapping("/sender/list")
    public R<List<MessageSenderVO>> listSenderInfo(@RequestParam String customerIds){
        List<Long> customerIdList = StringUtil.parse(customerIds);
        if (customerIdList.isEmpty()){
            return R.ok();
        }
        return R.ok(prepare(customerIdList).values().stream().collect(Collectors.toList()));
    }

    @ApiOperation(value = "获取站内信消息角标信息")
    @GetMapping("/read/badge")
    public R<List<MessageReadBadgeVO>> listMessageReadBadge(){
        List<MessageReadBadge> messageReadBadges = messageService.listMessageReadBadge(getCurrentCustomerId());
        return R.ok(BeanUtil.prepare(messageReadBadges, MessageReadBadgeVO.class));
    }

    @ApiOperation(value = "读取站内信")
    @GetMapping("/read")
    public R<PageList<MessageVO>> readMessage(@RequestParam(required = false, defaultValue = "0") long cursor,
                                                @RequestParam int pageSize,
                                                @ApiParam(value = "发送人用户id") Long senderCustomerId
                                                ){

        PageList<Message> messageCursorList = messageService.readMessage(senderCustomerId, getCurrentCustomerId(), getPageDomain());
        return R.ok(messageCursorList.map((s) -> BeanUtil.prepare(s, MessageVO.class)));
    }

    @ApiOperation(value = "发送私信")
    @PostMapping("/im/send")
    public R<Void> sendIm(@Validated @RequestBody SendImAO sendImAO){
        messageService.sendMessage(new SendMessageAO(getCurrentCustomerId(), MessageTargetType.PERSONAL.getValue(), sendImAO.getTargetCustomerId(), sendImAO.getMessageType(), sendImAO.getAlertMsg(), sendImAO.getMsgBody()));
        return R.ok();
    }

    @ApiOperation(value = "获取连接信息,用户连接centrifugo服务器")
    @GetMapping("/connect/info")
    public R<MessageCenterConnectInfoVO> getConnectInfo(){
        Long customerId = getCurrentCustomerId();
        MessageCenterConnectInfoVO connectInfoVO = MessageCenterConnectInfoVO.builder()
                .authenticationToken(messageService.authenticationToken(customerId))
                .subscribeChannelList(new ArrayList<>(messageService.subscribeChannelList(customerId)))
                .webSocketUrl(messageService.getWebSocketUrl())
                .build();
        return R.ok(connectInfoVO);
    }

    private Map<Long, MessageSenderVO> prepare(List<Long> customerIds){
        Map<Long, MessageSenderSystem> messageSenderSystemMap = messageService.listMessageSenderSystem().stream().collect(Collectors.toMap(MessageSenderSystem::getCustomerId, item -> item));
        Map<Long, MessageSenderVO> result = new HashMap<>();
        Map<Long, CustomerDTO> data = customerRpcService.listCustomer(ListCustomerAO.builder().customerIds(customerIds).customerOptions(Arrays.asList(CustomerOptions.CUSTOMER_ATTRIBUTE.name())).build()).getData();
        data.entrySet().forEach(customer ->{
            result.put(customer.getKey(), MessageSenderVO.builder().senderUid(customer.getKey()).nickName(customer.getValue().getCustomerAttribute().getNickName())
                    .avatarUrl(customer.getValue().getCustomerAttribute().getAvatarUrl()).im(!messageSenderSystemMap.containsKey(customer.getKey())
                            || messageSenderSystemMap.get(customer.getKey()).getIm()).build());
        });
        return result;
    }
}
