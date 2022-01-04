package com.x.provider.api.general.service;

import com.x.core.web.api.R;
import com.x.core.web.page.PageList;
import com.x.provider.api.general.constants.ServiceNameConstants;
import com.x.provider.api.general.model.ao.*;
import com.x.provider.api.general.model.dto.CommentDTO;
import com.x.provider.api.general.model.dto.CommentStatisticDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(contextId = "generalCommentService", value = ServiceNameConstants.SERVICE)
public interface CommentRpcService {
    @PostMapping(ServiceNameConstants.RPC_URL_PREFIX_COMMENT + "/reply")
    R<Void> comment(@RequestBody CommentAO commentAO);

    @PostMapping(ServiceNameConstants.RPC_URL_PREFIX_COMMENT + "/stat/list")
    R<Map<Long, CommentStatisticDTO>> listCommentStatMap(@RequestParam("idList") String idList);
}
