package com.x.provider.api.oss.service.factory;

import com.x.core.web.api.R;
import com.x.provider.api.oss.model.ao.vod.DeleteMediaAO;
import com.x.provider.api.oss.model.ao.vod.GetContentReviewResultAO;
import com.x.provider.api.oss.model.ao.vod.ListMediaAO;
import com.x.provider.api.oss.model.ao.vod.ListMediaUrlAO;
import com.x.provider.api.oss.model.dto.vod.MediaInfoDTO;
import com.x.provider.api.oss.service.VodRpcService;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class VodFallbackFactory implements FallbackFactory<VodRpcService> {

    @Override
    public VodRpcService create(Throwable throwable) {
        return new VodRpcService() {

            @Override
            public R<Void> contentReview(GetContentReviewResultAO getContentReviewResultAO) {
                return null;
            }

            @Override
            public Map<String, String> listMediaUrl(ListMediaUrlAO listMediaUrlAO) {
                return null;
            }

            @Override
            public R<Void> deleteMedia(DeleteMediaAO deleteMediaAO) {
                return null;
            }

            @Override
            public R<MediaInfoDTO> getMediaInfo(String fileId) {
                return null;
            }

            @Override
            public R<List<MediaInfoDTO>> listMediaInfo(ListMediaAO listMediaAO) {
                return R.ok(new ArrayList<>());
            }
        };
    }
}
