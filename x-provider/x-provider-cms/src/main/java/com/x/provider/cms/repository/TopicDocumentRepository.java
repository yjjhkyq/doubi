package com.x.provider.cms.repository;

import com.x.provider.cms.model.domain.TopicDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface TopicDocumentRepository extends ElasticsearchRepository<TopicDocument, Long> {
}
