package com.group.marketapp;

import com.group.marketapp.order.dto.request.CreateOrderRequestDto;
import com.group.marketapp.order.dto.request.OrderProductRequestDto;
import com.group.marketapp.order.repository.OrderRepository;
import com.group.marketapp.order.service.OrderService;
import com.group.marketapp.product.domain.Product;
import com.group.marketapp.product.domain.ProductCategory;
import com.group.marketapp.product.repository.ProductCategoryRepository;
import com.group.marketapp.product.repository.ProductRepository;
import com.group.marketapp.user.domain.Users;
import com.group.marketapp.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;


import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
public class OrderConcurrencyTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductCategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    private Product testProduct;
    private Users testUser;

    @BeforeEach
    void setup() {
        // 사용자 생성
        testUser = Users.builder()
                .loginId("testUser")
                .password("password123")
                .username("Test User")
                .address("123 Test Street")
                .role("ROLE_USER")
                .build();
        userRepository.save(testUser);


        // 카테고리 및 상품 생성
        ProductCategory category = ProductCategory.builder()
                .name("Electronics")
                .build();
        categoryRepository.save(category);

        testProduct = Product.builder()
                .name("Smartphone")
                .price(1000)
                .stock(50) // 초기 재고
                .isDeleted(false)
                .productCategory(category)
                .build();
        productRepository.save(testProduct);
    }

    @Test
    void testConcurrentOrderCreation() throws InterruptedException {
        int threadCount = 10; // 동시 실행할 스레드 수
        int ordersPerThread = 5; // 각 스레드에서 생성할 주문 수
        int totalOrders = threadCount * ordersPerThread;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // 주문 요청 DTO 생성
        OrderProductRequestDto orderProductRequest = OrderProductRequestDto.builder()
                .productId(testProduct.getId())
                .count(1) // 주문당 1개씩 구매
                .build();

        CreateOrderRequestDto createOrderRequest = CreateOrderRequestDto.builder()
                .receiverName("Receiver")
                .receiverAddress("Test Address")
                .orderProducts(List.of(orderProductRequest))
                .build();

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    // 스레드별 SecurityContextHolder 설정
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(
                                    testUser, // Users 객체를 Principal로 설정
                                    null,
                                    List.of(new SimpleGrantedAuthority(testUser.getRole()))
                            )
                    );

                    for (int j = 0; j < ordersPerThread; j++) {
                        orderService.createOrder(createOrderRequest, testUser.getLoginId());
                        log.info("Order created by thread: {}", Thread.currentThread().getName());
                    }
                } catch (Exception e) {
                    log.error("Order creation failed: {}", e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드가 작업을 마칠 때까지 대기
        executorService.shutdown();

        // 최종 재고 확인
        Product updatedProduct = productRepository.findById(testProduct.getId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        log.info("Final stock: {}, Expected stock: {}", updatedProduct.getStock(), Math.max(50 - totalOrders, 0));

        // 예상 재고 감소 확인
        assertThat(updatedProduct.getStock()).isEqualTo(Math.max(50 - totalOrders, 0));

        // 성공한 주문 개수 확인
        long successfulOrders = orderRepository.count();
        assertThat(successfulOrders).isEqualTo(Math.min(50, totalOrders)); // 재고가 허용하는 주문만 성공
    }
}