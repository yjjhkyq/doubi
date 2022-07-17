package com.x.provider.mc.controller.frontend;

import com.x.core.utils.BeanUtil;
import com.x.core.web.api.R;
import com.x.core.web.controller.BaseFrontendController;
import com.x.core.web.page.PageList;
import com.x.provider.api.customer.service.CustomerRpcService;
import com.x.provider.api.mc.model.ao.SendMessageAO;
import com.x.provider.api.mc.model.protocol.MessageClassEnum;
import com.x.provider.mc.model.ao.MarkMessageAsReadAO;
import com.x.provider.mc.model.ao.SendImAO;
import com.x.provider.mc.model.domain.Conversation;
import com.x.provider.mc.model.domain.Message;
import com.x.provider.mc.model.vo.ConnectInfoVO;
import com.x.provider.mc.model.vo.ConversationVO;
import com.x.provider.mc.model.vo.MessageVO;
import com.x.provider.mc.service.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Api(tags = "消息服務")
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

    @ApiOperation(value = "获取连接信息,用户连接centrifugo服务器")
    @GetMapping("/connect/info")
    public R<ConnectInfoVO> getConnectInfo(){
        Long customerId = getCurrentCustomerId();
        ConnectInfoVO connectInfoVO = ConnectInfoVO.builder()
                .authenticationToken(messageService.authenticationToken(customerId))
                .subscribeChannelList(new ArrayList<>(messageService.subscribeChannelList(customerId)))
                .webSocketUrl(messageService.getWebSocketUrl())
                .build();
        return R.ok(connectInfoVO);
    }

    @ApiOperation(value = "获取会话列表")
    @GetMapping("/conversation/list")
    public R<PageList<ConversationVO>> listConversation(@RequestParam(required = false, defaultValue = "0") long cursor,
                                                   @RequestParam int pageSize){
        PageList<Conversation> conversationList = messageService.listConversation(getCurrentCustomerId(), getPageDomain());
        List<ConversationVO> resultList = BeanUtil.prepare(messageService.prepare(conversationList.getList()), ConversationVO.class);
        return R.ok(conversationList.map(resultList));
    }

    @ApiOperation(value = "获取会话列表")
    @GetMapping("/conversation/get")
    public R<ConversationVO> getConversation(@ApiParam(value = "会话id") @RequestParam String conversationId){
        Conversation conversation = messageService.getConversation(conversationId, getCurrentCustomerId());
        return R.ok(BeanUtil.prepare(messageService.prepare(conversation), ConversationVO.class));
    }


    @ApiOperation(value = "获取历史消息")
    @GetMapping("/message/list")
    public R<PageList<MessageVO>> listMessage(@RequestParam(required = false, defaultValue = "0") long cursor,
                                              @RequestParam int pageSize,
                                              @ApiParam(value = "会话id") String conversationId){
        PageList<Message> messageCursorList = messageService.listMessage(conversationId, getCurrentCustomerId(), getPageDomain());
        List<MessageVO> resultList = BeanUtil.prepare(messageService.prepareMessage(messageCursorList.getList()), MessageVO.class);
        return R.ok(messageCursorList.map(resultList));
    }

    @ApiOperation(value = "发送私信,返回私信id")
    @PostMapping("/message/send")
    public R<Long> markMessageAsRead(@Validated @RequestBody SendImAO sendImAO){
        SendMessageAO sendMessageAO = BeanUtil.prepare(sendImAO, SendMessageAO.class);
        sendMessageAO.setFromCustomerId(getCurrentCustomerId());
        sendMessageAO.setMessageClass(MessageClassEnum.IM.getValue());
        sendMessageAO.setOnlineUserOnly(false);
        Long id = messageService.sendMessage(sendMessageAO);
        return R.ok(id);
    }

    @ApiOperation(value = "标记会话中的所有消息为已读,每次打开会话后都应该调用此接口")
    @PostMapping("/message/mark/as/read")
    public R<Void> markMessageAsRead(@Validated @RequestBody MarkMessageAsReadAO markMessageAsReadAO){
        messageService.markMessageAsRead(markMessageAsReadAO, getCurrentCustomerId());
        return R.ok();
    }

    @ApiOperation(value = "获取消息总未读数")
    @GetMapping("/message/total/unread/count")
    public R<Long> getTotalUnreadMessageCount(){
        return R.ok(messageService.getTotalUnreadMessageCount(getCurrentCustomerId()));
    }
}
