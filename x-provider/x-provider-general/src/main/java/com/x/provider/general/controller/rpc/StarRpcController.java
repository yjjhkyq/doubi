package com.x.provider.general.controller.rpc;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.x.core.utils.BeanUtil;
import com.x.core.web.api.R;
import com.x.core.web.page.TableDataInfo;
import com.x.core.web.page.TableSupport;
import com.x.provider.api.general.model.ao.IsStarredAO;
import com.x.provider.api.general.model.ao.ListStarAO;
import com.x.provider.api.general.model.ao.StarAO;
import com.x.provider.api.general.model.dto.StarDTO;
import com.x.provider.api.general.model.event.StarRequestEvent;
import com.x.provider.api.general.service.StarRpcService;
import com.x.provider.general.component.KafkaConsumer;
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
    private final KafkaConsumer kafkaConsumer;
    public StarRpcController(StarService starService,
                             KafkaConsumer kafkaConsumer){
        this.starService = starService;
        this.kafkaConsumer = kafkaConsumer;
    }

    @PostMapping("is/starred")
    @Override
    public R<Boolean> isStarred(@RequestBody IsStarredAO isStarred) {
        return R.ok(starService.isStarred(isStarred.getItemType(), isStarred.getItemId(), isStarred.getCustomerId()));
    }

    @PostMapping("list")
    @Override
    public R<TableDataInfo<StarDTO>> listStar(@RequestBody ListStarAO listStarAO) {
        return R.ok(TableSupport.buildTableDataInfo(starService.listStar(listStarAO), (item) -> BeanUtil.prepare(item, StarDTO.class)));
    }

    @PostMapping("create")
    @Override
    public R<Void> star(StarAO starAO) {
        kafkaConsumer.onStarRequest(BeanUtil.prepare(starAO, StarRequestEvent.class));
        return R.ok();
    }
}
