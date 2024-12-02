package com.group.marketapp.service;

import com.group.marketapp.domain.Product;
import com.group.marketapp.domain.ProductCategory;
import com.group.marketapp.dto.requestdto.CreateProductRequestDto;
import com.group.marketapp.dto.requestdto.UpdateProductRequestDto;
import com.group.marketapp.dto.responsedto.ProductResponseDto;
import com.group.marketapp.repository.ProductCategoryRepository;
import com.group.marketapp.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
