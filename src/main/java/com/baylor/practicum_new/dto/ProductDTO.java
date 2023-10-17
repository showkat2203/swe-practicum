package com.baylor.practicum_new.dto;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Long productId;
    private String productName;
    private String description;
}
