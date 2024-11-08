package com.group.marketapp.product.controller;

import com.group.marketapp.product.dto.request.CreateProductRequestDto;
import com.group.marketapp.product.dto.request.UpdateProductRequestDto;
import com.group.marketapp.product.dto.response.ProductResponseDto;
import com.group.marketapp.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/product/{id}")
    public ProductResponseDto getProduct(@PathVariable Long id){
        return productService.getProduct(id);
    }

    @GetMapping("/product/category/{id}")
    public List<ProductResponseDto> getProductCategory(@PathVariable Long id){
        return productService.getProductCategory(id);
    }

    @PostMapping("/product")
    public void createProduct(@RequestBody CreateProductRequestDto request) {
        productService.createProduct(request);
    }

    @PutMapping("/product")
    public void updateProduct(@RequestBody UpdateProductRequestDto request) {
        productService.updateProduct(request);
    }

    @DeleteMapping("/product/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

}
