package com.x.provider.cms.repository;

import com.x.provider.cms.model.domain.CustomerDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CustomerDocumentRepository extends ElasticsearchRepository<CustomerDocument, Long> {
}
