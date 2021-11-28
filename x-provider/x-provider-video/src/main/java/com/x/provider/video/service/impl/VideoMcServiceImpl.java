package com.x.provider.video.service.impl;

import com.x.provider.api.customer.enums.CustomerOptions;
import com.x.provider.api.customer.model.dto.CustomerDTO;
import com.x.provider.api.customer.service.CustomerRpcService;
import com.x.provider.api.general.enums.CommentItemTypeEnum;
import com.x.provider.api.general.enums.StarItemTypeEnum;
import com.x.provider.api.general.model.event.CommentEvent;
import com.x.provider.api.general.model.event.StarEvent;
import com.x.provider.api.mc.model.protocol.VideoCommentInteractMsgBody;
import com.x.provider.api.mc.model.protocol.VideoCommentStarInteractMsgBody;
import com.x.provider.api.mc.model.protocol.VideoStarInteractMsgBody;
import com.x.provider.api.mc.service.McHelper;
import com.x.provider.api.mc.service.MessageRpcService;
import com.x.provider.api.video.model.event.VideoChangedEvent;
import com.x.provider.api.vod.service.VodRpcService;
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

    private final MessageRpcService notifyRpcService;
    private final CustomerRpcService customerRpcService;
    private final VideoService videoService;
    private final VodRpcService vodRpcService;
    public VideoMcServiceImpl(MessageRpcService notifyRpcService,
                              CustomerRpcService customerRpcService,
                              VideoService videoService,
                              VodRpcService vodRpcService){
        this.notifyRpcService = notifyRpcService;
        this.customerRpcService = customerRpcService;
        this.videoService = videoService;
        this.vodRpcService = vodRpcService;
    }

    @Override
    public void onVideoChanged(VideoChangedEvent videoChangedEvent) {
        if (VideoChangedEvent.EventTypeEnum.VIDEO_PUBLISHED.getValue().equals(videoChangedEvent.getEventType())
                || VideoChangedEvent.EventTypeEnum.VIDEO_GREEN_BLOCKED.getValue().equals(videoChangedEvent.getEventType())) {
            notifyRpcService.sendMessage(McHelper.buildVideoGreenNotify(videoChangedEvent.getCustomerId(), videoChangedEvent.getTitle(), videoChangedEvent.getCreatedOnUtc(),
                    VideoChangedEvent.EventTypeEnum.VIDEO_PUBLISHED.getValue().equals(videoChangedEvent.getEventType())));
            return;
        }
    }

    @Override
    public void onStar(StarEvent starEvent) {
        if (!starEvent.isFirstStar() || !Arrays.asList(StarItemTypeEnum.VIDEO.getValue(), StarItemTypeEnum.COMMENT.getValue()).contains(starEvent.getItemType())){
            return;
        }
        Long videoId = starEvent.getItemType().equals(StarItemTypeEnum.VIDEO.getValue()) ? Long.parseLong(starEvent.getItemId()) : starEvent.getAssociationItemId();
        Optional<Video> video = videoService.getVideo(videoId);
        if (video.isEmpty() || video.get().getCustomerId().equals(starEvent.getStarCustomerId())){
            return;
        }

        CustomerDTO customer = customerRpcService.getCustomer(starEvent.getStarCustomerId(), Arrays.asList(CustomerOptions.CUSTOMER_ATTRIBUTE.name())).getData();

        if (starEvent.getItemType().equals(StarItemTypeEnum.VIDEO.getValue())){
            VideoStarInteractMsgBody starInteractMsgBody = new VideoStarInteractMsgBody();
            starInteractMsgBody.setFromUid(customer.getId());
            starInteractMsgBody.setFromNickName(customer.getCustomerAttribute().getNickName());
            starInteractMsgBody.setFromAvatarUrl(customer.getCustomerAttribute().getAvatarUrl());
            starInteractMsgBody.setInteractiveTargetId(video.get().getId());
            starInteractMsgBody.setInteractiveIcon(vodRpcService.getMediaInfo(video.get().getFileId()).getData().getCoverUrl());
            notifyRpcService.sendMessage(McHelper.buildVideoStarInteractMsg(video.get().getCustomerId(), starInteractMsgBody));
        }
        else{
            VideoCommentStarInteractMsgBody starInteractMsgBody = new VideoCommentStarInteractMsgBody();
            starInteractMsgBody.setFromUid(customer.getId());
            starInteractMsgBody.setFromNickName(customer.getCustomerAttribute().getNickName());
            starInteractMsgBody.setFromAvatarUrl(customer.getCustomerAttribute().getAvatarUrl());
            starInteractMsgBody.setInteractiveTargetId(video.get().getId());
            starInteractMsgBody.setInteractiveIcon(vodRpcService.getMediaInfo(video.get().getFileId()).getData().getCoverUrl());
            notifyRpcService.sendMessage(McHelper.buildVideoCommentStarInteractMsg(video.get().getCustomerId(), starInteractMsgBody));
        }
    }

    @Override
    public void onComment(CommentEvent commentEvent) {
        if (!commentEvent.getItemType().equals(CommentItemTypeEnum.VIDEO.getValue())){
            return;
        }
        Optional<Video> video = videoService.getVideo(commentEvent.getReplyRootId() != null && commentEvent.getReplyRootId() > 0 ? commentEvent.getReplyRootId() : commentEvent.getItemId());
        if (!video.isPresent() || video.get().getCustomerId().equals(commentEvent.getCommentCustomerId())){
            return;
        }
        CustomerDTO customer = customerRpcService.getCustomer(commentEvent.getCommentCustomerId(), Arrays.asList(CustomerOptions.CUSTOMER_ATTRIBUTE.name())).getData();
        VideoCommentInteractMsgBody interactMsgBody = new VideoCommentInteractMsgBody();
        interactMsgBody.setFromUid(customer.getId());
        interactMsgBody.setFromNickName(customer.getCustomerAttribute().getNickName());
        interactMsgBody.setFromAvatarUrl(customer.getCustomerAttribute().getAvatarUrl());
        interactMsgBody.setInteractiveTargetId(video.get().getId());
        interactMsgBody.setInteractiveIcon(vodRpcService.getMediaInfo(video.get().getFileId()).getData().getCoverUrl());
        interactMsgBody.setCommentContent(commentEvent.getContent());
        interactMsgBody.setCommentId(commentEvent.getId());
        interactMsgBody.setReply(commentEvent.getReplyCommentId() != null && commentEvent.getReplyCommentId() > 0);
        notifyRpcService.sendMessage(McHelper.buildVideoCommentInteractMsg(video.get().getCustomerId(), interactMsgBody));
    }
}
