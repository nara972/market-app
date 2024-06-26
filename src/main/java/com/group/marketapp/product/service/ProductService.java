package com.group.marketapp.product.service;

import com.group.marketapp.product.doamin.Product;
import com.group.marketapp.product.doamin.ProductCategory;
import com.group.marketapp.product.dto.request.CreateProductRequestDto;
import com.group.marketapp.product.dto.request.UpdateProductRequestDto;
import com.group.marketapp.product.dto.response.ProductResponseDto;
import com.group.marketapp.product.repository.ProductCategoryRepository;
import com.group.marketapp.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;

    public ProductResponseDto getProduct(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);

        return ProductResponseDto.of(product);

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
    }

}
