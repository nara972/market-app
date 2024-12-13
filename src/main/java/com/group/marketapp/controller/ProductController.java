package com.group.marketapp.controller;

import com.group.marketapp.dto.requestdto.CreateProductRequestDto;
import com.group.marketapp.dto.requestdto.UpdateProductRequestDto;
import com.group.marketapp.dto.responsedto.ProductResponseDto;
import com.group.marketapp.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "상품 관리", description = "상품 관련 API")
public class ProductController {

    private final ProductService productService;

    @Operation(
            summary = "상품 조회",
            description = "하나의 상품을 조회합니다."
    )
    @GetMapping("/product/{id}")
    public ProductResponseDto getProduct(@PathVariable Long id){
        return productService.getProduct(id);
    }

    @Operation(
            summary = "카테고리별 상품 조회",
            description = "카테고리별 상품을 조회합니다."
    )
    @GetMapping("/product/category/{id}")
    public List<ProductResponseDto> getProductCategory(@PathVariable Long id){
        return productService.getProductCategory(id);
    }

    @Operation(
            summary = "상품 생성",
            description = "새로운 상품을 생성합니다."
    )
    @PostMapping("/product")
    public ResponseEntity<?> createProduct(@RequestBody CreateProductRequestDto request) {
        try {
            productService.createProduct(request);
            return ResponseEntity.ok("Product created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create product");
        }
    }

    @Operation(
            summary = "상품 수정",
            description = "상품을 수정합니다."
    )
    @PutMapping("/product")
    public ResponseEntity<?> updateProduct(@RequestBody UpdateProductRequestDto request) {
        try {
            productService.updateProduct(request);
            return ResponseEntity.ok("Product updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update product");
        }
    }

    @Operation(
            summary = "상품 삭제",
            description = "상품을 삭제합니다."
    )
    @DeleteMapping("/product/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok("Product deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete product");
        }
    }
    
    @Operation(
            summary = "상품 검색",
            description = "상품명 또는 상품 카테고리명으로 상품을 검색합니다."
    )
    @GetMapping("/product/search")
    public ResponseEntity<List<ProductResponseDto>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int minPrice,
            @RequestParam(defaultValue = "1000000") int maxPrice,
            @RequestParam(defaultValue = "asc") String orderBy) {
        List<ProductResponseDto> results = productService.searchProducts(keyword, minPrice, maxPrice, orderBy);
        return ResponseEntity.ok(results);
    }

}
