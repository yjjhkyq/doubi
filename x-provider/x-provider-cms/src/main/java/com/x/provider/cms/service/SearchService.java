package com.x.provider.cms.service;

import com.x.provider.api.customer.model.event.CustomerEvent;
import com.x.provider.api.finance.model.event.SecurityChangedBatchEvent;
import com.x.provider.api.video.model.event.TopicBatchEvent;
import com.x.provider.api.video.model.event.VideoChangedEvent;
import com.x.provider.cms.model.domain.CustomerDocument;
import com.x.provider.cms.model.domain.SecurityDocument;
import com.x.provider.cms.model.domain.TopicDocument;
import com.x.provider.cms.model.domain.VideoDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchService {
    Page<SecurityDocument> searchSecurity(String keyword, Pageable page);
    Page<TopicDocument> searchTopic(String keyword, Pageable pageable);
    Page<VideoDocument> searchVideo(String keyword, Pageable pageable);
    Page<CustomerDocument> searchCustomer(String keyword, Pageable pageable);
    void initSecurityList();
    void onSecurityChanged(SecurityChangedBatchEvent event);
    void onTopicBatchChangedEvent(TopicBatchEvent event);
    void initTopicList();
    void onVideoChanged(VideoChangedEvent videoChangedEvent);
    void onCustomerInfoChanged(CustomerEvent customerEvent);
}
