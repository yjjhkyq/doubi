package com.x.provider.cms.repository;

import com.x.provider.cms.model.domain.SecurityDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SecurityDocumentRepository extends ElasticsearchRepository<SecurityDocument, Long> {
}
