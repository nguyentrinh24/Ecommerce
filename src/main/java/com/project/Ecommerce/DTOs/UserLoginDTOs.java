package com.project.Ecommerce.DTOs;
import com.fasterxml.jackson.annotation.JsonProperty;;
import jakarta.validation.constraints.NotBlank;
import lombok.*;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserLoginDTOs {
    @JsonProperty("phone_number")
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;


    @NotBlank(message = "Password cannot be blank")
    private String password;


}
