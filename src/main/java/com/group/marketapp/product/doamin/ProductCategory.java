package com.group.marketapp.product.doamin;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class ProductCategory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30,nullable = false)
    private String name;

    public ProductCategory() {}

}
