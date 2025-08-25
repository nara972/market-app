package com.group.marketapp.repository;

import com.group.marketapp.domain.ProductSearchDocument;

import java.util.List;

public interface ProductSearchRepositoryCustom {
    List<ProductSearchDocument> searchByKeyword(String keyword);
}
