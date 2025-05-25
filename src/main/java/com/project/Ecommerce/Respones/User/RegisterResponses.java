package com.project.Ecommerce.Respones.User;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.Ecommerce.Model.User;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class RegisterResponses {
    private String message;

    @JsonProperty("user")
    private User user;
}
