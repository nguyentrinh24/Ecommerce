package com.project.Ecommerce.Respones.Category;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class UpdateCategoryResponses {

    @JsonProperty("message")
    private String message;
}
