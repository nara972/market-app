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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

        //check product stock
        for(OrderProductRequestDto dto : request.getOrderProducts()){
            Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            if(product.getStock()< dto.getCount()){
                throw new IllegalArgumentException("Product has not enough stock");
            }
        }

        Order order = request.toOrder(user);
        order = orderRepository.save(order);

        System.out.println("order.getId() : " + order.getId());

        List<OrderProduct> orderProducts = request.toOrderProducts();

        for(OrderProduct orderProduct: orderProducts){
            orderProduct.setOrder(order);

            Product product = productRepository.findById(orderProduct.getProduct().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            product.setStock(product.getStock()-orderProduct.getCount());
            productRepository.save(product);
        }

        orderProductRepository.saveAll(orderProducts);

        return order.getId();

    }
}
