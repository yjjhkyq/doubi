package com.x.provider.general.controller.rpc;

import com.x.core.utils.BeanUtil;
import com.x.core.web.api.R;
import com.x.provider.api.general.model.ao.IsStarredAO;
import com.x.provider.api.general.model.ao.ListStarAO;
import com.x.provider.api.general.model.dto.StarDTO;
import com.x.provider.api.general.service.StarRpcService;
import com.x.provider.general.service.StarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @Override
    public List<StarDTO> listStar(@RequestBody ListStarAO listStarAO) {
        return BeanUtil.prepare(starService.listStar(listStarAO.getAssociationItemId(), listStarAO.getStarCustomerId()), StarDTO.class);
    }
}
