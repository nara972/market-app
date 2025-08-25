package com.group.marketapp.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;
import com.group.marketapp.domain.ProductSearchDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ProductSearchRepositoryImpl implements ProductSearchRepositoryCustom {

    private final ElasticsearchClient elasticsearchClient;

    @Override
    public List<ProductSearchDocument> searchByKeyword(String keyword) {
        // 상품명 OR 카테고리명 매칭
        Query boolQuery = QueryBuilders.bool()
                .should(QueryBuilders.match().field("name").query(keyword).build()._toQuery())
                .should(QueryBuilders.match().field("categoryName").query(keyword).build()._toQuery())
                .build()._toQuery();

        SearchRequest searchRequest = new SearchRequest.Builder()
                .index("products")   // products 인덱스에서 검색
                .query(boolQuery)
                .build();

        try {
            SearchResponse<ProductSearchDocument> searchResponse =
                    elasticsearchClient.search(searchRequest, ProductSearchDocument.class);

            return searchResponse.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Error occurred during Elasticsearch search: " + e.getMessage(), e);
        }
    }
}