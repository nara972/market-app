package com.group.marketapp.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "`order`")
@Getter
@Setter
@NoArgsConstructor
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 11, nullable = false)
    private String receiverName;

    @Column(length = 100, nullable = false)
    private String receiverAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Enumerated(value = EnumType.STRING)
    private OrderStatus orderStatus;


    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderProduct> orderProducts = new ArrayList<>();

    private LocalDateTime deliveredAt;

    @Builder
    public Order(long id, String receiverName, String receiverAddress, Users user, OrderStatus orderStatus, LocalDateTime deliveredAt) {
        this.id = id;
        this.receiverName = receiverName;
        this.receiverAddress = receiverAddress;
        this.user = user;
        this.orderStatus = orderStatus;
        this.deliveredAt = deliveredAt;
    }

    public void setOrderProducts(List<OrderProduct> orderProducts) {
        this.orderProducts = orderProducts;
        for (OrderProduct orderProduct : orderProducts) {
            orderProduct.setOrder(this);
        }
    }

    public void cancelOrder(){
        this.orderStatus = OrderStatus.CANCELED;
    }

}
