package com.project.Ecommerce.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserLoginDTOs {
    @JsonProperty("phone_number")

    private String phoneNumber;

    @NotBlank(message = "Password cannot be blank")
    @JsonProperty("password")
    private String password;

}
