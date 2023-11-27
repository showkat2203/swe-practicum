package com.baylor.practicum_new.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginRequestDTO {
    private String email;
    private String password;
}
