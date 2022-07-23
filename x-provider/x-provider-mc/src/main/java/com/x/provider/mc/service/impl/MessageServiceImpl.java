package com.x.provider.mc.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.x.core.utils.BeanUtil;
import com.x.core.utils.CompareUtils;
import com.x.core.utils.JsonUtil;
import com.x.core.web.page.PageDomain;
import com.x.core.web.page.PageList;
import com.x.provider.api.customer.model.ao.ListSimpleCustomerAO;
import com.x.provider.api.customer.model.dto.SimpleCustomerDTO;
import com.x.provider.api.customer.service.CustomerRpcService;
import com.x.provider.api.mc.constants.McEventTopic;
import com.x.provider.api.mc.enums.ConversationType;
import com.x.provider.api.mc.enums.GroupType;
import com.x.provider.api.mc.model.ao.SendMessageAO;
import com.x.provider.api.mc.model.event.MessageEvent;
import com.x.provider.mc.configure.ApplicationConfig;
import com.x.provider.mc.mapper.*;
import com.x.provider.mc.model.ao.MarkMessageAsReadAO;
import com.x.provider.mc.model.domain.ConversationId;
import com.x.provider.mc.model.domain.*;
import com.x.provider.mc.model.dto.ConnectInfoDTO;
import com.x.provider.mc.model.dto.ConversationDTO;
import com.x.provider.mc.model.dto.MessageDTO;
import com.x.provider.mc.model.query.ConversationQuery;
import com.x.provider.mc.model.query.GroupQuery;
import com.x.provider.mc.model.query.MessageQuery;
import com.x.provider.mc.service.MessageEngineService;
import com.x.provider.mc.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    private static final String CONVERSATION_ID_SPLITTER = "_";
    private static final String CONVERSATION_ID_FORMATTER = "{}" + CONVERSATION_ID_SPLITTER +"{}";
    public static final String DEFAULT_ALERT_MESSAGE = "欢迎新同学";


    private final MessageMapper messageMapper;
    private final MessageConversationMapper messageConversationMapper;
    private final GroupMapper groupMapper;
    private final ConversationMapper conversationMapper;
    private final Executor executor;
    private final MessageEngineService messageEngineService;
    private final ApplicationConfig applicationConfig;
    private final GroupMemberMapper groupMemberMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final CustomerRpcService customerRpcService;

    public MessageServiceImpl(MessageMapper messageMapper,
                              @Qualifier("mcDefaultExecutor") Executor executor,
                              MessageEngineService messageEngineService,
                              ApplicationConfig applicationConfig,
                              MessageConversationMapper messageOwnerMapper,
                              GroupMapper groupMapper,
                              ConversationMapper conversationMapper,
                              GroupMemberMapper groupMemberMapper,
                              KafkaTemplate<String, Object> kafkaTemplate,
                              CustomerRpcService customerRpcService){
        this.messageMapper = messageMapper;
        this.executor = executor;
        this.messageEngineService = messageEngineService;
        this.applicationConfig = applicationConfig;
        this.messageConversationMapper = messageOwnerMapper;
        this.groupMapper = groupMapper;
        this.conversationMapper = conversationMapper;
        this.groupMemberMapper = groupMemberMapper;
        this.kafkaTemplate = kafkaTemplate;
        this.customerRpcService = customerRpcService;
    }

    @Override
    public PageList<Message> listMessage(String conversationIdStr, Long sessionCustomerId, PageDomain pageDomain) {
        ConversationId conversationId = toConversationId(conversationIdStr);
        LambdaQueryWrapper<Message> queryWrapper = buildQuery(MessageQuery.builder().toGroupId(conversationId.getGroupId()).toCustomerId(conversationId.getCustomerId())
                .gtId(pageDomain.getCursor()).build());
        queryWrapper = queryWrapper.orderByDesc(Message::getId);
        List<Message> records = messageMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(records)){
            return new PageList<>();
        }
        return new PageList<>(records, pageDomain.getPageSize(), CollectionUtils.lastElement(records).getId());
    }

    @Override
    public Long sendMessage(SendMessageAO sendMessageAO) {
        Message message = BeanUtil.prepare(sendMessageAO, Message.class);
        message.setCreatedOnUtc(new Date());
        if (!sendMessageAO.getOnlineUserOnly()){
            messageMapper.insert(message);
        }
        MessageEvent messageEvent = BeanUtil.prepare(message, MessageEvent.class);
        messageEvent.setEventType(MessageEvent.EventTypeEnum.SEND.getValue());
        messageEvent.setOnlineUserOnly(sendMessageAO.getOnlineUserOnly());
        this.kafkaTemplate.send(McEventTopic.TOPIC_NAME_SEND_MESSAGE, StrUtil.format("{}:{}",sendMessageAO.getToCustomerId() + sendMessageAO.getToGroupId()), messageEvent);
        return message.getId() == null ?0 : message.getId();
    }

    @Override
    public void onSendMessage(MessageEvent messageEvent){
        if (!MessageEvent.EventTypeEnum.SEND.getValue().equals(messageEvent.getEventType())){
            return;
        }
        Message message = BeanUtil.prepare(messageEvent, Message.class);
        if (messageEvent.getOnlineUserOnly() != null && messageEvent.getOnlineUserOnly()){
            messageEngineService.sendMessage(null, prepare(message));
            return;
        }
        saveMessageConversation(message);
        ConversationType conversationType = prepare(message.getToCustomerId(), message.getToGroupId());
        List<Conversation> conversationList = saveConversation(message, conversationType.getValue()).stream()
                .filter(item -> !item.getOwnerCustomerId().equals(message.getFromCustomerId())).collect(Collectors.toList());
        if (conversationList.isEmpty()){
            return;
        }
        List<ConversationDTO> conversationDTO = prepare(conversationList);
        MessageDTO messageDTO = prepare(message);
        conversationDTO.forEach(item -> {
            messageEngineService.sendMessage(item, messageDTO);
        });
    }

    @Override
    public Long getTotalUnreadMessageCount(Long ownerCustomerId){
        return conversationMapper.sumUnreadCount(ownerCustomerId);
    }

    @Override
    public Group getGroup(Long id) {
        return groupMapper.selectById(id);
    }

    @Override
    public List<Group> listGroup(List<Long> idList) {
        return groupMapper.selectBatchIds(idList);
    }

    @Override
    public void markMessageAsRead(MarkMessageAsReadAO markMessageAsReadAO, Long ownerCustomerId){
        Conversation conversation = getConversation(markMessageAsReadAO.getConversationId(), ownerCustomerId);
        if (conversation == null){
            return;
        }
        conversation.setUnreadCount(0L);
        if (conversation.getGroupId() > 0){
            Message lastMessage = getLastMessage(MessageQuery.builder().toGroupId(conversation.getGroupId()).build());
            if (lastMessage != null){
                conversation.setLastReadMessageId(lastMessage.getId());
            }
        }
        conversationMapper.updateById(conversation);
    }

    private void saveMessageConversation(Message message){
        messageConversationMapper.insert(MessageConversation.builder().messageId(message.getId()).customerId(message.getToCustomerId()).groupId(message.getToGroupId()).build());
        if (message.getToCustomerId() > 0){
            messageConversationMapper.insert(MessageConversation.builder().messageId(message.getId()).customerId(message.getFromCustomerId()).groupId(message.getToGroupId()).build());
        }
        else{
            Group group = groupMapper.selectById(message.getToGroupId());
            if (group.getGroupType().equals(GroupType.ALL.getValue())){

            }
        }
    }

    private List<Conversation> saveConversation(Message message, Integer conversationType){
        List<Conversation> conversations = prepareConversation(message);
        conversations.forEach(item ->{
            if (item.getId() == null){
                try {
                    conversationMapper.insert(item);
                }
                catch (Exception e){
                    log.error("conversation :{}", JsonUtil.toJSONString(item));
                }
            }
            else {
                conversationMapper.updateById(item);
            }
        });
        return conversations;
    }

    private List<Conversation> prepareConversation(Message message){
        Integer conversationType = prepare(message.getToCustomerId(), message.getToGroupId()).getValue();
        if (message.getToCustomerId() > 0){
            List<Conversation> result = new ArrayList<>(2);
            result.add(prepareConversation(message, conversationType, message.getFromCustomerId(), message.getToCustomerId()));
            if (!message.getToCustomerId().equals(message.getFromCustomerId())) {
                result.add(prepareConversation(message, conversationType, message.getToCustomerId(), message.getFromCustomerId()));
            }
            return result;
        }
        List<Long> conversationMemberList = listConversationMember(message);
        List<Conversation> result = new ArrayList<>(conversationMemberList.size());
        conversationMemberList.forEach(item ->{
            result.add(prepareConversation(message, conversationType, item, 0L));
        });
        return result;
    }

    private Conversation prepareConversation(Message message, Integer conversationType, Long ownerCustomerId, Long customerId){
        Conversation conversation = get(ConversationQuery.builder().ownerCustomerId(ownerCustomerId).customerId(customerId).groupId(message.getToGroupId()).build());
        return prepare(conversation, conversationType, message, ownerCustomerId, customerId);
    }

    private Conversation prepare(Conversation conversation, Integer conversationType,  Message message, Long ownerCustomerId, Long customerId){
        Long unReadCount = !Objects.equals(ownerCustomerId, message.getFromCustomerId()) ? 1L : 0L;
        if (conversation == null){
            conversation = Conversation.builder()
                    .ownerCustomerId(ownerCustomerId)
                    .customerId(customerId)
                    .groupId(message.getToGroupId())
                    .conversationType(conversationType)
                    .conversationId(toConversationId(ConversationId.builder().groupId(message.getToGroupId()).customerId(customerId).build()))
                    .unreadCount(0L)
                    .build();
        }
        conversation.setAlertMessage(message.getAlertMsg());
        conversation.setUnreadCount(conversation.getUnreadCount() + unReadCount);
        conversation.setDisplayOrder(System.currentTimeMillis());
        return conversation;
    }

    private List<Long> listConversationMember(Message message){
        List<Long> memberCustomerIdList = new ArrayList<>(2);
        memberCustomerIdList.add(message.getFromCustomerId());
        if (CompareUtils.gtZero(message.getToCustomerId())){
            memberCustomerIdList.add(message.getToCustomerId());
        }
        return memberCustomerIdList;
    }

    @Override
    public void initSystemConversation(Long ownerCustomerId) {
        List<Group> groupList = listGroup(GroupQuery.builder().groupType(GroupType.ALL.getValue()).build());
        groupList.forEach(item -> {
            Conversation conversation = prepareAllConversation4InitConversation(ownerCustomerId, item);
            if (conversation == null){
                return;
            }
            if (conversation.getId() == null){
                conversationMapper.insert(conversation);
            }
            else{
                conversationMapper.updateById(conversation);
            }
        });
    }

    private Conversation prepareAllConversation4InitConversation(Long ownerCustomerId, Group item) {
        Conversation conversation = get(ConversationQuery.builder().groupId(item.getId()).customerId(0L).ownerCustomerId(ownerCustomerId).build());
        if (conversation == null){
            conversation = Conversation.builder().id(null).unreadCount(0L).lastReadMessageId(0L).conversationType(ConversationType.GROUP.getValue()).groupId(item.getId())
                    .customerId(0L).ownerCustomerId(ownerCustomerId).conversationId(toConversationId(ConversationId.builder().customerId(null).groupId(item.getId()).build()))
                    .displayOrder(System.currentTimeMillis()).alertMessage(DEFAULT_ALERT_MESSAGE).build();
        }
        MessageQuery messageQuery = MessageQuery.builder().toGroupId(conversation.getGroupId())
                .gtId(conversation.getId()).build();
        if (item.getMessageLivedMilliSeconds() > 0){
            messageQuery.setGtCreateDate(DateUtils.addMilliseconds(new Date(), item.getMessageLivedMilliSeconds()));
        }
        Message lastMessage = getLastMessage(messageQuery);
        if (lastMessage != null){
            if (Objects.equals(lastMessage.getId(), conversation.getLastReadMessageId())){
                return null;
            }
            conversation.setAlertMessage(lastMessage.getAlertMsg());
        }
        conversation.setUnreadCount(count(messageQuery).longValue());
        conversation.setDisplayOrder(item.getDisplayOrder());
        return conversation;
    }

    @Override
    public PageList<Conversation> listConversation(Long ownerCustomerId, PageDomain pageDomain){
        LambdaQueryWrapper<Conversation> query = buildQuery(ConversationQuery.builder().ownerCustomerId(ownerCustomerId).ltDisplayOrder(pageDomain.getCursor()).build()).orderByDesc(Conversation::getDisplayOrder)
                .last(StrUtil.format(" limit {} ", pageDomain.getPageSize()));
        List<Conversation> conversationList = conversationMapper.selectList(query);
        if (conversationList.isEmpty()){
            return new PageList<>();
        }

        return new PageList<>(conversationList, pageDomain.getPageSize(), CollectionUtils.lastElement(conversationList).getDisplayOrder());
    }

    @Override
    public Conversation getConversation(String conversationId, Long ownerCustomerId){
        Conversation conversation = getConversation(ConversationQuery.builder().conversationId(conversationId).ownerCustomerId(ownerCustomerId).build());
        final ConversationId conversationIdObject = toConversationId(conversationId);
        if (conversation == null){
            conversation = Conversation.builder()
                    .conversationId(toConversationId(ConversationId.builder().customerId(conversationIdObject.getCustomerId()).groupId(conversationIdObject.getGroupId()).build()))
                    .customerId(conversationIdObject.getCustomerId())
                    .groupId(conversationIdObject.getGroupId())
                    .displayOrder(System.currentTimeMillis())
                    .ownerCustomerId(ownerCustomerId)
                    .conversationType(CompareUtils.gtZero(conversationIdObject.getCustomerId()) ? ConversationType.C2C.getValue() : ConversationType.GROUP.getValue())
                    .unreadCount(0L)
                    .alertMessage("")
                    .lastReadMessageId(0L)
                    .build();
        }
        return conversation;
    }

    public Conversation getConversation(ConversationQuery conversationQuery){
        return conversationMapper.selectOne(buildQuery(conversationQuery));
    }

    private Integer count(MessageQuery messageQuery){
        LambdaQueryWrapper<Message> query = buildQuery(messageQuery);
        return messageMapper.selectCount(query);
    }

    public Message getLastMessage(MessageQuery messageQuery){
        LambdaQueryWrapper<Message> query = buildQuery(messageQuery);
        query = query.orderByDesc(Message::getId);
        return messageMapper.selectOne(query);
    }

    public List<Group> listGroup(GroupQuery condition){
        return groupMapper.selectList(buildQuery(condition));
    }

    private LambdaQueryWrapper<Message> buildQuery(MessageQuery messageQuery){
        LambdaQueryWrapper<Message> query = new LambdaQueryWrapper<>();
        if (CompareUtils.gtZero(messageQuery.getToGroupId()) ){
            query = query.eq(Message::getToGroupId, messageQuery.getToGroupId());
        }
        if (CompareUtils.gtZero(messageQuery.getToCustomerId())){
            query = query.eq(Message::getToCustomerId, messageQuery.getToCustomerId());
        }
        if (messageQuery.getGtId() != null){
            query = query.gt(Message::getId, messageQuery.getGtId());
        }
        if (messageQuery.getGtCreateDate() != null){
            query = query.gt(Message::getCreatedOnUtc, messageQuery.getGtCreateDate());
        }
        if (CompareUtils.gtZero(messageQuery.getLtId())){
            query = query.lt(Message::getId, messageQuery.getLtId());
        }
        return query;
    }

    private Conversation get(ConversationQuery conversationQuery){
        return conversationMapper.selectOne(buildQuery(conversationQuery));
    }

    private LambdaQueryWrapper<Conversation> buildQuery(ConversationQuery condition){
        LambdaQueryWrapper<Conversation> query = new LambdaQueryWrapper<Conversation>();
        if (condition.getCustomerId() != null){
            query = query.eq(Conversation::getCustomerId, condition.getCustomerId());
        }
        if (condition.getGroupId() != null){
            query = query.eq(Conversation::getGroupId, condition.getGroupId());
        }
        if (condition.getId() != null){
            query = query.eq(Conversation::getId, condition.getId());
        }
        if (CompareUtils.gtZero(condition.getLtDisplayOrder())){
            query = query.lt(Conversation::getDisplayOrder, condition.getLtDisplayOrder());
        }
        if (CompareUtils.gtZero(condition.getOwnerCustomerId())){
            query = query.eq(Conversation::getOwnerCustomerId, condition.getOwnerCustomerId());
        }
        if (!StrUtil.isEmpty(condition.getConversationId())){
            query = query.eq(Conversation::getConversationId, condition.getConversationId());
        }
        return query;
    }

    private LambdaQueryWrapper<Group> buildQuery(GroupQuery condition){
        LambdaQueryWrapper<Group> query = new LambdaQueryWrapper<>();
        if (condition.getId() != null){
            query = query.eq(Group::getId, condition.getId());
        }
        if (condition.getCustomerId() != null){
            query = query.eq(Group::getCustomerId, condition.getCustomerId());
        }
        if (condition.getGroupType() != null){
            query = query.eq(Group::getGroupType, condition.getGroupType());
        }
        return query;
    }

    public static String toConversationId(ConversationId conversationId){
        return conversationId.getCustomerId() != null ? StrUtil.format(CONVERSATION_ID_FORMATTER, ConversationType.C2C.name(), conversationId.getCustomerId())
                : StrUtil.format(CONVERSATION_ID_FORMATTER, ConversationType.GROUP.name(), conversationId.getGroupId());
    }

    public static ConversationId toConversationId(String conversationId){
        String[] splitConversationId = conversationId.split(CONVERSATION_ID_SPLITTER);
        ConversationId conversationIdBO = new ConversationId();
        Long conversationIdValue = Long.valueOf(splitConversationId[1]);
        if (splitConversationId[0].equals(ConversationType.C2C.name())){
            conversationIdBO.setCustomerId(conversationIdValue);
        }
        else {
            conversationIdBO.setGroupId(conversationIdValue);
        }
        return conversationIdBO;
    }

    private MessageDTO prepare(Message message){
        return prepareMessage(Arrays.asList(message)).get(0);
    }

    @Override
    public List<MessageDTO> prepareMessage(List<Message> messageList){
        List<MessageDTO> result = BeanUtil.prepare(messageList, MessageDTO.class);
        Map<Long, SimpleCustomerDTO> senderMap = customerRpcService.listSimpleCustomerV2(ListSimpleCustomerAO.builder()
                .customerIds(new ArrayList<>(result.stream().map(item -> item.getFromCustomerId()).collect(Collectors.toSet()))).build()).getData();
        result.forEach(item -> {
            SimpleCustomerDTO fromCustomer = senderMap.get(item.getFromCustomerId());
            item.setFromCustomerNickName(fromCustomer.getNickName());
            item.setFromCustomerAvatarUrl(fromCustomer.getAvatarUrl());
            item.setCreatedTimestamp(item.getCreatedOnUtc().getTime());
        });
        return result;
    }

    private ConversationType prepare(Long customerId, Long groupId){
        return CompareUtils.gtZero(customerId) ? ConversationType.C2C : ConversationType.GROUP;
    }

    private ConversationDTO prepare(Conversation conversation, SimpleCustomerDTO showCustomer, Group group){
        ConversationDTO result = BeanUtil.prepare(conversation, ConversationDTO.class);
        if (showCustomer != null) {
            result.setShowName(showCustomer.getNickName());
            result.setFaceUrl(showCustomer.getAvatarUrl());
        }
        if (group != null && !StringUtils.isEmpty(group.getGroupName())){
            result.setShowName(group.getGroupName());
        }
        return result;
    }

    @Override
    public List<ConversationDTO> prepare(List<Conversation> conversationList){
        List<ConversationDTO> result = new ArrayList<>(conversationList.size());
        Set<Long> faceCustomerIdSet = new HashSet<>(conversationList.size());
        faceCustomerIdSet.addAll(conversationList.stream().filter(item -> item.getCustomerId() > 0).map(item -> item.getCustomerId()).collect(Collectors.toSet()));
        List<Long> groupIdList = conversationList.stream().filter(item -> item.getGroupId() > 0).map(item -> item.getGroupId()).collect(Collectors.toList());
        Map<Long, Group> groupMap = groupIdList.isEmpty() ? new HashMap<>() : groupMapper.selectBatchIds(groupIdList).stream().collect(Collectors.toMap(item -> item.getId(), item -> item));
        faceCustomerIdSet.addAll(groupMap.values().stream().map(item -> item.getCustomerId()).collect(Collectors.toSet()));
        Map<Long, SimpleCustomerDTO> simpleCustomerMap = customerRpcService.listSimpleCustomerV2(ListSimpleCustomerAO.builder()
                .customerIds(new ArrayList<>(faceCustomerIdSet))
                .build()).getData();
        conversationList.forEach(item -> {
            Long customerId = item.getCustomerId();
            if (item.getGroupId() >0 && groupMap.containsKey(item.getGroupId())){
                customerId = groupMap.get(item.getGroupId()).getCustomerId();
            }
            result.add(prepare(item, simpleCustomerMap.get(customerId), groupMap.get(item.getGroupId())));
        });
        return result;
    }

    @Override
    public ConversationDTO prepare(Conversation conversation) {
        return prepare(Arrays.asList(conversation)).get(0);
    }
}
