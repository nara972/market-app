package com.group.marketapp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Transactional
public class ProductServiceTest {

    /**
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductCategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    private ProductCategory testCategory;

    @BeforeEach
    void setup() {
        // 카테고리 생성
        testCategory = ProductCategory.builder()
                .name("Electronics")
                .build();
        categoryRepository.save(testCategory);
    }

    @Test
    void testCreateProduct() {
        CreateProductRequestDto request = new CreateProductRequestDto();
        request.setName("Smartphone");
        request.setPrice(1000);
        request.setStock(50);
        request.setCategoryId(testCategory.getId());

        productService.createProduct(request);

        // 상품 저장 확인
        var savedProducts = productRepository.findByProductCategory(testCategory);
        assertThat(savedProducts).hasSize(1);

        var savedProduct = savedProducts.get(0);
        assertThat(savedProduct.getName()).isEqualTo("Smartphone");
        assertThat(savedProduct.getPrice()).isEqualTo(1000);
        assertThat(savedProduct.getStock()).isEqualTo(50);
        assertThat(savedProduct.isDeleted()).isFalse();
    }

    @Test
    void testGetProduct() {
        // 테스트 상품 생성
        CreateProductRequestDto request = new CreateProductRequestDto();
        request.setName("Smartphone");
        request.setPrice(1000);
        request.setStock(50);
        request.setCategoryId(testCategory.getId());
        productService.createProduct(request);

        var savedProduct = productRepository.findByProductCategory(testCategory).get(0);
        ProductResponseDto response = productService.getProduct(savedProduct.getId());

        // 상품 조회 확인
        assertThat(response.getName()).isEqualTo("Smartphone");
        assertThat(response.getPrice()).isEqualTo(1000);
        assertThat(response.getStock()).isEqualTo(50);
    }

    @Test
    void testGetProductCategory() {
        // 두 개의 상품 생성
        for (int i = 1; i <= 2; i++) {
            CreateProductRequestDto request = new CreateProductRequestDto();
            request.setName("Product" + i);
            request.setPrice(500 * i);
            request.setStock(10 * i);
            request.setCategoryId(testCategory.getId());
            productService.createProduct(request);
        }

        List<ProductResponseDto> products = productService.getProductCategory(testCategory.getId());

        // 카테고리별 상품 조회 확인
        assertThat(products).hasSize(2);
        assertThat(products.get(0).getName()).isEqualTo("Product1");
        assertThat(products.get(1).getName()).isEqualTo("Product2");
    }

    @Test
    void testUpdateProduct() {
        // 상품 생성
        CreateProductRequestDto createRequest = new CreateProductRequestDto();
        createRequest.setName("Old Product");
        createRequest.setPrice(500);
        createRequest.setStock(20);
        createRequest.setCategoryId(testCategory.getId());
        productService.createProduct(createRequest);

        var savedProduct = productRepository.findByProductCategory(testCategory).get(0);

        // 상품 수정
        UpdateProductRequestDto updateRequest = new UpdateProductRequestDto();
        updateRequest.setId(savedProduct.getId());
        updateRequest.setName("Updated Product");
        updateRequest.setPrice(1000);
        updateRequest.setStock(50);
        updateRequest.setCategoryId(testCategory.getId());
        productService.updateProduct(updateRequest);

        // 수정된 상품 확인
        var updatedProduct = productRepository.findById(savedProduct.getId()).orElseThrow();
        assertThat(updatedProduct.getName()).isEqualTo("Updated Product");
        assertThat(updatedProduct.getPrice()).isEqualTo(1000);
        assertThat(updatedProduct.getStock()).isEqualTo(50);
    }

    @Test
    void testDeleteProduct() {
        // 상품 생성
        CreateProductRequestDto request = new CreateProductRequestDto();
        request.setName("Product to Delete");
        request.setPrice(100);
        request.setStock(10);
        request.setCategoryId(testCategory.getId());
        productService.createProduct(request);

        var savedProduct = productRepository.findByProductCategory(testCategory).get(0);
        productService.deleteProduct(savedProduct.getId());

        // 삭제된 상품 확인
        var deletedProduct = productRepository.findById(savedProduct.getId()).orElseThrow();
        assertThat(deletedProduct.isDeleted()).isTrue();
    }
    **/
}