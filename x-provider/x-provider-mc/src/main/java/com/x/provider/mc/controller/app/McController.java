package com.x.provider.mc.controller.app;

import com.x.core.utils.BeanUtil;
import com.x.core.web.api.R;
import com.x.core.web.controller.BaseFrontendController;
import com.x.core.web.page.PageList;
import com.x.provider.api.customer.service.CustomerRpcService;
import com.x.provider.api.mc.model.dto.SendMessageRequestDTO;
import com.x.provider.api.mc.model.protocol.MessageClassEnum;
import com.x.provider.mc.model.bo.GetConversationRequestBO;
import com.x.provider.mc.model.bo.MessageBO;
import com.x.provider.mc.model.vo.MarkMessageAsReadRequestVO;
import com.x.provider.mc.model.vo.SendImRequestAO;
import com.x.provider.mc.model.domain.Conversation;
import com.x.provider.mc.model.domain.Message;
import com.x.provider.mc.model.vo.ConnectInfoVO;
import com.x.provider.mc.model.vo.ConversationVO;
import com.x.provider.mc.model.vo.MessageVO;
import com.x.provider.mc.service.MessageEngineService;
import com.x.provider.mc.service.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "消息服務")
@RestController
@RequestMapping("/app/message/center")
public class McController extends BaseFrontendController {

    private final MessageService messageService;
    private final MessageEngineService messageEngineService;

    public McController(MessageService messageService,
                        MessageEngineService messageEngineService){
        this.messageService = messageService;
        this.messageEngineService = messageEngineService;
    }

    @ApiOperation(value = "获取连接信息,用户连接centrifugo服务器")
    @GetMapping("/connect/info")
    public R<ConnectInfoVO> getConnectInfo(@ApiParam(value = "2 web socket x 1 centrifugo", required = true) Integer webSocketEngineType){
        Long customerId = getCurrentCustomerId();
        return R.ok(BeanUtil.prepare(messageEngineService.listConnectionInfo(customerId).stream().filter(item -> webSocketEngineType.equals(item.getWebSocketEngineType())).findFirst().get(), ConnectInfoVO.class));
    }

    @ApiOperation(value = "获取会话列表")
    @GetMapping("/conversation/list")
    public R<PageList<ConversationVO>> listConversation(@RequestParam(required = false, defaultValue = "0") long cursor,
                                                   @RequestParam int pageSize){
        messageService.initSystemConversation(getCurrentCustomerId());
        PageList<Conversation> conversationList = messageService.listConversation(getCurrentCustomerId(), getPageDomain());
        List<ConversationVO> resultList = BeanUtil.prepare(messageService.prepare(conversationList.getList()), ConversationVO.class);
        return R.ok(conversationList.map(resultList));
    }

    @ApiOperation(value = "获取会话列表")
    @GetMapping("/conversation/get")
    public R<ConversationVO> getConversation(@ApiParam(value = "会话id", defaultValue = "") @RequestParam(required = false, defaultValue = "") String conversationId,
                                             @ApiParam(value = "用户id") @RequestParam(required = false, defaultValue = "0") Long customerId,
                                             @ApiParam(value = "群组id") @RequestParam(required = false, defaultValue = "0") Long groupId){
        Conversation conversation = messageService.getConversation(GetConversationRequestBO.builder().conversationId(conversationId).customerId(customerId)
                .groupId(groupId).build());
        return R.ok(BeanUtil.prepare(messageService.prepare(conversation), ConversationVO.class));
    }


    @ApiOperation(value = "获取历史消息")
    @GetMapping("/message/list")
    public R<PageList<MessageVO>> listMessage(@RequestParam(required = false, defaultValue = "0") long cursor,
                                              @RequestParam int pageSize,
                                              @ApiParam(value = "会话id") @RequestParam String conversationId){
        PageList<Message> messageCursorList = messageService.listMessage(conversationId, getCurrentCustomerId(), getPageDomain());
        List<MessageVO> resultList = BeanUtil.prepare(messageService.prepareMessage(messageCursorList.getList()), MessageVO.class);
        return R.ok(messageCursorList.map(resultList));
    }

    @ApiOperation(value = "发送私信,返回私信id")
    @PostMapping("/message/send")
    public R<MessageVO> sendMessage(@Validated @RequestBody SendImRequestAO sendImAO){
        SendMessageRequestDTO sendMessageAO = BeanUtil.prepare(sendImAO, SendMessageRequestDTO.class);
        sendMessageAO.setFromCustomerId(getCurrentCustomerId());
        sendMessageAO.setMessageClass(MessageClassEnum.IM.getValue());
        sendMessageAO.setOnlineUserOnly(false);
        final MessageBO messageBO = messageService.sendMessage(sendMessageAO);
        return R.ok(BeanUtil.prepare(messageBO, MessageVO.class));
    }

    @ApiOperation(value = "标记会话中的所有消息为已读,每次打开会话后都应该调用此接口")
    @PostMapping("/message/mark/as/read")
    public R<Void> markMessageAsRead(@Validated @RequestBody MarkMessageAsReadRequestVO markMessageAsReadAO){
        messageService.markMessageAsRead(markMessageAsReadAO, getCurrentCustomerId());
        return R.ok();
    }

    @ApiOperation(value = "获取消息总未读数")
    @GetMapping("/message/total/unread/count")
    public R<Long> getTotalUnreadMessageCount(){
        return R.ok(messageService.getTotalUnreadMessageCount(getCurrentCustomerId()));
    }
}
