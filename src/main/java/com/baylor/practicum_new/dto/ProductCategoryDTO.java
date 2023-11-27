package com.baylor.practicum_new.dto;

import lombok.*;

import java.util.Set;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductCategoryDTO {
    private Long productId;
    private String productName;
    private String description;
    private Set<Long> categoryIds;
}
