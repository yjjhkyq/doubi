package com.x.provider.video.controller.frontent;

import com.x.core.utils.BeanUtil;
import com.x.core.web.api.R;
import com.x.core.web.controller.BaseFrontendController;
import com.x.provider.video.model.ao.TopicSearchAO;
import com.x.provider.video.model.vo.TopicSearchItemVO;
import com.x.provider.video.service.TopicService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "视频主题服务")
@RestController
@RequestMapping("/frontend/topic")
public class TopicController extends BaseFrontendController {

    private final TopicService topicService;

    public TopicController(TopicService topicService){
        this.topicService = topicService;
    }

    @ApiOperation(value = "搜索视频主题")
    @PostMapping("/search")
    public R<List<TopicSearchItemVO>> search(@RequestBody TopicSearchAO topicSearchAO){
        var topics = topicService.searchTopic(topicSearchAO.getKeyWords());
        return R.ok(BeanUtil.prepare(topics, TopicSearchItemVO.class));
    }
}
