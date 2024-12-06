package com.group.marketapp.service;

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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;
    private final ProductSearchRepository productSearchRepository;

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
                .orElseThrow(IllegalArgumentException::new);

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

    }

    public void deleteProduct(Long id){
        productRepository.updateStateProduct(true,id);
        deleteProductFromSearch(id);
    }

    // 검색 기능
    public List<ProductResponseDto> searchProducts(String keyword) {
        List<ProductSearchDocument> searchResults =
                productSearchRepository.findByNameOrCategoryName(keyword, keyword);

        return searchResults.stream()
                .map(doc -> ProductResponseDto.builder()
                        .id(doc.getId())
                        .name(doc.getName())
                        .categoryId(doc.getCategoryId())
                        .categoryName(doc.getCategoryName())
                        .price(doc.getPrice())
                        .build())
                .collect(Collectors.toList());
    }

    // 데이터 동기화
    public void syncProductToSearchDocument(Product product) {
        ProductSearchDocument searchDocument = ProductSearchDocument.builder()
                .id(product.getId())
                .name(product.getName())
                .categoryName(product.getProductCategory().getName())
                .categoryId(product.getProductCategory().getId())
                .price(product.getPrice())
                .isDeleted(product.isDeleted())
                .build();

        productSearchRepository.save(searchDocument);
    }

    // 상품 삭제 시 검색 인덱스에서도 삭제
    public void deleteProductFromSearch(Long id) {
        productSearchRepository.deleteById(id);
    }
}



