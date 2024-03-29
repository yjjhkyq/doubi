package com.x.provider.oss.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.vod.v20180717.VodClient;
import com.tencentcloudapi.vod.v20180717.models.*;
import com.x.core.exception.ApiException;
import com.x.core.web.api.R;
import com.x.provider.api.oss.enums.MediaTypeEnum;
import com.x.core.domain.SuggestionTypeEnum;
import com.x.provider.api.oss.model.dto.vod.GetContentReviewResultRequestDTO;
import com.x.provider.api.oss.model.dto.vod.ListMediaRequestDTO;
import com.x.provider.api.oss.model.dto.vod.ListMediaUrlRequestDTO;
import com.x.provider.api.oss.model.dto.vod.ContentReviewResultDTO;
import com.x.provider.oss.configure.TencentOssConfig;
import com.x.provider.oss.mapper.ContentReviewResultMapper;
import com.x.provider.oss.mapper.ContentReviewResultNotifyMapper;
import com.x.provider.oss.mapper.MediaInfoMapper;
import com.x.provider.oss.mapper.MediaTranscodeItemMapper;
import com.x.provider.oss.model.domain.ContentReviewResult;
import com.x.provider.oss.model.domain.ContentReviewResultNotify;
import com.x.provider.oss.model.domain.MediaInfo;
import com.x.provider.oss.model.domain.MediaTranscodeItem;
import com.x.provider.oss.model.vo.vod.VodUploadParamVO;
import com.x.provider.oss.service.RedisKeyService;
import com.x.provider.oss.service.VodService;
import com.x.redis.service.DistributeRedisLock;
import com.x.redis.service.RedisService;
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

    private final TencentOssConfig tencentVodConfig;
    private final MediaInfoMapper mediaInfoMapper;
    private final ContentReviewResultMapper contentReviewResultMapper;
    private final MediaTranscodeItemMapper mediaTranscodeItemMapper;
    private final RedisKeyService redisKeyService;
    private final RestTemplate restTemplate;
    private final RedisService redisService;
    private final ContentReviewResultNotifyMapper contentReviewResultNotifyMapper;
    private final VodClient vodClient;
    public VodServiceImpl(TencentOssConfig tencentVodConfig,
                          MediaInfoMapper mediaInfoMapper,
                          ContentReviewResultMapper contentReviewResultMapper,
                          MediaTranscodeItemMapper mediaTranscodeItemMapper,
                          RedisKeyService redisKeyService,
                          RestTemplate restTemplate,
                          RedisService redisService,
                          ContentReviewResultNotifyMapper contentReviewResultNotifyMapper){
        this.tencentVodConfig = tencentVodConfig;
        this.mediaInfoMapper = mediaInfoMapper;
        this.contentReviewResultMapper = contentReviewResultMapper;
        this.mediaTranscodeItemMapper = mediaTranscodeItemMapper;
        this.redisKeyService = redisKeyService;
        this.restTemplate = restTemplate;
        this.redisService = redisService;
        this.contentReviewResultNotifyMapper = contentReviewResultNotifyMapper;
        Credential credential = new Credential(this.tencentVodConfig.getAppSecretId(), this.tencentVodConfig.getAppSecretKey());
        this.vodClient = new VodClient(credential, TencentOssConfig.AP_CHENGDU);

    }

    @Override
    public VodUploadParamVO getVodUploadParam(long customerId, String fileName) {
        VodUploadParamVO result = new VodUploadParamVO();
        TencentSignatureServiceImpl sign = new TencentSignatureServiceImpl();
        sign.setCurrentTime(System.currentTimeMillis());
        sign.setSecretId(tencentVodConfig.getAppSecretId());
        sign.setSecretKey(tencentVodConfig.getAppSecretKey());
        sign.setRandom(new Random().nextInt(Integer.MAX_VALUE));
        sign.setSignValidDuration(Duration.ofSeconds(10 * 60));
        try {
            String signature = sign.getUploadSignature(tencentVodConfig.getVodTaskStreamName());
            result.setSignature(signature);
            result.setFileName(StrUtil.format("{}.{}", IdUtil.simpleUUID(), FileUtil.extName(fileName)));
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
    public void contentReview(GetContentReviewResultRequestDTO getContentReviewResultAO) {
        getContentReviewResultAO.getFileIds().forEach(item ->{
            contentReviewResultNotifyMapper.insert(ContentReviewResultNotify.builder().fileId(item).notifySuccess(false).notifyUrl(getContentReviewResultAO.getNotifyUrl()).retryCount(0).build());
            try(DistributeRedisLock lock = new DistributeRedisLock(redisKeyService.getContentReviewNotifyLockKey(item))) {
                Optional<SuggestionTypeEnum> contentReviewResult = getContentReviewResult(item);
                if (contentReviewResult.isPresent()){
                    notifyContentReviewResult(item, getContentReviewResultAO.getNotifyUrl(), contentReviewResult.get());
                    return;
                }
                redisService.setCacheObject(redisKeyService.getContentReviewNotifyUrl(item), getContentReviewResultAO.getNotifyUrl(), Duration.ofMinutes(10));
            }
        });
    }

    @Override
    public Map<String, String> listMediaUrl(ListMediaUrlRequestDTO listMediaUrlAO) {
        listMediaUrlAO.setFileIds(listMediaUrlAO.getFileIds().stream().distinct().collect(Collectors.toList()));
        if (listMediaUrlAO.getMediaType().equals(MediaTypeEnum.COVER)) {
            return getMediaInfo(listMediaUrlAO.getFileIds()).stream().filter(item -> !StringUtils.isEmpty(item.getCoverUrl())).collect(
                    Collectors.toMap(MediaInfo::getFileId, MediaInfo::getCoverUrl));
        }
        return new HashMap<>();
    }

    @Override
    public void deleteMedia(String fileId) {
        DeleteMediaRequest deleteMediaRequest = new DeleteMediaRequest();
        deleteMediaRequest.setFileId(fileId);

        try {
            this.vodClient.DeleteMedia(deleteMediaRequest);
        }
        catch (TencentCloudSDKException e){
            log.error(e.getErrorCode(), e);
            throw new ApiException(e.getMessage());
        }
    }

    @Override
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

    @Override
    public List<MediaInfo> listMediaInfo(ListMediaRequestDTO listMediaAO) {
        var query = new LambdaQueryWrapper<MediaInfo>();
        if (!CollectionUtils.isEmpty(listMediaAO.getFileIdList())){
            query.in(MediaInfo::getFileId, listMediaAO.getFileIdList());
        }
        return mediaInfoMapper.selectList(query);
    }

    public List<MediaInfo> getMediaInfo(List<String> fileIds){
        var query = new LambdaQueryWrapper<MediaInfo>();

        if (fileIds.isEmpty()){
            return Collections.emptyList();
        }
        query.in(MediaInfo::getFileId, fileIds);
        return mediaInfoMapper.selectList(query);
    }

    public List<ContentReviewResult> listContentReviewResult(String fileId){
        var query = new LambdaQueryWrapper<ContentReviewResult>();
        if (!StringUtils.isEmpty(fileId)){
            query.eq(ContentReviewResult::getFileId, fileId);
        }
        return contentReviewResultMapper.selectList(query);
    }

    public MediaTranscodeItem getMediaTranscodeItem(String fileId){
        LambdaQueryWrapper<MediaTranscodeItem> query = buildQuery(fileId, null);
        return mediaTranscodeItemMapper.selectOne(query);
    }

    @Override
    public List<MediaTranscodeItem> listMediaTranscodeItem(List<String> fileIdList){
        LambdaQueryWrapper<MediaTranscodeItem> query = buildQuery(null, fileIdList);
        return mediaTranscodeItemMapper.selectList(query);
    }

    private LambdaQueryWrapper<MediaTranscodeItem> buildQuery(String fileId, List<String> fileIdList) {
        var query = new LambdaQueryWrapper<MediaTranscodeItem>();
        if (!StringUtils.isEmpty(fileId)){
            query.eq(MediaTranscodeItem::getFileId, fileId);
        }
        if (!CollectionUtils.isEmpty(fileIdList)){
            query.in(MediaTranscodeItem::getFileId, fileIdList);
        }
        return query;
    }

    private void notifyContentReviewResult(String fileId, String notifyUrl, SuggestionTypeEnum reviewResultEnum) {
        List<ContentReviewResultNotify> contentReviewResultNotifyList = getContentReviewResultNotify(fileId, notifyUrl);
        boolean notifySuccess = false;
        try {
            ContentReviewResultDTO contentReviewResultDTO = ContentReviewResultDTO.builder().fileId(fileId).reviewResult(reviewResultEnum.name()).build();
            restTemplate.postForEntity(notifyUrl, contentReviewResultDTO, R.class);
            notifySuccess = true;
        }
        catch (Exception e){
            log.error(e.getMessage(), e);
        }
        for (ContentReviewResultNotify notify: contentReviewResultNotifyList) {
            notify.setNotifySuccess(notifySuccess);
            notify.setRetryCount(notify.getRetryCount() +1);
            contentReviewResultNotifyMapper.updateById(notify);
        }
    }

    private Optional<SuggestionTypeEnum> getContentReviewResult(String fileId){
        List<ContentReviewResult> contentReviewResults = listContentReviewResult(fileId);
        return getContentReviewResult(contentReviewResults);
    }

    private Optional<SuggestionTypeEnum> getContentReviewResult(List<ContentReviewResult> contentReviewResults) {
        if (contentReviewResults.isEmpty()){
            return Optional.empty();
        }
        SuggestionTypeEnum reviewResultEnum = contentReviewResults.stream().allMatch(item -> !item.getSuggestion().equals(SuggestionTypeEnum.PASS.name())) ?
                SuggestionTypeEnum.BLOCK : SuggestionTypeEnum.PASS;
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
        Optional<ContentReviewResult> noPass = contentReviewResults.stream().filter(item -> SuggestionTypeEnum.BLOCK.name().equals(item.getSuggestion())).findAny();
        if (noPass.isPresent()){
            deleteMedia(fileId);
        }
        try(DistributeRedisLock lock = new DistributeRedisLock(redisKeyService.getContentReviewNotifyLockKey(fileId))){
            String notifyUrl = redisService.getCacheObject(redisKeyService.getContentReviewNotifyUrl(fileId), String.class);
            if (!StringUtils.isEmpty(notifyUrl)){
                Optional<SuggestionTypeEnum> contentReviewResult = getContentReviewResult(contentReviewResults);
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

    private List<ContentReviewResultNotify> getContentReviewResultNotify(String fileId, String notifyUrl){
        var query = new LambdaQueryWrapper<ContentReviewResultNotify>();
        if (!StringUtils.isEmpty(fileId)){
            query.eq(ContentReviewResultNotify::getFileId, fileId);
        }
        if (!StringUtils.isEmpty(notifyUrl)){
            query.eq(ContentReviewResultNotify::getNotifyUrl, notifyUrl);
        }
        return contentReviewResultNotifyMapper.selectList(query);
    }
}
