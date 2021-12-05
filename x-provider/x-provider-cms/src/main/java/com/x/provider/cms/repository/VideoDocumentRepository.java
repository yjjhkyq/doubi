package com.x.provider.cms.repository;

import com.x.provider.cms.model.domain.VideoDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface VideoDocumentRepository extends ElasticsearchRepository<VideoDocument, Long> {
}
