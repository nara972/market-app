package com.group.marketapp.dto.requestdto;

import com.group.marketapp.domain.Order;
import com.group.marketapp.domain.OrderProduct;
import com.group.marketapp.domain.OrderStatus;
import com.group.marketapp.domain.Product;
import com.group.marketapp.domain.Users;
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

    public Order toOrderWithProducts(Users user) {
        Order order = Order.builder()
                .receiverName(receiverName)
                .receiverAddress(receiverAddress)
                .user(user)
                .orderStatus(OrderStatus.PENDING)
                .build();


            List<OrderProduct> products = orderProducts.stream()
                    .map(dto -> {
                        OrderProduct orderProduct = OrderProduct.builder()
                                .product(Product.builder().id(dto.getProductId()).build())
                                .count(dto.getCount())
                                .build();
                        orderProduct.setOrder(order);
                        return orderProduct;
                    })
                    .collect(Collectors.toList());

            order.setOrderProducts(products);


        return order;
    }

    @Builder
    public CreateOrderRequestDto(String receiverName, String receiverAddress, List<OrderProductRequestDto> orderProducts) {
        this.receiverName = receiverName;
        this.receiverAddress = receiverAddress;
        this.orderProducts = orderProducts;
    }

}
