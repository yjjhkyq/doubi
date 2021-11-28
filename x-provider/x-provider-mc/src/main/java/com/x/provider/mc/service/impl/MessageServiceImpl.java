package com.x.provider.mc.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.x.core.exception.ApiException;
import com.x.core.utils.ApiAssetUtil;
import com.x.core.utils.BeanUtil;
import com.x.core.web.api.ResultCode;
import com.x.core.web.page.CursorList;
import com.x.core.web.page.TableSupport;
import com.x.provider.api.mc.enums.MessageTargetType;
import com.x.provider.mc.configure.ApplicationConfig;
import com.x.provider.mc.mapper.MessageMapper;
import com.x.provider.mc.mapper.MessageReadBadgeMapper;
import com.x.provider.mc.mapper.MessageSenderSystemMapper;
import com.x.provider.api.mc.model.ao.SendMessageAO;
import com.x.provider.mc.model.ao.ReadMessageAO;
import com.x.provider.mc.model.domain.Message;
import com.x.provider.mc.model.domain.MessageReadBadge;
import com.x.provider.mc.model.domain.MessageSenderSystem;
import com.x.provider.mc.service.MessageEngineService;
import com.x.provider.mc.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    private final MessageMapper messageMapper;
    private final MessageSenderSystemMapper messageSenderSystemMapper;
    private final MessageReadBadgeMapper messageReadBadgeMapper;
    private final Executor executor;
    private final MessageEngineService messageEngineService;
    private final ApplicationConfig applicationConfig;

    public MessageServiceImpl(MessageMapper messageMapper,
                              MessageSenderSystemMapper messageSenderSystemMapper,
                              MessageReadBadgeMapper messageReadBadgeMapper,
                              @Qualifier("mcDefaultExecutor") Executor executor,
                              MessageEngineService messageEngineService,
                              ApplicationConfig applicationConfig){
        this.messageMapper = messageMapper;
        this.messageSenderSystemMapper = messageSenderSystemMapper;
        this.messageReadBadgeMapper = messageReadBadgeMapper;
        this.executor = executor;
        this.messageEngineService = messageEngineService;
        this.applicationConfig = applicationConfig;
    }

    @Override
    public List<MessageSenderSystem> listMessageSenderSystem() {
        return listMessageSenderSystemBy(null);
    }

    @Override
    public CursorList<Message> readMessage(ReadMessageAO readMessageAO, Long customerId) {
        Optional<MessageSenderSystem> messageSenderSystem = getMessageSenderSystem(readMessageAO.getSenderUid());
        Integer targetType = messageSenderSystem.isPresent() ? messageSenderSystem.get().getTargetType() : MessageTargetType.PERSONAL.getValue();
        Long targetId = getTargetId(customerId, messageSenderSystem.isPresent() ? messageSenderSystem.get().getTargetId() : customerId, targetType);
        IPage page = TableSupport.buildIPageRequest(readMessageAO);
        LambdaQueryWrapper<Message> query = new LambdaQueryWrapper<Message>().eq(Message::getSenderUid, readMessageAO.getSenderUid()).eq(Message::getTargetId, targetId)
                .gt(Message::getExpireDate, new Date()).lt(Message::getId, readMessageAO.getDescOrderCursor())
                .orderByDesc(Message::getId);
        List<Message> records = messageMapper.selectPage(page, query).getRecords();
        if (CollectionUtils.isEmpty(records)){
            return new CursorList<>();
        }
        executor.execute(() -> {
            updateMessageBadge(readMessageAO.getSenderUid(), targetId, customerId, true);
        });
        return new CursorList<>(records, CollectionUtils.lastElement(records).getId());
    }

    public void updateMessageBadge(Long senderUid, Long targetUid, Long currentCustomerId, boolean messageRead) {
        Optional<MessageReadBadge> messageReadBadge = getMessageReadBadgeBy(currentCustomerId, senderUid);
        Optional<Message> newestNotify = getNewestMessage(targetUid, senderUid);
        Long readEndNotifyId = newestNotify.isPresent() ? newestNotify.get().getId() : 0;
        String alterMsg = newestNotify.isPresent() ? newestNotify.get().getAlertMsg() :" ";
        if (!messageRead ){
            if (messageReadBadge.isEmpty() || !messageReadBadge.get().getReadEndNotifyId().equals(readEndNotifyId)){
                if (messageReadBadge.isEmpty()){
                    messageReadBadgeMapper.insert(MessageReadBadge.builder().senderUid(senderUid).alertMsg(alterMsg).customerId(currentCustomerId).hasUnreadMsg(!newestNotify.isEmpty()).readEndNotifyId(readEndNotifyId).build());
                }
                else{
                    messageReadBadgeMapper.updateById(MessageReadBadge.builder().id(messageReadBadge.get().getId()).alertMsg(alterMsg).hasUnreadMsg(!newestNotify.isEmpty()).readEndNotifyId(readEndNotifyId).build());
                }
            }
            return;
        }
        if (messageReadBadge.isPresent()){
            if (!messageReadBadge.get().getReadEndNotifyId().equals(readEndNotifyId) || messageReadBadge.get().getHasUnreadMsg().equals(true)){
                messageReadBadgeMapper.updateById(MessageReadBadge.builder().id(messageReadBadge.get().getId()).hasUnreadMsg(false).readEndNotifyId(readEndNotifyId).build());
            }
        }
        else {
            messageReadBadgeMapper.insert(MessageReadBadge.builder().senderUid(senderUid).alertMsg(alterMsg).customerId(currentCustomerId).hasUnreadMsg(false).readEndNotifyId(readEndNotifyId).build());
        }
    }

    @Transactional
    @Override
    public void sendMessage(SendMessageAO sendMessageAO) {
        Optional<MessageSenderSystem> notifySender = getMessageSenderSystem(sendMessageAO.getSenderUid());
        Integer messageExpireDays = notifySender.isPresent() ? notifySender.get().getMsgExpireDays() : applicationConfig.getMessageExpireDays();
        if (notifySender.isPresent()) {
            Message message = BeanUtil.prepare(sendMessageAO, Message.class);
            message.setExpireDate(DateUtils.addDays(new Date(), messageExpireDays));
            messageMapper.insert(message);
            messageEngineService.sendMessage(MessageTargetType.valueOf(sendMessageAO.getMessageTargetType()), message);
        }
        if (sendMessageAO.getMessageTargetType().equals(MessageTargetType.PERSONAL.getValue())) {
            Optional<MessageReadBadge> messageReadBadge = getMessageReadBadgeBy(sendMessageAO.getTargetId(), sendMessageAO.getSenderUid());
            if (messageReadBadge.isPresent()) {
                messageReadBadgeMapper.updateById(MessageReadBadge.builder().id(messageReadBadge.get().getId()).hasUnreadMsg(true).alertMsg(sendMessageAO.getAlertMsg()).build());
            } else {
                messageReadBadgeMapper.insert(MessageReadBadge.builder().hasUnreadMsg(true).alertMsg(sendMessageAO.getAlertMsg()).senderUid(sendMessageAO.getSenderUid()).customerId(sendMessageAO.getTargetId()).build());
            }
        }
    }

    @Override
    public List<MessageReadBadge> listMessageReadBadge(Long customerId) {
        listMessageSenderSystem().stream().filter(item -> !item.getTargetType().equals(MessageTargetType.PERSONAL.getValue())).forEach(item ->{
            updateMessageBadge(item.getCustomerId(), getTargetId(customerId, item.getTargetId(), item.getTargetType()), customerId, false);
        });
        return listMessageReadBadgeBy(customerId, null, null);
    }

    @Override
    public String getWebSocketUrl() {
        return messageEngineService.getWebSocketUrl();
    }

    @Override
    public String authenticationToken(Long customerId) {
        return messageEngineService.authenticationToken(customerId);
    }

    @Override
    public Set<String> subscribeChannelList(Long customerId) {
        Set<String> result = new HashSet<>();
        listMessageSenderSystem().forEach(item ->{
            result.add(messageEngineService.getChannelName(getTargetId(customerId, item.getTargetId(), item.getTargetType()), MessageTargetType.valueOf(item.getTargetType())));
        });
        result.add(messageEngineService.getChannelName(customerId, MessageTargetType.PERSONAL));
        return result;
    }

    private Long getTargetId(Long currentCustomerId, Long targetId, Integer targetTpe){
        if (targetTpe.equals(MessageTargetType.PERSONAL.getValue())){
            return currentCustomerId;
        }
        return targetId;
    }

    private Optional<MessageReadBadge> getMessageReadBadgeBy(Long targetCustomerId, Long senderUid){
        LambdaQueryWrapper<MessageReadBadge> query = buildMessageReadBadge(targetCustomerId, senderUid, null);
        return Optional.ofNullable(messageReadBadgeMapper.selectOne(query));
    }

    private List<MessageReadBadge> listMessageReadBadgeBy(Long targetUid, Long senderUid, Set<Long> targetIdList){
        LambdaQueryWrapper<MessageReadBadge> query = buildMessageReadBadge(targetUid, senderUid, targetIdList);
        return messageReadBadgeMapper.selectList(query);
    }

    public Optional<Message> getNewestMessage(Long targetUid, Long senderUid){
        LambdaQueryWrapper<Message> query = buildMessageQuery(targetUid, senderUid).orderByDesc(Message::getId).last(" limit 1 ");
        return Optional.ofNullable(messageMapper.selectOne(query));
    }

    private LambdaQueryWrapper<Message> buildMessageQuery(Long targetUid, Long senderUid) {
        LambdaQueryWrapper<Message> query = new LambdaQueryWrapper<>();
        if (targetUid != null){
            query = query.eq(Message::getTargetId, targetUid);
        }
        if (senderUid != null){
            query = query.eq(Message::getSenderUid, senderUid);
        }
        return query;
    }

    private LambdaQueryWrapper<MessageReadBadge> buildMessageReadBadge(Long targetUid, Long senderUid, Set<Long> targetUidList) {
        LambdaQueryWrapper<MessageReadBadge> query = new LambdaQueryWrapper<>();
        if (targetUid != null){
            query = query.eq(MessageReadBadge::getCustomerId, targetUid);
        }
        if (senderUid != null){
            query = query.eq(MessageReadBadge::getSenderUid, senderUid);
        }
        if (!CollectionUtils.isEmpty(targetUidList)){
            query = query.in(MessageReadBadge::getCustomerId, targetUid);
        }
        return query;
    }

    private Optional<MessageSenderSystem> getMessageSenderSystem(Long senderUid){
        return listMessageSenderSystemBy(senderUid).stream().findFirst();
    }

    private List<MessageSenderSystem> listMessageSenderSystemBy(Long senderUid){
        LambdaQueryWrapper<MessageSenderSystem> query = new LambdaQueryWrapper<>();
        if (senderUid != null){
            query = query.eq(MessageSenderSystem::getCustomerId, senderUid);
        }
        return messageSenderSystemMapper.selectList(query);
    }
}
