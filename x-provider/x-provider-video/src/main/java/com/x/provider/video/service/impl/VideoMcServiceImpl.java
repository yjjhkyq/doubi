package com.x.provider.video.service.impl;

import com.x.provider.api.common.enums.ItemTypeEnum;
import com.x.provider.api.customer.enums.CustomerOptions;
import com.x.provider.api.customer.model.dto.CustomerDTO;
import com.x.provider.api.customer.service.CustomerRpcService;
import com.x.provider.api.general.enums.CommentItemTypeEnum;
import com.x.provider.api.general.enums.StarItemTypeEnum;
import com.x.provider.api.general.model.event.CommentEvent;
import com.x.provider.api.general.model.event.StarEvent;
import com.x.provider.api.mc.model.protocol.ProductCommentInteractMsgBody;
import com.x.provider.api.mc.model.protocol.ProductCommentStarInteractMsgBody;
import com.x.provider.api.mc.model.protocol.ProductStarInteractMsgBody;
import com.x.provider.api.mc.service.McHelper;
import com.x.provider.api.mc.service.MessageRpcService;
import com.x.provider.api.oss.service.VodRpcService;
import com.x.provider.api.video.model.event.VideoChangedEvent;
import com.x.provider.video.model.domain.Video;
import com.x.provider.video.service.VideoMcService;
import com.x.provider.video.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Service
public class VideoMcServiceImpl implements VideoMcService {

    private final MessageRpcService messageRpcService;
    private final CustomerRpcService customerRpcService;
    private final VideoService videoService;
    private final VodRpcService vodRpcService;
    public VideoMcServiceImpl(MessageRpcService messageRpcService,
                              CustomerRpcService customerRpcService,
                              VideoService videoService,
                              VodRpcService vodRpcService){
        this.messageRpcService = messageRpcService;
        this.customerRpcService = customerRpcService;
        this.videoService = videoService;
        this.vodRpcService = vodRpcService;
    }

    @Override
    public void onVideoChanged(VideoChangedEvent videoChangedEvent) {
        if (VideoChangedEvent.EventTypeEnum.VIDEO_PUBLISHED.getValue().equals(videoChangedEvent.getEventType())
                || VideoChangedEvent.EventTypeEnum.VIDEO_GREEN_BLOCKED.getValue().equals(videoChangedEvent.getEventType())) {
            messageRpcService.sendMessage(McHelper.buildProductGreenNotify(videoChangedEvent.getCustomerId(), videoChangedEvent.getTitle(), videoChangedEvent.getCreatedOnUtc(),
                    VideoChangedEvent.EventTypeEnum.VIDEO_PUBLISHED.getValue().equals(videoChangedEvent.getEventType())));
            return;
        }
    }

    @Override
    public void onStar(StarEvent starEvent) {
        if (!starEvent.isFirstStar() || !Arrays.asList(StarItemTypeEnum.VIDEO.getValue(), StarItemTypeEnum.COMMENT.getValue()).contains(starEvent.getItemType())){
            return;
        }
        Long videoId = starEvent.getItemType().equals(StarItemTypeEnum.VIDEO.getValue()) ? Long.parseLong(starEvent.getItemId())
                : (StarItemTypeEnum.COMMENT.getValue().equals(starEvent.getItemType()) && starEvent.getComment() != null && starEvent.getComment().getItemType() == ItemTypeEnum.VIDEO.getValue()
                ? Long.valueOf(starEvent.getComment().getItemId()) : 0L);
        if (videoId == 0){
            return;
        }
        Optional<Video> video = videoService.getVideo(videoId);
        if (video.isEmpty() || video.get().getCustomerId().equals(starEvent.getStarCustomerId())){
            return;
        }

        CustomerDTO customer = customerRpcService.getCustomer(starEvent.getStarCustomerId(), Arrays.asList(CustomerOptions.CUSTOMER_ATTRIBUTE.name())).getData();

        if (starEvent.getItemType().equals(StarItemTypeEnum.VIDEO.getValue())){
            ProductStarInteractMsgBody starInteractMsgBody = new ProductStarInteractMsgBody();
            starInteractMsgBody.setFromUid(customer.getId());
            starInteractMsgBody.setFromNickName(customer.getCustomerAttribute().getNickName());
            starInteractMsgBody.setFromAvatarUrl(customer.getCustomerAttribute().getAvatarUrl());
            starInteractMsgBody.setInteractiveTargetId(video.get().getId());
            starInteractMsgBody.setInteractiveIcon(vodRpcService.getMediaInfo(video.get().getFileId()).getData().getCoverUrl());
            messageRpcService.sendMessage(McHelper.buildProductStarInteractMsg(video.get().getCustomerId(), starInteractMsgBody));
        }
        else{
            ProductCommentStarInteractMsgBody starInteractMsgBody = new ProductCommentStarInteractMsgBody();
            starInteractMsgBody.setFromUid(customer.getId());
            starInteractMsgBody.setFromNickName(customer.getCustomerAttribute().getNickName());
            starInteractMsgBody.setFromAvatarUrl(customer.getCustomerAttribute().getAvatarUrl());
            starInteractMsgBody.setInteractiveTargetId(video.get().getId());
            starInteractMsgBody.setInteractiveIcon(vodRpcService.getMediaInfo(video.get().getFileId()).getData().getCoverUrl());
            messageRpcService.sendMessage(McHelper.buildProductCommentStarInteractMsg(video.get().getCustomerId(), starInteractMsgBody));
        }
    }

    @Override
    public void onComment(CommentEvent commentEvent) {
        if (!commentEvent.getItemType().equals(CommentItemTypeEnum.VIDEO.getValue())){
            return;
        }
        Optional<Video> video = videoService.getVideo(commentEvent.getItemId());
        if (!video.isPresent() || video.get().getCustomerId().equals(commentEvent.getCommentCustomerId())){
            return;
        }
        CustomerDTO customer = customerRpcService.getCustomer(commentEvent.getCommentCustomerId(), Arrays.asList(CustomerOptions.CUSTOMER_ATTRIBUTE.name())).getData();
        ProductCommentInteractMsgBody interactMsgBody = new ProductCommentInteractMsgBody();
        interactMsgBody.setFromUid(customer.getId());
        interactMsgBody.setFromNickName(customer.getCustomerAttribute().getNickName());
        interactMsgBody.setFromAvatarUrl(customer.getCustomerAttribute().getAvatarUrl());
        interactMsgBody.setInteractiveTargetId(video.get().getId());
        interactMsgBody.setInteractiveIcon(vodRpcService.getMediaInfo(video.get().getFileId()).getData().getCoverUrl());
        interactMsgBody.setCommentContent(commentEvent.getContent());
        interactMsgBody.setCommentId(commentEvent.getId());
        interactMsgBody.setReply(commentEvent.getParentCommentId() > 0);
        messageRpcService.sendMessage(McHelper.buildProductCommentInteractMsg(video.get().getCustomerId(), interactMsgBody));
    }
}
