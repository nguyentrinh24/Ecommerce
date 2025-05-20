package com.project.Ecommerce.DTOs;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CategoryDTOs {
    @NotEmpty(message = "Category cannot empty")
    private  String name;
}
