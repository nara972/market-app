package com.group.marketapp.order.dto.request;

import com.group.marketapp.order.domain.Order;
import com.group.marketapp.order.domain.OrderProduct;
import com.group.marketapp.order.domain.OrderStatus;
import com.group.marketapp.product.domain.Product;
import com.group.marketapp.user.domain.Users;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class CreateOrderRequestDto {

    private String receiverName;

    private String receiverAddress;

    private List<OrderProductRequestDto> orderProducts;

    /**
    public Order toOrder(String loginId){
        return Order.builder()
                .receiverName(receiverName)
                .receiverAddress(receiverAddress)
                .user(Users.builder().loginId(loginId).build())
                .build();
    }**/
    public Order toOrder(Users user){
        return Order.builder()
                .receiverName(receiverName)
                .receiverAddress(receiverAddress)
                .user(user) // 이미 영속된 Users 객체를 설정
                .orderStatus(OrderStatus.PENDING)
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

    @Builder
    public CreateOrderRequestDto(String receiverName, String receiverAddress, List<OrderProductRequestDto> orderProducts) {
        this.receiverName = receiverName;
        this.receiverAddress = receiverAddress;
        this.orderProducts = orderProducts;
    }

}
