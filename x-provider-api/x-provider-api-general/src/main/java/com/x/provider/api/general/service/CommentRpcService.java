package com.x.provider.api.general.service;

import com.x.core.web.api.R;
import com.x.core.web.page.TableDataInfo;
import com.x.provider.api.general.constants.ServiceNameConstants;
import com.x.provider.api.general.model.ao.*;
import com.x.provider.api.general.model.dto.CommentDTO;
import com.x.provider.api.general.model.dto.StarDTO;
import com.x.provider.api.general.service.factory.StarFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "generalCommentService", value = ServiceNameConstants.SERVICE, fallbackFactory = StarFallbackFactory.class)
public interface CommentRpcService {
    @PostMapping(ServiceNameConstants.RPC_URL_PREFIX_COMMENT + "/insert")
    R<Void> comment(@RequestBody CommentAO commentAO);

    @PostMapping(ServiceNameConstants.RPC_URL_PREFIX_COMMENT + "/reply")
    R<Void> commentReply(@RequestBody CommentReplyAO commentReplyAO);

    @PostMapping(ServiceNameConstants.RPC_URL_PREFIX_COMMENT + "/list")
    R<TableDataInfo<CommentDTO>> listComment(@RequestBody ListCommentAO listCommentAO);
}
