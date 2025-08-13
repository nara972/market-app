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
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final RedissonClient redissonClient;

    @Transactional
    public Long createOrder(CreateOrderRequestDto request, String loginId) {
        Users user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Order order = request.toOrderWithProducts(user);

        Map<Long, Integer> productIdToCount = request.getOrderProducts().stream()
                .collect(Collectors.toMap(OrderProductRequestDto::getProductId, OrderProductRequestDto::getCount));

        List<RLock> locks = new ArrayList<>();

        try {
            for (Long productId : productIdToCount.keySet()) {
                RLock lock = redissonClient.getLock("lock:product:" + productId);
                if (lock.tryLock(5, 10, TimeUnit.SECONDS)) {
                    locks.add(lock);
                } else {
                    throw new IllegalStateException("Lock failed: " + productId);
                }
            }

            List<Product> products = productRepository.findAllById(productIdToCount.keySet());

            for (Product product : products) {
                int count = productIdToCount.get(product.getId());
                if (product.getStock() < count) {
                    throw new IllegalArgumentException("Insufficient stock for product: " + product.getId());
                }
                product.setStock(product.getStock() - count);
            }

            productRepository.saveAll(products);
            orderRepository.save(order);

        } catch (InterruptedException e) {
            throw new IllegalStateException("Lock interrupted", e);
        } finally {
            for (RLock lock : locks) {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }

        return order.getId();
    }


    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        order.cancelOrder();

        orderRepository.save(order);
    }

     /**
      * 비관적 락을 이용한 동시성 이슈 해결
     @Transactional
     public Long createOrder(CreateOrderRequestDto request,String loginId){

     Users user = userRepository.findByLoginId(loginId)
     .orElseThrow(()->new IllegalArgumentException("User not found"));

     Order order = orderRepository.save(request.toOrder(user));

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
     */

}
