package com.group.marketapp.repository;

import com.group.marketapp.domain.ProductSearchDocument;

import java.util.List;

public interface ProductSearchRepositoryCustom {
    List<ProductSearchDocument> searchByKeywordAndPrice(
            String keyword, int minPrice, int maxPrice, String orderBy);
}
