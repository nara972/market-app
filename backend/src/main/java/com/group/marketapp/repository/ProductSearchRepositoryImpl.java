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
    public List<ProductSearchDocument> searchByKeywordAndPrice(String keyword, int minPrice, int maxPrice, String orderBy) {
        Query boolQuery = QueryBuilders.bool()
                .must(QueryBuilders.bool()
                        .should(QueryBuilders.match().field("name").query(keyword).build()._toQuery())
                        .should(QueryBuilders.match().field("categoryName").query(keyword).build()._toQuery())
                        .build()._toQuery())
                .filter(QueryBuilders.range()
                        .field("price")
                        .gte(JsonData.of(minPrice))
                        .lte(JsonData.of(maxPrice))
                        .build()._toQuery())
                .build()._toQuery();

        SortOptions sortOptions = SortOptions.of(s -> s
                .field(f -> f
                        .field("price")
                        .order(orderBy.equalsIgnoreCase("asc") ? SortOrder.Asc : SortOrder.Desc)
                )
        );

        SearchRequest searchRequest = new SearchRequest.Builder()
                .index("products")
                .query(boolQuery)
                .sort(sortOptions)
                .build();

        try {
            SearchResponse<ProductSearchDocument> searchResponse = elasticsearchClient.search(
                    searchRequest,
                    ProductSearchDocument.class
            );

            return searchResponse.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Error occurred during Elasticsearch search: " + e.getMessage(), e);
        }
    }
}