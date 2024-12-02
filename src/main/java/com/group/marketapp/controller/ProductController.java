package com.group.marketapp.controller;

import com.group.marketapp.dto.requestdto.CreateProductRequestDto;
import com.group.marketapp.dto.requestdto.UpdateProductRequestDto;
import com.group.marketapp.dto.responsedto.ProductResponseDto;
import com.group.marketapp.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> createProduct(@RequestBody CreateProductRequestDto request) {
        try {
            productService.createProduct(request);
            return ResponseEntity.ok("Product created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create product");
        }
    }

    @PutMapping("/product")
    public ResponseEntity<?> updateProduct(@RequestBody UpdateProductRequestDto request) {
        try {
            productService.updateProduct(request);
            return ResponseEntity.ok("Product updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update product");
        }
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok("Product deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete product");
        }
    }

}
