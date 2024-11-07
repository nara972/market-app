package com.group.marketapp;

import com.group.marketapp.order.dto.request.CreateOrderRequestDto;
import com.group.marketapp.order.dto.request.OrderProductRequestDto;
import com.group.marketapp.order.service.OrderService;
import com.group.marketapp.product.domain.Product;
import com.group.marketapp.product.domain.ProductCategory;
import com.group.marketapp.product.dto.request.CreateProductRequestDto;
import com.group.marketapp.product.repository.ProductCategoryRepository;
import com.group.marketapp.product.repository.ProductRepository;
import com.group.marketapp.product.service.ProductService;
import com.group.marketapp.user.domain.Users;
import com.group.marketapp.user.dto.request.CreateUserRequestDto;
import com.group.marketapp.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class OrderServicePessimisticLockTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @Transactional
    public void testPessimisticLockOnOrder() throws InterruptedException {
        // Given: 상품 생성 및 초기 재고 설정
        Product product = new Product("Test Product", 10, false); // 상품 초기 재고 10
        product = productRepository.save(product);

        // 첫 번째 주문 요청 (5개 주문)
        OrderProductRequestDto orderProductRequest1 = new OrderProductRequestDto(product.getId(), 5);
        CreateOrderRequestDto request1 = new CreateOrderRequestDto(List.of(orderProductRequest1));

        // 두 번째 주문 요청 (6개 주문 - 재고 부족 예상)
        OrderProductRequestDto orderProductRequest2 = new OrderProductRequestDto(product.getId(), 6);
        CreateOrderRequestDto request2 = new CreateOrderRequestDto(List.of(orderProductRequest2));

        // 두 개의 스레드 풀 생성
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // 첫 번째 스레드에서 주문 생성 (정상 처리 예상)
        executorService.submit(() -> {
            orderService.createOrder(request1, "user1");
        });

        // 두 번째 스레드에서 동시에 주문 생성 시도 (재고 부족으로 예외 발생 예상)
        executorService.submit(() -> {
            assertThrows(IllegalArgumentException.class, () -> {
                orderService.createOrder(request2, "user2");
            });
        });

        // 스레드가 모두 완료될 때까지 대기
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        // Then: 최종 재고가 정확하게 차감되었는지 확인
        Product updatedProduct = productRepository.findById(product.getId()).orElseThrow();
        assertEquals(5, updatedProduct.getStock(), "재고는 5개 남아야 합니다.");
    }
}