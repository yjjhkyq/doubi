package com.x.provider.cms.controller.frontend;

import com.x.core.utils.BeanUtil;
import com.x.core.web.api.R;
import com.x.core.web.page.TableDataInfo;
import com.x.core.web.page.TableSupport;
import com.x.provider.api.customer.enums.CustomerOptions;
import com.x.provider.api.customer.model.ao.ListCustomerAO;
import com.x.provider.api.customer.model.dto.CustomerDTO;
import com.x.provider.api.customer.service.CustomerRpcService;
import com.x.provider.api.finance.model.ao.ListSecurityAO;
import com.x.provider.api.finance.model.dto.SecurityDTO;
import com.x.provider.api.finance.service.FinanceRpcService;
import com.x.provider.api.video.enums.TopicSourceTypeEnum;
import com.x.provider.api.vod.enums.MediaTypeEnum;
import com.x.provider.api.vod.model.ao.ListMediaUrlAO;
import com.x.provider.api.vod.service.VodRpcService;
import com.x.provider.cms.model.domain.CustomerDocument;
import com.x.provider.cms.model.domain.SecurityDocument;
import com.x.provider.cms.model.domain.TopicDocument;
import com.x.provider.cms.model.domain.VideoDocument;
import com.x.provider.cms.model.vo.CustomerDocumentVO;
import com.x.provider.cms.model.vo.SecurityDocumentVO;
import com.x.provider.cms.model.vo.TopicDocumentVO;
import com.x.provider.cms.model.vo.VideoDocumentVO;
import com.x.provider.cms.service.SearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Max;
import java.util.*;
import java.util.stream.Collectors;

@Api(tags = "搜索服务")
@RestController
@RequestMapping("/frontend/search")
public class SearchController {

    private final SearchService searchService;
    private final FinanceRpcService financeRpcService;
    private final CustomerRpcService customerRpcService;
    private final VodRpcService vodRpcService;

    public SearchController(SearchService searchService,
                            FinanceRpcService financeRpcService,
                            CustomerRpcService customerRpcService,
                            VodRpcService vodRpcService){
        this.searchService = searchService;
        this.financeRpcService = financeRpcService;
        this.customerRpcService = customerRpcService;
        this.vodRpcService = vodRpcService;
    }

    @ApiOperation(value = "搜索股票")
    @GetMapping("/security")
    public R<TableDataInfo<SecurityDocumentVO>> searchSecurity(@ApiParam(value = "关键字") @RequestParam String keyword,
                                                            @ApiParam(value = "页") @RequestParam(required = false, defaultValue = "1") int page,
                                                            @ApiParam(value = "每页大小，对于auto complete 搜索，此值填入8,代表发发发") @RequestParam(required = false, defaultValue = "8") @Max(20) int size){
        final Page<SecurityDocument> securityDocuments = searchService.searchSecurity(keyword, TableSupport.getPageRequest());
        return R.ok(TableDataInfo.prepare(securityDocuments, (t) -> BeanUtil.prepare(t, SecurityDocumentVO.class)));
    }

    @ApiOperation(value = "搜索话题")
    @GetMapping("/topic")
    public R<TableDataInfo<TopicDocumentVO>> searchTopic(@ApiParam(value = "关键字") @RequestParam String keyword,
                                                            @ApiParam(value = "页") @RequestParam(required = false, defaultValue = "1") int page,
                                                            @ApiParam(value = "每页大小，对于auto complete 搜索，此值填入8,代表发发发") @RequestParam(required = false, defaultValue = "8") @Max(20) int size){
        final Page<TopicDocument> securityDocuments = searchService.searchTopic(keyword, TableSupport.getPageRequest());
        TableDataInfo<TopicDocumentVO> result = TableDataInfo.prepare(securityDocuments, (t) -> BeanUtil.prepare(t, TopicDocumentVO.class));
        Set<Long> securityIdList = result.getList().stream().filter(item -> item.getSourceType().equals(TopicSourceTypeEnum.SECURITY.ordinal())).map(TopicDocumentVO::getSourceId).filter(item -> StringUtils.hasText(item))
                .map(Long::valueOf).collect(Collectors.toSet());
        Map<Long, SecurityDTO> securityMap = financeRpcService.listSecurity(ListSecurityAO.builder().ids(new ArrayList<>(securityIdList)).build()).stream().collect(Collectors.toMap(SecurityDTO::getId, item -> item));
        result.getList().forEach(item -> {
            if (Objects.equals(item.getSourceType(), TopicSourceTypeEnum.SECURITY.ordinal())){
                SecurityDTO security = securityMap.get(Long.valueOf(item.getSourceId()));
                item.setSecurityDocument(BeanUtil.prepare(security, SecurityDocumentVO.class));
            }
        });
        return R.ok(result);
    }

    @ApiOperation(value = "搜索视频")
    @GetMapping("/video")
    public R<TableDataInfo<VideoDocumentVO>> searchVideo(@ApiParam(value = "关键字") @RequestParam String keyword,
                                                         @ApiParam(value = "页") @RequestParam(required = false, defaultValue = "1") int page,
                                                         @ApiParam(value = "每页大小，对于auto complete 搜索，此值填入8,代表发发发") @RequestParam(required = false, defaultValue = "8") @Max(20) int size){
        final Page<VideoDocument> securityDocuments = searchService.searchVideo(keyword, TableSupport.getPageRequest());
        TableDataInfo<VideoDocumentVO> result = TableDataInfo.prepare(securityDocuments, (t) -> BeanUtil.prepare(t, VideoDocumentVO.class));
        Set<Long> customerIdList = result.getList().stream().map(VideoDocumentVO::getCustomerId).collect(Collectors.toSet());
        Set<String> videoFileIds = result.getList().stream().map(VideoDocumentVO::getFileId).collect(Collectors.toSet());
        Map<Long, CustomerDTO> customerMap = customerRpcService.listCustomer(ListCustomerAO.builder().customerIds(new ArrayList<>(customerIdList))
                .customerOptions(Arrays.asList(CustomerOptions.CUSTOMER_ATTRIBUTE.name())).build()).getData();
        Map<String, String> mediaMap = vodRpcService.listMediaUrl(ListMediaUrlAO.builder().fileIds(new ArrayList<>(videoFileIds)).mediaType(MediaTypeEnum.COVER).build());
        result.getList().forEach(item -> {
            item.setCoverUrl(mediaMap.getOrDefault(item.getFileId(), ""));
            CustomerDTO customer = customerMap.get(item.getCustomerId());
            if (customer != null){
                item.setNickname(customer.getCustomerAttribute().getNickName());
                item.setAvatarUrl(customer.getCustomerAttribute().getAvatarUrl());
            }
        });
        return R.ok(result);
    }

    @ApiOperation(value = "搜索用户信息")
    @GetMapping("/customer")
    public R<TableDataInfo<CustomerDocumentVO>> searchCustomer(@ApiParam(value = "关键字") @RequestParam String keyword,
                                                               @ApiParam(value = "页") @RequestParam(required = false, defaultValue = "1") int page,
                                                               @ApiParam(value = "每页大小，对于auto complete 搜索，此值填入8,代表发发发") @RequestParam(required = false, defaultValue = "8") @Max(20) int size){
        final Page<CustomerDocument> securityDocuments = searchService.searchCustomer(keyword, TableSupport.getPageRequest());
        return R.ok(TableDataInfo.prepare(securityDocuments, (t) -> BeanUtil.prepare(t, CustomerDocumentVO.class)));
    }
}
