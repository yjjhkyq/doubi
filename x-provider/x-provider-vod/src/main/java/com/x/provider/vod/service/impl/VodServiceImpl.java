package com.x.provider.vod.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.x.core.exception.ApiException;
import com.x.core.web.api.R;
import com.x.provider.api.vod.enums.ReviewResultEnum;
import com.x.provider.api.vod.model.ao.GetContentReviewResultAO;
import com.x.provider.api.vod.model.dto.ContentReviewResultDTO;
import com.x.provider.vod.configure.TencentVodConfig;
import com.x.provider.vod.mapper.ContentReviewResultMapper;
import com.x.provider.vod.mapper.MediaInfoMapper;
import com.x.provider.vod.mapper.MediaTranscodeItemMapper;
import com.x.provider.vod.model.domain.ContentReviewResult;
import com.x.provider.vod.model.domain.MediaInfo;
import com.x.provider.vod.model.domain.MediaTranscodeItem;
import com.x.provider.vod.model.vo.VodUploadParamVO;
import com.x.provider.vod.service.RedisKeyService;
import com.x.provider.vod.service.VodService;
import com.x.redis.service.DistributeRedisLock;
import com.x.redis.service.RedisService;
import com.tencentcloudapi.vod.v20180717.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class VodServiceImpl implements VodService {

    private final String OBJECT_PATH = "/{}";

    private final TencentVodConfig tencentVodConfig;
    private final MediaInfoMapper mediaInfoMapper;
    private final ContentReviewResultMapper contentReviewResultMapper;
    private final MediaTranscodeItemMapper mediaTranscodeItemMapper;
    private final RedisKeyService redisKeyService;
    private final RestTemplate restTemplate;
    private final RedisService redisService;

    public VodServiceImpl(TencentVodConfig tencentVodConfig,
                          MediaInfoMapper mediaInfoMapper,
                          ContentReviewResultMapper contentReviewResultMapper,
                          MediaTranscodeItemMapper mediaTranscodeItemMapper,
                          RedisKeyService redisKeyService,
                          RestTemplate restTemplate,
                          RedisService redisService){
        this.tencentVodConfig = tencentVodConfig;
        this.mediaInfoMapper = mediaInfoMapper;
        this.contentReviewResultMapper = contentReviewResultMapper;
        this.mediaTranscodeItemMapper = mediaTranscodeItemMapper;
        this.redisKeyService = redisKeyService;
        this.restTemplate = restTemplate;
        this.redisService = redisService;
    }

    @Override
    public VodUploadParamVO getVodUploadParam(long customerId) {
        VodUploadParamVO result = new VodUploadParamVO();
        TencentSignatureServiceImpl sign = new TencentSignatureServiceImpl();
        sign.setCurrentTime(System.currentTimeMillis());
        sign.setSecretId(tencentVodConfig.getSecretId());
        sign.setSecretKey(tencentVodConfig.getSecretKey());
        sign.setRandom(new Random().nextInt(java.lang.Integer.MAX_VALUE));
        sign.setSignValidDuration(Duration.ofMinutes(2));
        try {
            String signature = sign.getUploadSignature(tencentVodConfig.getTaskStreamName());
            result.setSignature(signature);
            result.setCoverPath(StrUtil.format(OBJECT_PATH, customerId));
            result.setVideoPath(StrUtil.format(OBJECT_PATH, customerId));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ApiException("get upload signature error");
        }
        return result;
    }

    @Override
    @Transactional
    public void onEvent(EventContent eventContent) {
        try {
            if (eventContent.getFileUploadEvent() != null) {
                onFileUploadEvent(eventContent.getFileUploadEvent());
            }

            if (eventContent.getProcedureStateChangeEvent() != null) {
                if (eventContent.getProcedureStateChangeEvent().getAiContentReviewResultSet() != null) {
                    onContentReviewEvent(eventContent.getProcedureStateChangeEvent().getFileId(),
                            Arrays.asList(eventContent.getProcedureStateChangeEvent().getAiContentReviewResultSet()));
                }
                if (eventContent.getProcedureStateChangeEvent().getMediaProcessResultSet() != null
                        && eventContent.getProcedureStateChangeEvent().getMediaProcessResultSet().length > 0) {
                    for (MediaProcessTaskResult mediaProcessTaskResult : eventContent.getProcedureStateChangeEvent().getMediaProcessResultSet()) {
                        onCoverBySnapshot(eventContent.getProcedureStateChangeEvent().getFileId(), mediaProcessTaskResult.getCoverBySnapshotTask());
                        onTranscodeEvent(eventContent.getProcedureStateChangeEvent().getFileId(), mediaProcessTaskResult.getAdaptiveDynamicStreamingTask());
                    }

                }
            }
        }
        catch (Exception e){
            log.error(e.getMessage(), e);
        }

    }

    @Override
    public void contentReview(GetContentReviewResultAO getContentReviewResultAO) {
        getContentReviewResultAO.getFileIds().forEach(item ->{
            try(DistributeRedisLock lock = new DistributeRedisLock(redisKeyService.getContentReviewNotifyLockKey(item))) {
                Optional<ReviewResultEnum> contentReviewResult = getContentReviewResult(item);
                if (contentReviewResult.isPresent()){
                    notifyContentReviewResult(item, getContentReviewResultAO.getNotifyUrl(), contentReviewResult.get());
                    return;
                }
                redisService.setCacheObject(redisKeyService.getContentReviewNotifyUrl(item), getContentReviewResultAO.getNotifyUrl(), Duration.ofMinutes(10));
            }
        });
    }

    public MediaInfo getMediaInfo(long id, String fileId){
        var query = new LambdaQueryWrapper<MediaInfo>();
        if (id > 0){
            query.eq(MediaInfo::getId, id);
        }
        if (!StringUtils.isEmpty(fileId)){
            query.eq(MediaInfo::getFileId, fileId);
        }
        return mediaInfoMapper.selectOne(query);
    }

    public List<ContentReviewResult> listContentReviewResult(String fileId){
        var query = new LambdaQueryWrapper<ContentReviewResult>();
        if (!StringUtils.isEmpty(fileId)){
            query.eq(ContentReviewResult::getFileId, fileId);
        }
        return contentReviewResultMapper.selectList(query);
    }

    public MediaTranscodeItem getMediaTranscodeItem(String fileId){
        var query = new LambdaQueryWrapper<MediaTranscodeItem>();
        if (!StringUtils.isEmpty(fileId)){
            query.eq(MediaTranscodeItem::getFileId, fileId);
        }
        return mediaTranscodeItemMapper.selectOne(query);
    }

    private void notifyContentReviewResult(String fileId, String notifyUrl, ReviewResultEnum reviewResultEnum) {
        ContentReviewResultDTO contentReviewResultDTO = ContentReviewResultDTO.builder().fileId(fileId).reviewResult(reviewResultEnum.name()).build();
        restTemplate.postForEntity(notifyUrl, contentReviewResultDTO, R.class);
    }

    private Optional<ReviewResultEnum> getContentReviewResult(String fileId){
        List<ContentReviewResult> contentReviewResults = listContentReviewResult(fileId);
        return getContentReviewResult(contentReviewResults);
    }

    private Optional<ReviewResultEnum> getContentReviewResult(List<ContentReviewResult> contentReviewResults) {
        if (contentReviewResults.isEmpty()){
            return Optional.empty();
        }
        ReviewResultEnum reviewResultEnum = contentReviewResults.stream().allMatch(item -> !item.getSuggestion().equals(ReviewResultEnum.PASS.name())) ?
                ReviewResultEnum.BLOCK : ReviewResultEnum.PASS;
        return Optional.of(reviewResultEnum);
    }

    private void onCoverBySnapshot(String fileId, MediaProcessTaskCoverBySnapshotResult coverBySnapshotResult){
        if (coverBySnapshotResult == null || coverBySnapshotResult.getOutput() == null){
            return;
        }
        MediaInfo mediaInfo = getMediaInfo(0, fileId);
        mediaInfo.setCoverUrl(coverBySnapshotResult.getOutput().getCoverUrl());
        mediaInfoMapper.updateById(mediaInfo);
    }

    private void onTranscodeEvent(String fileId, MediaProcessTaskAdaptiveDynamicStreamingResult mediaProcessTaskTranscodeResult){
        if (mediaProcessTaskTranscodeResult == null || mediaProcessTaskTranscodeResult.getOutput() == null){
            return;
        }
        var transCodeItemExisted = getMediaTranscodeItem(fileId);
        if (transCodeItemExisted != null){
            return;
        }
        var transCodeItem = prepare(fileId, mediaProcessTaskTranscodeResult);
        mediaTranscodeItemMapper.insert(transCodeItem);
    }

    private void onFileUploadEvent(FileUploadTask fileUploadTask){
        MediaInfo mediaInfo = getMediaInfo(0, fileUploadTask.getFileId());
        if (mediaInfo != null){
            return;
        }
        mediaInfo = prepare(fileUploadTask);
        mediaInfoMapper.insert(mediaInfo);
        return;
    }

    private void onContentReviewEvent(String fileId, List<AiContentReviewResult> aiContentReviewResults){
        if (CollectionUtils.isEmpty(aiContentReviewResults)){
            return;
        }
        List<ContentReviewResult> contentReviewResults = prepare(fileId, aiContentReviewResults);
        try(DistributeRedisLock lock = new DistributeRedisLock(redisKeyService.getContentReviewNotifyLockKey(fileId))){
            String notifyUrl = redisService.getCacheObject(redisKeyService.getContentReviewNotifyUrl(fileId));
            if (!StringUtils.isEmpty(notifyUrl)){
                Optional<ReviewResultEnum> contentReviewResult = getContentReviewResult(contentReviewResults);
                if (contentReviewResult.isPresent()){
                    notifyContentReviewResult(fileId, notifyUrl, contentReviewResult.get());
                }
                redisService.deleteObject(redisKeyService.getContentReviewNotifyUrl(fileId));
            }
            Set<String> reviewTypeExisted = listContentReviewResult(fileId).stream().map(ContentReviewResult::getReviewType).collect(Collectors.toSet());
            contentReviewResults.stream().filter(item -> !reviewTypeExisted.contains(item.getReviewType())).forEach(item -> {
                contentReviewResultMapper.insert(item);
            });
        }
    }

    private MediaTranscodeItem prepare(String fileId, MediaProcessTaskAdaptiveDynamicStreamingResult mediaProcessTaskTranscodeResult){
        var output = mediaProcessTaskTranscodeResult.getOutput();
        return MediaTranscodeItem.builder().fileId(fileId).url(output.getUrl()).build();
    }

    private List<ContentReviewResult> prepare(String fileId, List<AiContentReviewResult> aiContentReviewResults){
        var contentReviewResults = new ArrayList<ContentReviewResult>(3);
        aiContentReviewResults.forEach(item -> {
            if (item.getPornTask() != null){
                contentReviewResults.add(new ContentReviewResult(fileId, item.getType(), item.getPornTask().getOutput().getSuggestion()));
            }
            if (item.getPoliticalTask() != null){
                contentReviewResults.add(new ContentReviewResult(fileId, item.getType(), item.getPoliticalTask().getOutput().getSuggestion()));
            }
            if (item.getTerrorismTask() != null){
                contentReviewResults.add(new ContentReviewResult(fileId, item.getType(), item.getTerrorismTask().getOutput().getSuggestion()));
            }
        });
        return contentReviewResults;
    }

    private MediaInfo prepare(FileUploadTask fileUploadTask){
        MediaBasicInfo mediaBasicInfo = fileUploadTask.getMediaBasicInfo();
        MediaMetaData mediaMetaData = fileUploadTask.getMetaData();
        return MediaInfo.builder()
                .fileId(fileUploadTask.getFileId())
                .coverUrl(mediaBasicInfo.getCoverUrl())
                .mediaUrl(mediaBasicInfo.getMediaUrl())
                .duration(mediaMetaData.getDuration())
                .height(mediaMetaData.getHeight())
                .width(mediaMetaData.getHeight())
                .name(mediaBasicInfo.getName())
                .size(mediaMetaData.getSize())
                .type(mediaBasicInfo.getType()).build();
    }
}
