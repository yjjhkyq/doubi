package com.x.provider.api.general.service.factory;

import com.x.core.web.api.R;
import com.x.provider.api.general.model.ao.IsStarredAO;
import com.x.provider.api.general.model.ao.ListStarAO;
import com.x.provider.api.general.model.dto.StarDTO;
import com.x.provider.api.general.service.StarRpcService;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StarFallbackFactory implements FallbackFactory<StarRpcService> {

    @Override
    public StarRpcService create(Throwable throwable) {
        return new StarRpcService() {

            @Override
            public R<Boolean> isStarred(IsStarredAO isStarred) {
                return R.ok(false);
            }

            @Override
            public List<StarDTO> listStar(ListStarAO listStarAO) {
                return new ArrayList<>();
            }
        };
    }
}
