package com.group.marketapp;

import com.group.marketapp.domain.Order;
import com.group.marketapp.domain.OrderStatus;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private UserRepository userRepository;

    private Users testUser;
    private Product testProduct;

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
        productCategoryRepository.save(category);

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
    void testCreateOrder() {
        // 주문 요청 DTO 생성
        OrderProductRequestDto orderProductRequest = OrderProductRequestDto.builder()
                .productId(testProduct.getId())
                .count(5) // 5개 구매
                .build();

        CreateOrderRequestDto createOrderRequest = CreateOrderRequestDto.builder()
                .receiverName("Receiver")
                .receiverAddress("Test Address")
                .orderProducts(List.of(orderProductRequest))
                .build();

        // 주문 생성
        Long orderId = orderService.createOrder(createOrderRequest, testUser.getLoginId());

        // 생성된 주문 검증
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        assertThat(order.getUser()).isEqualTo(testUser); // 주문 사용자 확인
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PENDING); // 주문 상태 확인

        // 재고 감소 검증
        Product updatedProduct = productRepository.findById(testProduct.getId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        assertThat(updatedProduct.getStock()).isEqualTo(45); // 기존 재고 50에서 5 감소
    }

    @Test
    void testCancelOrder() {
        // Given: 주문 생성에 필요한 데이터 설정
        int initialStock = 50;
        int purchaseCount = 5;

        // 주문 상품 생성
        OrderProductRequestDto orderProductRequest = OrderProductRequestDto.builder()
                .productId(testProduct.getId())
                .count(purchaseCount) // 구매 개수 설정
                .build();

        // 주문 요청 DTO 생성
        CreateOrderRequestDto createOrderRequest = CreateOrderRequestDto.builder()
                .receiverName("Receiver")
                .receiverAddress("Test Address")
                .orderProducts(List.of(orderProductRequest))
                .build();

        // 상품 초기 재고 설정
        testProduct.setStock(initialStock);
        productRepository.save(testProduct);

        // When: 주문 생성 및 취소 실행
        Long orderId = orderService.createOrder(createOrderRequest, testUser.getLoginId());
        orderService.cancelOrder(orderId);

        // Then: 주문 상태 확인
        Order canceledOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        assertThat(canceledOrder.getOrderStatus()).isEqualTo(OrderStatus.CANCELED); // 주문 취소 상태 확인

        // Then: 재고 복구 확인
        Product updatedProduct = productRepository.findById(testProduct.getId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        assertThat(updatedProduct.getStock()).isEqualTo(initialStock); // 재고 복구 확인
    }
}