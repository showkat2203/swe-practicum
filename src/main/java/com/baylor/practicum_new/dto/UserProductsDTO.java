package com.baylor.practicum_new.dto;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserProductsDTO {
    private Long userId;
    private String userName;
    private List<ProductDTO> products;
}
