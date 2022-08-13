package com.x.provider.general.controller.rpc;

import com.x.core.utils.BeanUtil;
import com.x.core.web.api.R;
import com.x.provider.api.general.model.dto.CommentRequestDTO;
import com.x.provider.api.general.model.dto.CommentStatisticDTO;
import com.x.provider.api.general.service.CommentRpcService;
import com.x.provider.general.service.CommentService;
import com.x.provider.general.service.CommentStatService;
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
@RequestMapping("/rpc/general/comment")
public class CommentRpcController implements CommentRpcService {

    private final CommentService commentService;
    private final CommentStatService commentStatService;

    public CommentRpcController(CommentService commentService,
                                CommentStatService commentStatService){
        this.commentService = commentService;
        this.commentStatService = commentStatService;
    }

    @PostMapping("reply")
    @Override
    public R<Void> comment(@RequestBody CommentRequestDTO commentAO) {
        commentService.comment(commentAO);
        return R.ok();
    }

    @PostMapping("stat/list")
    @Override
    public R<Map<Long, CommentStatisticDTO>> listCommentStatMap(String idList) {
        return R.ok(BeanUtil.prepare(commentStatService.listCommentStatMap(StringUtil.parse(idList)).values(), CommentStatisticDTO.class).stream()
                .collect(Collectors.toMap(CommentStatisticDTO::getId, item -> item)));
    }
}
