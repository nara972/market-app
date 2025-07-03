package com.group.marketapp.dto.responsedto;

import com.group.marketapp.domain.ProductCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryResponseDto {

    private Long id;
    private String name;
    private Long parentId;

    public static CategoryResponseDto of(ProductCategory category) {
        return new CategoryResponseDto(
                category.getId(),
                category.getName(),
                category.getParent() != null ? category.getParent().getId() : null
        );
    }

}
