package com.group.marketapp.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "products")
@Getter
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductSearchDocument {

    @Id
    private Long id;

    private String name;

    private String categoryName;

    private Long categoryId;

    private int price;

    @JsonCreator
    public ProductSearchDocument(
            @JsonProperty("id") Long id,
            @JsonProperty("name") String name,
            @JsonProperty("categoryName") String categoryName,
            @JsonProperty("categoryId") Long categoryId,
            @JsonProperty("price") int price) {
        this.id = id;
        this.name = name;
        this.categoryName = categoryName;
        this.categoryId = categoryId;
        this.price = price;
    }

}
