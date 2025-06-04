package com.project.Ecommerce.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDTOs
{

    @JsonProperty( "id")
    private  Long id;

    @JsonProperty( "email")
    private String email;

    @JsonProperty("fullname")
    @NotEmpty(message = "Full name not empty ")
    private String fullName;

    @JsonProperty("phonenumber")
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    private String address;

    @NotBlank(message = "Password cannot be blank")
    private String password;

    @JsonProperty("retype_password")
    private String retypePassword;

    @JsonProperty("date_birth")
    private Date dateOfBirth;

    @JsonProperty("fb_account_id")
    private Integer  facebookAccountId;

    @JsonProperty("gg_account_id")
    private Integer  googleAccountId;


    @JsonProperty("role_id")
    @NotNull(message = "Role ID is required")
    private Long roleId;
}
