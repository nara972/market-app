package com.group.marketapp.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.group.marketapp.domain.Product;
import com.group.marketapp.domain.ProductCategory;
import com.group.marketapp.domain.ProductSearchDocument;
import com.group.marketapp.dto.requestdto.CreateProductRequestDto;
import com.group.marketapp.dto.requestdto.UpdateProductRequestDto;
import com.group.marketapp.dto.responsedto.ProductResponseDto;
import com.group.marketapp.repository.ProductCategoryRepository;
import com.group.marketapp.repository.ProductRepository;
import com.group.marketapp.repository.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;
    private final ProductSearchRepository productSearchRepository;
    private final ElasticsearchClient elasticsearchClient;

    public ProductResponseDto getProduct(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);

        return ProductResponseDto.of(product);

    }

    @Cacheable(value = "productCategoryCache", key = "#id")
    public List<ProductResponseDto> getProductCategory(Long id){

        List<Product> products = productRepository.findProductsByCategoryIdWithSubcategories(id);

        return products.stream()
                .map(ProductResponseDto::of)
                .collect(Collectors.toList());

    }

    public void createProduct(CreateProductRequestDto request){

            ProductCategory category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));

            Product product = Product.builder()
                    .name(request.getName())
                    .price(request.getPrice())
                    .stock(request.getStock())
                    .productCategory(category)
                    .build();

            productRepository.save(product);

            syncProductToSearchDocument(product);
    }

    public void updateProduct(UpdateProductRequestDto request){
        Product product=productRepository.findById(request.getId())
                .orElseThrow(IllegalArgumentException::new);

        ProductCategory category = categoryRepository.findById(request.getCategoryId())
                        .orElseThrow(IllegalArgumentException::new);

        product.update(request,category);
        productRepository.save(product);

        syncProductToSearchDocument(product);
    }

    public void deleteProduct(Long id){
        productRepository.updateStateProduct(true,id);
        deleteProductFromSearch(id);
    }

    public List<ProductResponseDto> searchProducts(String keyword, int minPrice, int maxPrice, String orderBy) {
        try {
            List<ProductSearchDocument> searchResults =
                    productSearchRepository.searchByKeywordAndPrice(keyword, minPrice, maxPrice, orderBy);

            return searchResults.stream()
                    .map(doc -> ProductResponseDto.builder()
                            .id(doc.getId())
                            .name(doc.getName())
                            .categoryId(doc.getCategoryId())
                            .categoryName(doc.getCategoryName())
                            .price(doc.getPrice())
                            .build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error occurred during Elasticsearch search: " + e.getMessage(), e);
        }
    }

    public void syncProductToSearchDocument(Product product) {
        try {

            ProductSearchDocument searchDocument = ProductSearchDocument.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .categoryName(product.getProductCategory().getName())
                    .categoryId(product.getProductCategory().getId())
                    .price(product.getPrice())
                    .build();

            productSearchRepository.save(searchDocument);

        } catch (Exception e) {
            throw new RuntimeException("Failed to sync product to Elasticsearch: " + e.getMessage(), e);
        }
    }

    public void deleteProductFromSearch(Long id) {
        productSearchRepository.deleteById(id);
    }
}



