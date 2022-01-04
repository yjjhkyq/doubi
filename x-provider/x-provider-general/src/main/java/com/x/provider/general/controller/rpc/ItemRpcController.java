package com.x.provider.general.controller.rpc;

import com.x.core.utils.BeanUtil;
import com.x.core.web.api.R;
import com.x.provider.api.general.model.ao.CommentAO;
import com.x.provider.api.general.model.dto.CommentStatisticDTO;
import com.x.provider.api.general.model.dto.ItemStatisticDTO;
import com.x.provider.api.general.service.CommentRpcService;
import com.x.provider.api.general.service.ItemRpcService;
import com.x.provider.general.service.CommentService;
import com.x.provider.general.service.ItemStatService;
import com.x.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/rpc/general/item")
public class ItemRpcController implements ItemRpcService {

    private final ItemStatService itemStatService;

    public ItemRpcController(ItemStatService itemStatService){
        this.itemStatService = itemStatService;
    }

    @PostMapping("/list")
    @Override
    public R<Map<Long, ItemStatisticDTO>> listStatMap(int itemType, String idList) {
        return R.ok(BeanUtil.prepare(itemStatService.listItemStatMap(itemType, StringUtil.parse(idList)).values(), ItemStatisticDTO.class).stream()
                .collect(Collectors.toMap(ItemStatisticDTO::getItemId, item ->item)));
    }
}
