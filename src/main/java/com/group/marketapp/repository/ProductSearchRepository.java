package com.group.marketapp.repository;

import com.group.marketapp.domain.ProductSearchDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductSearchRepository extends ElasticsearchRepository<ProductSearchDocument, Long>,ProductSearchRepositoryCustom{

}
