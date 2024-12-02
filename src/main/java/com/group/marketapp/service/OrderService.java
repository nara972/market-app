package com.group.marketapp.service;

import com.group.marketapp.domain.Order;
import com.group.marketapp.domain.OrderProduct;
import com.group.marketapp.dto.requestdto.CreateOrderRequestDto;
import com.group.marketapp.dto.requestdto.OrderProductRequestDto;
import com.group.marketapp.repository.OrderProductRepository;
import com.group.marketapp.repository.OrderRepository;
import com.group.marketapp.domain.Product;
import com.group.marketapp.repository.ProductRepository;
import com.group.marketapp.domain.Users;
import com.group.marketapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;


    @Transactional
    public Long createOrder(CreateOrderRequestDto request,String loginId){

        Users user = userRepository.findByLoginId(loginId)
                .orElseThrow(()->new IllegalArgumentException("User not found"));

        Order order = request.toOrder(user);
        order = orderRepository.save(order);

        List<OrderProduct> orderProducts = request.toOrderProducts();

        for(OrderProductRequestDto dto : request.getOrderProducts()){

            Product product = productRepository.findByIdWithLock(dto.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));
            if(product.getStock() < dto.getCount()){
                throw new IllegalArgumentException("Insufficient stock");
            }

            product.setStock(product.getStock() - dto.getCount());
            productRepository.save(product);
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
