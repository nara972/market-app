package com.group.marketapp.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class ProductCategory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column()
    private Long id;

    @Column(length = 30,nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private ProductCategory parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductCategory> subCategories = new ArrayList<>();

    @OneToMany(mappedBy = "productCategory")
    private List<Product> product =new ArrayList<>();

    public ProductCategory() {}

    @Builder
    public ProductCategory(Long id, String name) {
        this.id = id;
        this.name = name;
    }

}
