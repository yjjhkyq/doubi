package com.x.provider.general.controller.rpc;

import com.x.core.web.api.R;
import com.x.provider.api.general.model.ao.IsStarredAO;
import com.x.provider.api.general.model.ao.StarAO;
import com.x.provider.api.general.service.StarRpcService;
import com.x.provider.general.service.StarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/rpc/general/star")
public class StarRpcController implements StarRpcService {

    private final StarService starService;
    public StarRpcController(StarService starService){
        this.starService = starService;
    }

    @PostMapping("is/starred")
    @Override
    public R<Boolean> isStarred(@RequestBody IsStarredAO isStarred) {
        return R.ok(starService.isStarred(isStarred.getItemType(), isStarred.getItemId(), isStarred.getCustomerId()));
    }

    @PostMapping("create")
    @Override
    public R<Boolean> star(@RequestBody StarAO starAO) {
        starService.star(starAO);
        return R.ok();
    }
}
