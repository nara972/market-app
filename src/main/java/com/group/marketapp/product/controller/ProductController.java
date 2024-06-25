package com.group.marketapp.product.controller;

import com.group.marketapp.product.doamin.Product;
import com.group.marketapp.product.dto.request.CreateProductRequestDto;
import com.group.marketapp.product.dto.response.ProductResponseDto;
import com.group.marketapp.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/product/{id}")
    public ProductResponseDto getProduct(@PathVariable Long id){
        return productService.getProduct(id);
    }

    @PostMapping("/product")
    public void createProduct(@RequestBody CreateProductRequestDto requeset) {
        productService.createProduct(requeset);
    }

}
