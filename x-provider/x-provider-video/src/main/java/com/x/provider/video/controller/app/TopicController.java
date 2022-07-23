package com.x.provider.video.controller.app;

import com.x.core.utils.BeanUtil;
import com.x.core.web.api.R;
import com.x.core.web.controller.BaseFrontendController;
import com.x.core.web.page.PageList;
import com.x.provider.api.finance.model.ao.ListSecurityAO;
import com.x.provider.api.finance.model.dto.SecurityDTO;
import com.x.provider.api.finance.service.FinanceRpcService;
import com.x.provider.api.video.enums.TopicSourceTypeEnum;
import com.x.provider.video.model.ao.topic.FavoriteToggleTopicAO;
import com.x.provider.video.model.domain.Topic;
import com.x.provider.video.model.vo.topic.SecurityVO;
import com.x.provider.video.model.vo.topic.TopicVO;
import com.x.provider.video.service.TopicService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Api(tags = "视频主题服务")
@RestController
@RequestMapping("/app/topic")
public class TopicController extends BaseFrontendController {

    private final TopicService topicService;
    private final FinanceRpcService financeRpcService;

    public TopicController(TopicService topicService,
                           FinanceRpcService financeRpcService){
        this.topicService = topicService;
        this.financeRpcService = financeRpcService;
    }

    @ApiOperation(value = "自选取消自选主题")
    @PostMapping("/favorite/toggle")
    public R<Void> favoriteTopicToggle(@RequestBody FavoriteToggleTopicAO favoriteToggleTopicAO){
        topicService.favoriteTopic(getCurrentCustomerId(), favoriteToggleTopicAO.getId(), favoriteToggleTopicAO.isFavorite());
        return R.ok();
    }

    @ApiOperation(value = "自选话题")
    @GetMapping("/favorite/list")
    public R<PageList<TopicVO>> listFavoriteTopic(@RequestParam(required = false, defaultValue = "0") long cursor,
                                                      @RequestParam int pageSize){
        PageList<Topic> topics = topicService.listTopic(getCurrentCustomerId(), getPageDomain());
        List<TopicVO> topicList = prepare(topics.getList());
        topicList.forEach(item -> item.setMyFavorite(true));
        return R.ok(PageList.map(topics, topicList));
    }

    @ApiOperation(value = "主题详情")
    @GetMapping("/detail")
    public R<TopicVO> topicDetail(@ApiParam(value = "主题id") @RequestParam long id){
        final Optional<Topic> topic = topicService.getTopic(id, null);
        TopicVO result = prepare(Arrays.asList(topic.get())).get(0);
        result.setMyFavorite(topicService.isFavoriteTopic(getCurrentCustomerId(), id));
        return R.ok(result);
    }

    private List<TopicVO> prepare(List<Topic> topics){
        if (topics.isEmpty()){
            return new ArrayList<>();
        }
        Set<Long> securityIdList = topics.stream().filter(item -> item.getSourceType().equals(TopicSourceTypeEnum.SECURITY.ordinal())).map(Topic::getSourceId).filter(item -> StringUtils.hasText(item))
                .map(Long::valueOf).collect(Collectors.toSet());
        Map<Long, SecurityDTO> securityMap = financeRpcService.listSecurity(ListSecurityAO.builder().ids(new ArrayList<>(securityIdList)).build()).stream().collect(Collectors.toMap(SecurityDTO::getId, item -> item));
        List<TopicVO> result = new ArrayList<>(topics.size());
        topics.forEach(item -> {
            TopicVO topicVO = BeanUtil.prepare(item, TopicVO.class);
            if (TopicSourceTypeEnum.SECURITY.ordinal() == item.getSourceType().intValue()){
                topicVO.setSecurity(BeanUtil.prepare(securityMap.get(Long.parseLong(item.getSourceId())), SecurityVO.class));
            }
            result.add(topicVO);
        });
        return result;
    }
}
