package com.group.marketapp.order.dto.request;

import com.group.marketapp.order.domain.Order;
import com.group.marketapp.order.domain.OrderProduct;
import com.group.marketapp.product.domain.Product;
import com.group.marketapp.user.domain.User;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CreateOrderRequestDto {

    private String receiverName;

    private String receiverAddress;

    private List<OrderProductRequestDto> orderProducts;

    public Order toOrder(long userid){
        return Order.builder()
                .receiverName(receiverName)
                .receiverAddress(receiverAddress)
                .user(User.builder().id(userid).build())
                .build();
    }


    public List<OrderProduct> toOrderProducts(){
        return orderProducts.stream()
                .map(dto -> OrderProduct.builder()
                        .product(Product.builder().id(dto.getProductId()).build())
                        .count(dto.getCount())
                        .build())
                .collect(Collectors.toList());
    }

}
