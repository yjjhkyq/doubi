package com.x.provider.api.mc.service;

import cn.hutool.core.util.StrUtil;
import com.x.core.utils.DateUtils;
import com.x.core.utils.JsonUtil;
import com.x.provider.api.mc.enums.MessageTargetType;
import com.x.provider.api.mc.enums.SenderSystemType;
import com.x.provider.api.mc.model.ao.SendMessageAO;
import com.x.provider.api.mc.model.protocol.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class McHelper {

    public static SendMessageAO buildVideoGreenNotify(Long targetId, String videoTitle, Date videoCreateDate, boolean greenPass){
        String greenResult = greenPass ? "作品审核通过啦" : "作品审核未通过，请调整视频内容重新上传";
        Map<String, String> tableData = Map.of("作品标题", videoTitle, "发布日期", DateUtils.dateTimeNow(DateUtils.YYYY_MM_DD_HH_MM_SS), "审核结果", greenResult);
        GeneralMsgBody generalMsgBody = GeneralMsgBody.builder().title("作品审核通知").tableData(tableData).build();
        return new SendMessageAO(SenderSystemType.SECRETARY.getValue(), MessageTargetType.PERSONAL.getValue(), targetId, MessageType.GENERAL_MSG.name(), greenResult, JsonUtil.toJSONString(generalMsgBody));
    }

    public static SendMessageAO buildUserInfoGreenNotify(Long targetId, boolean greenPass){
        String greenResult = greenPass ? "用户资料审核通过啦" : "用户资料审核未通过，请调整内容重新上传" ;
        GeneralMsgBody generalMsgBody = GeneralMsgBody.builder().title("用户资料审核通知").content(greenResult).build();
        return new SendMessageAO(SenderSystemType.SECRETARY.getValue(), MessageTargetType.PERSONAL.getValue(), targetId, MessageType.GENERAL_MSG.name(), greenResult, JsonUtil.toJSONString(generalMsgBody));
    }

    public static SendMessageAO buildFansNotify(Long targetId, String fromNickName){
        return new SendMessageAO(SenderSystemType.FAN.getValue(), MessageTargetType.PERSONAL.getValue(), targetId, MessageType.FOLLOW.name(), fromNickName + " 关注了你", null);
    }

    public static SendMessageAO buildVideoStarInteractMsg(Long targetId, VideoStarInteractMsgBody videoStarInteractMsgBody){
        videoStarInteractMsgBody.setAlertMsg(StrUtil.format("{} 赞了你的作品", videoStarInteractMsgBody.getFromNickName()));
        videoStarInteractMsgBody.setInteractiveDescription("赞了你的作品");
        return new SendMessageAO(SenderSystemType.INACTIVE.getValue(), MessageTargetType.PERSONAL.getValue(), targetId, MessageType.STAR_VIDEO.name(), videoStarInteractMsgBody.getAlertMsg(), JsonUtil.toJSONString(videoStarInteractMsgBody));
    }

    public static SendMessageAO buildVideoCommentStarInteractMsg(Long targetId, VideoCommentStarInteractMsgBody videoCommentStarInteractMsgBody){
        videoCommentStarInteractMsgBody.setAlertMsg(StrUtil.format("{} 赞了你的评论", videoCommentStarInteractMsgBody.getFromNickName()));
        videoCommentStarInteractMsgBody.setInteractiveDescription("赞了你的评论");
        return new SendMessageAO(SenderSystemType.INACTIVE.getValue(), MessageTargetType.PERSONAL.getValue(), targetId, MessageType.STAR_VIDEO_COMMENT.name(), videoCommentStarInteractMsgBody.getAlertMsg(), JsonUtil.toJSONString(videoCommentStarInteractMsgBody));
    }

    public static SendMessageAO buildVideoCommentInteractMsg(Long targetId, VideoCommentInteractMsgBody videoCommentStarInteractMsgBody){
        videoCommentStarInteractMsgBody.setAlertMsg(StrUtil.format("{}{}", videoCommentStarInteractMsgBody.isReply() ? "回复: " : "作品评论:", videoCommentStarInteractMsgBody.getCommentContent()));
        videoCommentStarInteractMsgBody.setInteractiveDescription(StrUtil.format("{}{}", videoCommentStarInteractMsgBody.isReply() ? "回复: " : "作品评论:", videoCommentStarInteractMsgBody.getCommentContent()));
        return new SendMessageAO(SenderSystemType.INACTIVE.getValue(), MessageTargetType.PERSONAL.getValue(), targetId, MessageType.COMMENT_VIDEO.name(), videoCommentStarInteractMsgBody.getAlertMsg(), JsonUtil.toJSONString(videoCommentStarInteractMsgBody));
    }
}
