package com.baylor.practicum_new.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@JsonPropertyOrder({"userId", "name", "email"})
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Data
public class UserResponseDTO {
    private Long userId;
    private String name;
    private String email;
}