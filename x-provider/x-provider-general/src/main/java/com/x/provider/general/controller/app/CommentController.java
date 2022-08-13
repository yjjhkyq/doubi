package com.x.provider.general.controller.app;

import cn.hutool.core.date.DateUtil;
import com.x.core.utils.BeanUtil;
import com.x.core.utils.DateUtils;
import com.x.core.web.api.R;
import com.x.core.web.controller.BaseFrontendController;
import com.x.core.web.page.PageList;
import com.x.provider.api.customer.enums.CustomerOptions;
import com.x.provider.api.customer.model.dto.ListSimpleCustomerRequestDTO;
import com.x.provider.api.customer.model.dto.SimpleCustomerDTO;
import com.x.provider.api.customer.service.CustomerRpcService;
import com.x.provider.general.model.ao.ListCommentAO;
import com.x.provider.general.model.domain.Comment;
import com.x.provider.general.model.domain.CommentStatistic;
import com.x.provider.general.model.vo.CommentStatisticVO;
import com.x.provider.general.model.vo.CommentVO;
import com.x.provider.general.service.CommentService;
import com.x.provider.general.service.CommentStatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Api(tags = "评论服务")
@RestController
@RequestMapping("/app/comment")
public class CommentController extends BaseFrontendController {

    private final CommentService commentService;
    private final CommentStatService commentStatService;
    private final CustomerRpcService customerRpcService;

    public CommentController(CommentService commentService,
                             CommentStatService commentStatService,
                             CustomerRpcService customerRpcService){
        this.commentService = commentService;
        this.commentStatService = commentStatService;
        this.customerRpcService = customerRpcService;
    }

    @ApiOperation(value = "获取评论列表")
    @GetMapping("/list")
    public R<PageList<CommentVO>> listComment(@RequestParam(required = false, defaultValue = "0") long cursor,
                                              @RequestParam int pageSize,
                                              @RequestParam @ApiParam(value = "1 视频") int itemType,
                                              @RequestParam @ApiParam(value = "获取视频评论时填入视频id") long itemId,
                                              @RequestParam(required = false, defaultValue = "0") @ApiParam(value = "获取某一个评论下的回复列表时填入此值") Long rootCommentId){
        ListCommentAO listCommentAO = ListCommentAO.builder().itemId(itemId).itemType(itemType).rootCommentId(rootCommentId).pageDomain(getPageDomain()).build();
        PageList<Comment> comments = commentService.listComment(listCommentAO);
        if (comments.isEmptyList()){
            return R.ok(PageList.mapToEmptyListPage(comments));
        }
        Map<Long, CommentStatistic> commentStatMap = commentStatService.listCommentStatMap(comments.getList().stream().map(Comment::getId).collect(Collectors.toList()));
        Set<Long> customerIdLis = new HashSet<>(comments.getList().size() * 2);
        comments.getList().stream().forEach(item -> {
            customerIdLis.add(item.getCommentCustomerId());
            if (item.getParentCommentCustomerId() > 0){
                customerIdLis.add(item.getParentCommentCustomerId());
            }
        });
        Map<Long, SimpleCustomerDTO> customerMap = customerRpcService.listSimpleCustomer(ListSimpleCustomerRequestDTO.builder()
                .sessionCustomerId(getCurrentCustomerIdAndNotCheckLogin())
                .customerIds(new ArrayList<>(customerIdLis))
                .customerOptions(List.of(CustomerOptions.CUSTOMER_RELATION.name(), CustomerOptions.CUSTOMER_ATTRIBUTE.name()))
                .build())
                .getData();
        return R.ok(PageList.map(comments, (s) -> prepare(s, commentStatMap, customerMap)));
    }

    private CommentVO prepare(Comment comment, Map<Long, CommentStatistic> commentStatMap, Map<Long, SimpleCustomerDTO> customerMap){
        CommentVO result = BeanUtil.prepare(comment, CommentVO.class);
        result.setCommentCustomer(customerMap.get(comment.getCommentCustomerId()));
        if (comment.getParentCommentCustomerId() >0){
            result.setParentCommentCustomer(customerMap.get(comment.getParentCommentCustomerId()));
        }
        result.setStatistic(BeanUtil.prepare(commentStatMap.getOrDefault(comment.getId(), CommentStatistic.builder().id(comment.getId()).build()), CommentStatisticVO.class));
        return result;
    }

}
