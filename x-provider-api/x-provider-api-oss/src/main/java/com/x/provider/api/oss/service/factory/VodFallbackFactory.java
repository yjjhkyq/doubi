package com.x.provider.api.oss.service.factory;

import com.x.core.web.api.R;
import com.x.provider.api.oss.model.dto.vod.DeleteMediaRequestDTO;
import com.x.provider.api.oss.model.dto.vod.GetContentReviewResultRequestDTO;
import com.x.provider.api.oss.model.dto.vod.ListMediaRequestDTO;
import com.x.provider.api.oss.model.dto.vod.ListMediaUrlRequestDTO;
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
            public R<Void> contentReview(GetContentReviewResultRequestDTO getContentReviewResultAO) {
                return null;
            }

            @Override
            public Map<String, String> listMediaUrl(ListMediaUrlRequestDTO listMediaUrlAO) {
                return null;
            }

            @Override
            public R<Void> deleteMedia(DeleteMediaRequestDTO deleteMediaAO) {
                return null;
            }

            @Override
            public R<MediaInfoDTO> getMediaInfo(String fileId) {
                return null;
            }

            @Override
            public R<List<MediaInfoDTO>> listMediaInfo(ListMediaRequestDTO listMediaAO) {
                return R.ok(new ArrayList<>());
            }
        };
    }
}
