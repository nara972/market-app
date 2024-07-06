package com.group.marketapp.order.service;

import com.group.marketapp.order.domain.Order;
import com.group.marketapp.order.domain.OrderProduct;
import com.group.marketapp.order.dto.request.CreateOrderRequestDto;
import com.group.marketapp.order.dto.request.OrderProductRequestDto;
import com.group.marketapp.order.repository.OrderProductRepository;
import com.group.marketapp.order.repository.OrderRepository;
import com.group.marketapp.product.repository.ProductRepository;
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


    @Transactional
    public Long createOrder(CreateOrderRequestDto request,long userId){

        Order order = request.toOrder(userId);
        order = orderRepository.save(order);

        System.out.println("order.getId() : " + order.getId());

        List<OrderProduct> orderProducts = request.toOrderProducts();

        for(OrderProduct orderProduct: orderProducts){
            orderProduct.setOrder(order);
        }

        orderProductRepository.saveAll(orderProducts);

        return order.getId();

    }
}
