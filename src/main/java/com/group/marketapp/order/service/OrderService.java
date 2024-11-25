package com.group.marketapp.order.service;

import com.group.marketapp.order.domain.Order;
import com.group.marketapp.order.domain.OrderProduct;
import com.group.marketapp.order.dto.request.CreateOrderRequestDto;
import com.group.marketapp.order.dto.request.OrderProductRequestDto;
import com.group.marketapp.order.repository.OrderProductRepository;
import com.group.marketapp.order.repository.OrderRepository;
import com.group.marketapp.product.domain.Product;
import com.group.marketapp.product.repository.ProductRepository;
import com.group.marketapp.user.domain.Users;
import com.group.marketapp.user.repository.UserRepository;
import com.group.marketapp.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final ProductRepository productRepository;
    private final UserService userService;
    private final UserRepository userRepository;


    @Transactional
    public Long createOrder(CreateOrderRequestDto request,String loginId){


        Users user = userRepository.findByLoginId(loginId)
                .orElseThrow(()->new IllegalArgumentException("User not found"));
        log.info("Creating order for user: {}", user.getLoginId());


        Order order = request.toOrder(user);
        order = orderRepository.save(order);

        List<OrderProduct> orderProducts = request.toOrderProducts();

        for(OrderProductRequestDto dto : request.getOrderProducts()){

            Product product = productRepository.findByIdWithLock(dto.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));
            log.info("Processing product: {}, current stock: {}", product.getName(), product.getStock());
            if(product.getStock() < dto.getCount()){
                throw new IllegalArgumentException("Insufficient stock");
            }

            product.setStock(product.getStock() - dto.getCount());
            productRepository.save(product);
            log.info("Updated stock for product: {}, new stock: {}", product.getName(), product.getStock());
        }

        for(OrderProduct orderProduct: orderProducts){
            orderProduct.setOrder(order);

        }
        orderProductRepository.saveAll(orderProducts);
        return order.getId();

    }

    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        order.cancelOrder();

        orderRepository.save(order);
    }

}
