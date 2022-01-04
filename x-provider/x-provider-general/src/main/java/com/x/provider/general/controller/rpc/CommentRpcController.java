package com.x.provider.general.controller.rpc;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.x.core.utils.BeanUtil;
import com.x.core.web.api.R;
import com.x.core.web.page.PageList;
import com.x.core.web.page.PageHelper;
import com.x.provider.api.general.model.ao.*;
import com.x.provider.api.general.model.dto.CommentDTO;
import com.x.provider.api.general.model.dto.CommentStatisticDTO;
import com.x.provider.api.general.service.CommentRpcService;
import com.x.provider.general.model.domain.Comment;
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
    public R<Void> comment(@RequestBody CommentAO commentAO) {
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
