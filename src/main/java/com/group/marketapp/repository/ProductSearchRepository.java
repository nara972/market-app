package com.group.marketapp.repository;

import com.group.marketapp.domain.ProductSearchDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ProductSearchRepository extends ElasticsearchRepository<ProductSearchDocument, Long> {

    // 상품명 또는 카테고리명으로 검색
    List<ProductSearchDocument> findByNameOrCategoryName(String name, String categoryName);
}
