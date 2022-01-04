package com.x.provider.api.general.service;

import com.x.core.web.api.R;
import com.x.provider.api.general.constants.ServiceNameConstants;
import com.x.provider.api.general.model.dto.CommentStatisticDTO;
import com.x.provider.api.general.model.dto.ItemStatisticDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(contextId = "itemService", value = ServiceNameConstants.SERVICE)
public interface ItemRpcService {

    @PostMapping(ServiceNameConstants.RPC_URL_PREFIX_ITEM + "/list")
    R<Map<Long, ItemStatisticDTO>> listStatMap(@RequestParam("itemType") int itemType, @RequestParam("idList") String idList);
}
