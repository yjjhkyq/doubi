package com.x.provider.oss.service.impl;

import com.x.core.utils.JsonUtil;
import com.x.core.web.api.R;
import com.x.core.domain.SuggestionTypeEnum;
import com.x.provider.api.oss.model.dto.oss.AttributeGreenRequestDTO;
import com.x.provider.api.oss.model.dto.oss.GreenRequestDTO;
import com.x.provider.api.oss.model.dto.oss.AttributeGreenResultDTO;
import com.x.provider.oss.configure.TencentOssConfig;
import com.x.provider.oss.service.GreenService;
import com.x.provider.oss.service.RedisKeyService;
import com.x.redis.service.DistributeRedisLock;
import com.x.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service("tencentGreenService")
public class TencentGreenServiceImpl implements GreenService {

    private final RedisService redisService;
    private final RedisKeyService redisKeyService;
    private final RestTemplate restTemplate;

    public TencentGreenServiceImpl(RedisService redisService,
                                 RedisKeyService redisKeyService,
                                 RestTemplate restTemplate){
        this.redisService = redisService;
        this.redisKeyService = redisKeyService;
        this.restTemplate = restTemplate;
    }

    @Override
    public void onGreenResultNotify(Map<String, Object> result){
        log.info("green result notify, result:{}", JsonUtil.toJSONString(result));
        if((int)result.get("code") == 0){
            Map<String, Object> data = (Map<String, Object>)result.get("data");
            String url = (String) data.get("url");
            SuggestionTypeEnum suggestionTypeEnum = prepare((Integer) data.get("result"));
            String objectKey = url.substring(url.lastIndexOf(TencentOssConfig.DOMAIN_QCLOUD) + TencentOssConfig.DOMAIN_QCLOUD.length());
            try(DistributeRedisLock lock = new DistributeRedisLock(objectKey)) {
                Optional<AttributeGreenRequestDTO> attributeGreenRpcAO = redisService.getOptionalCacheObject(redisKeyService.getAttributeGreenRpcAOKey(objectKey), AttributeGreenRequestDTO.class);
                if (attributeGreenRpcAO.isPresent()) {
                    onGreenAttribute(attributeGreenRpcAO.get(), suggestionTypeEnum);
                } else {
                    redisService.setCacheObject(redisKeyService.getAttributeGreenResultKey(objectKey), suggestionTypeEnum.toString(), Duration.ofDays(30));
                }
            }
        } else{
            log.error("gren result error, error code:{}  error message:{}", result.get("code"), result.get("message"));
        }
    }

    @Override
    public SuggestionTypeEnum green(GreenRequestDTO greenRpcAO) {
        return null;
    }

    @Override
    public void greenAttributeAsync(AttributeGreenRequestDTO attributeGreenRpcAO) {
        Optional<String> suggestionType = Optional.empty();
        try(DistributeRedisLock lock = new DistributeRedisLock(attributeGreenRpcAO.getValue())) {
            suggestionType = redisService.getOptionalCacheObject(redisKeyService.getAttributeGreenResultKey(attributeGreenRpcAO.getValue()), String.class);
            if (!suggestionType.isPresent()) {
                redisService.setCacheObject(redisKeyService.getAttributeGreenRpcAOKey(attributeGreenRpcAO.getValue()), attributeGreenRpcAO, Duration.ofMinutes(2));
                return;
            }
        }
        onGreenAttribute(attributeGreenRpcAO, SuggestionTypeEnum.valueOf(suggestionType.get()));
    }

    @Override
    public AttributeGreenResultDTO greenAttributeSync(AttributeGreenRequestDTO attributeGreenRpcAO) {
        return null;
    }

    private SuggestionTypeEnum prepare(Integer greenResult){
        return greenResult == 0 ? SuggestionTypeEnum.PASS : SuggestionTypeEnum.BLOCK;
    }

    private void onGreenAttribute(AttributeGreenRequestDTO attributeGreenRpcAO, SuggestionTypeEnum suggestionType) {
        AttributeGreenResultDTO attributeGreenResultRpcVO = new AttributeGreenResultDTO();
        BeanUtils.copyProperties(attributeGreenRpcAO, attributeGreenResultRpcVO);
        attributeGreenResultRpcVO.setSuggestionType(suggestionType.toString());
        restTemplate.postForEntity(attributeGreenRpcAO.getCallbackUrl(), attributeGreenResultRpcVO, R.class);
    }
}
