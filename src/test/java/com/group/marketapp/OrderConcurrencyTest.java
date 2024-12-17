package com.group.marketapp;

import com.group.marketapp.dto.requestdto.CreateOrderRequestDto;
import com.group.marketapp.dto.requestdto.OrderProductRequestDto;
import com.group.marketapp.repository.OrderRepository;
import com.group.marketapp.service.OrderService;
import com.group.marketapp.domain.Product;
import com.group.marketapp.domain.ProductCategory;
import com.group.marketapp.repository.ProductCategoryRepository;
import com.group.marketapp.repository.ProductRepository;
import com.group.marketapp.domain.Users;
import com.group.marketapp.repository.UserRepository;
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
                .stock(50) // 초기 재고 설정
                .isDeleted(false)
                .productCategory(category)
                .build();
        productRepository.save(testProduct);
    }

    @Test
    void compareLockPerformance() throws InterruptedException {
        int threadCount = 10; // 동시 실행할 스레드 수
        int ordersPerThread = 5; // 각 스레드에서 생성할 주문 수
        int totalOrders = threadCount * ordersPerThread;

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

        // 비관적 락 성능 테스트
        long pessimisticDuration = measureExecutionTime(() -> {
            try {
                executeConcurrentOrders(() -> orderService.createOrder(createOrderRequest, testUser.getLoginId()), threadCount, ordersPerThread);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        // 분산 락 성능 테스트
        long distributedDuration = measureExecutionTime(() -> {
            try {
                executeConcurrentOrders(() -> orderService.createOrderWithRedisson(createOrderRequest, testUser.getLoginId()), threadCount, ordersPerThread);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        // 결과 출력
        log.info("비관적 락 실행 시간: {}ms", pessimisticDuration);
        log.info("분산 락 실행 시간: {}ms", distributedDuration);

        // 성능 비교
        assertThat(pessimisticDuration).isGreaterThan(0);
        assertThat(distributedDuration).isGreaterThan(0);
    }

    private void executeConcurrentOrders(Runnable task, int threadCount, int ordersPerThread) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    // 스레드별 SecurityContextHolder 설정
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(
                                    testUser,
                                    null,
                                    List.of(new SimpleGrantedAuthority(testUser.getRole()))
                            )
                    );

                    for (int j = 0; j < ordersPerThread; j++) {
                        task.run();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();
    }

    private long measureExecutionTime(Runnable task) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        task.run();
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }

}