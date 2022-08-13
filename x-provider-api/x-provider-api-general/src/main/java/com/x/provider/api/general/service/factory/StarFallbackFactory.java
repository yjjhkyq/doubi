package com.x.provider.api.general.service.factory;

import com.x.core.web.api.R;
import com.x.provider.api.general.model.dto.IsStarredRequestDTO;
import com.x.provider.api.general.model.dto.ListStarRequestDTO;
import com.x.provider.api.general.model.dto.StarRequestDTO;
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
            public R<Boolean> isStarred(IsStarredRequestDTO isStarred) {
                return R.ok(false);
            }

            @Override
            public R<Boolean> star(StarRequestDTO starAO) {
                return R.ok();
            }

            @Override
            public R<List<StarDTO>> listStar(ListStarRequestDTO listStarAO) {
                return R.ok(new ArrayList<>());
            }
        };
    }
}
