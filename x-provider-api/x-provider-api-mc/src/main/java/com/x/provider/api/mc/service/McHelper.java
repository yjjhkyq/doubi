package com.x.provider.api.mc.service;

import cn.hutool.core.util.StrUtil;
import com.x.core.utils.DateUtils;
import com.x.core.utils.JsonUtil;
import com.x.provider.api.mc.enums.SenderSystemType;
import com.x.provider.api.mc.model.dto.SendMessageRequestDTO;
import com.x.provider.api.mc.model.protocol.*;

import java.util.Date;
import java.util.Map;

public class McHelper {

    public static SendMessageRequestDTO buildProductGreenNotify(Long targetId, String productTitle, Date videoCreateDate, boolean greenPass){
        String greenResult = greenPass ? "作品审核通过啦" : "作品审核未通过，请调整视频内容重新上传";
        Map<String, String> tableData = Map.of("作品标题", productTitle, "发布日期", DateUtils.dateTimeNow(DateUtils.YYYY_MM_DD_HH_MM_SS), "审核结果", greenResult);
        GeneralMsgBody generalMsgBody = GeneralMsgBody.builder().title("作品审核通知").tableData(tableData).build();
        return new SendMessageRequestDTO(SenderSystemType.SECRETARY.getValue(), 0L, targetId, MessageType.GENERAL_MSG.name(), greenResult,
                JsonUtil.toJSONString(generalMsgBody), MessageClassEnum.IM.getValue(), false );
    }

    public static SendMessageRequestDTO buildUserInfoGreenNotify(Long targetId, boolean greenPass){
        String greenResult = greenPass ? "用户资料审核通过啦" : "用户资料审核未通过，请调整内容重新上传" ;
        GeneralMsgBody generalMsgBody = GeneralMsgBody.builder().title("用户资料审核通知").content(greenResult).build();
        return new SendMessageRequestDTO(SenderSystemType.SECRETARY.getValue(), 0L, targetId, MessageType.GENERAL_MSG.name(), greenResult,
                JsonUtil.toJSONString(generalMsgBody), MessageClassEnum.IM.getValue(), false );
    }

    public static SendMessageRequestDTO buildFansNotify(Long targetId, String fromNickName){
        return new SendMessageRequestDTO(SenderSystemType.FAN.getValue(), 0L, targetId, MessageType.FOLLOW.name(), fromNickName + " 关注了你",
                JsonUtil.toJSONString(Map.of("data", "romNickName + \" 关注了你\"")), MessageClassEnum.IM.getValue(), false );
    }

    public static SendMessageRequestDTO buildProductStarInteractMsg(Long targetId, ProductStarInteractMsgBody productStarInteractMsgBody){
        productStarInteractMsgBody.setAlertMsg(StrUtil.format("{} 赞了你的作品", productStarInteractMsgBody.getFromNickName()));
        productStarInteractMsgBody.setInteractiveDescription("赞了你的作品");
        return new SendMessageRequestDTO(SenderSystemType.INACTIVE.getValue(), 0L, targetId, MessageType.STAR_PRODUCT.name(), productStarInteractMsgBody.getAlertMsg(),
                JsonUtil.toJSONString(productStarInteractMsgBody), MessageClassEnum.IM.getValue(), false );
    }

    public static SendMessageRequestDTO buildProductCommentStarInteractMsg(Long targetId, ProductCommentStarInteractMsgBody videoCommentStarInteractMsgBody){
        videoCommentStarInteractMsgBody.setAlertMsg(StrUtil.format("{} 赞了你的评论", videoCommentStarInteractMsgBody.getFromNickName()));
        videoCommentStarInteractMsgBody.setInteractiveDescription("赞了你的评论");
        return new SendMessageRequestDTO(SenderSystemType.INACTIVE.getValue(), 0L, targetId, MessageType.STAR_PRODUCT_COMMENT.name(), videoCommentStarInteractMsgBody.getAlertMsg(),
                JsonUtil.toJSONString(videoCommentStarInteractMsgBody), MessageClassEnum.IM.getValue(), false );
    }

    public static SendMessageRequestDTO buildProductCommentInteractMsg(Long targetId, ProductCommentInteractMsgBody productCommentStarInteractMsgBody){
        productCommentStarInteractMsgBody.setAlertMsg(StrUtil.format("{}{}", productCommentStarInteractMsgBody.isReply() ? "回复: " : "作品评论:", productCommentStarInteractMsgBody.getCommentContent()));
        productCommentStarInteractMsgBody.setInteractiveDescription(StrUtil.format("{}{}", productCommentStarInteractMsgBody.isReply() ? "回复: " : "作品评论:", productCommentStarInteractMsgBody.getCommentContent()));
        return new SendMessageRequestDTO(SenderSystemType.INACTIVE.getValue(), 0L, targetId, MessageType.COMMENT_PRODUCT.name(), productCommentStarInteractMsgBody.getAlertMsg(),
                JsonUtil.toJSONString(productCommentStarInteractMsgBody), MessageClassEnum.IM.getValue(), false );
    }
}
