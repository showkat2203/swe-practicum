package com.baylor.practicum_new.dto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryDTO {
    private Long categoryId;
    private String name;
    private String description;
    private List<ProductDTO> products;
}
