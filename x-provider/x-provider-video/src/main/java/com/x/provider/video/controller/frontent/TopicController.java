package com.x.provider.video.controller.frontent;

import com.x.core.utils.BeanUtil;
import com.x.core.web.api.R;
import com.x.core.web.controller.BaseFrontendController;
import com.x.provider.api.finance.model.ao.ListSecurityAO;
import com.x.provider.api.finance.model.dto.SecurityDTO;
import com.x.provider.api.finance.service.FinanceRpcService;
import com.x.provider.api.video.enums.TopicSourceTypeEnum;
import com.x.provider.video.model.ao.topic.FavoriteToggleTopicAO;
import com.x.provider.video.model.ao.topic.TopicSearchAO;
import com.x.provider.video.model.domain.Topic;
import com.x.provider.video.model.vo.topic.TopicDetailVO;
import com.x.provider.video.model.vo.topic.TopicSearchItemVO;
import com.x.provider.video.service.TopicService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Api(tags = "视频主题服务")
@RestController
@RequestMapping("/frontend/topic")
public class TopicController extends BaseFrontendController {

    private final TopicService topicService;
    private final FinanceRpcService financeRpcService;

    public TopicController(TopicService topicService,
                           FinanceRpcService financeRpcService){
        this.topicService = topicService;
        this.financeRpcService = financeRpcService;
    }

    @ApiOperation(value = "搜索视频主题")
    @PostMapping("/search")
    public R<List<TopicSearchItemVO>> search(@RequestBody TopicSearchAO topicSearchAO){
        var topics = topicService.searchTopic(topicSearchAO.getKeyWords());
        return R.ok(BeanUtil.prepare(topics, TopicSearchItemVO.class));
    }

    @ApiOperation(value = "自选取消自选主题")
    @PostMapping("/favorite/toggle")
    public R<Void> favoriteTopicToggle(@RequestBody FavoriteToggleTopicAO favoriteToggleTopicAO){
        topicService.favoriteTopic(getCurrentCustomerId(), favoriteToggleTopicAO.getId(), favoriteToggleTopicAO.isFavorite());
        return R.ok();
    }

    @ApiOperation(value = "主题详情")
    @PostMapping("/detail")
    public R<TopicDetailVO> topicDetail(@ApiParam(value = "主题id") @RequestParam long id){
        final Optional<Topic> topic = topicService.getTopic(id, null);
        return R.ok(prepareTopicDetail(topic.get(), getCurrentCustomerIdAndNotCheckLogin()));
    }

    private TopicDetailVO prepareTopicDetail(Topic topic, long currentCustomerId){
        if (topic == null){
            return null;
        }
        TopicDetailVO topicDetailVO = new TopicDetailVO();
        topicDetailVO.setId(topic.getId());
        topicDetailVO.setTitle(topic.getTitle());
        topicDetailVO.setSourceType(topic.getSourceType());
        if (topicDetailVO.getSourceType() == TopicSourceTypeEnum.SECURITY.ordinal()){
            final List<SecurityDTO> securityList = financeRpcService.listSecurity(ListSecurityAO.builder().ids(Arrays.asList(Long.parseLong(topic.getSourceId()))).build());
            if (!securityList.isEmpty()){
                topicDetailVO.setCode(securityList.get(0).getCode());
            }
        }
        topicDetailVO.setMyFavorite(topicService.isFavoriteTopic(currentCustomerId, topic.getId()));
        return topicDetailVO;
    }
}
