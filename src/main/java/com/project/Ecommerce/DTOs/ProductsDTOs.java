package com.project.Ecommerce.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductsDTOs {

    @NotEmpty(message = "name cannot empty")
    private String name;

    @Min(value = 0,message = "price > 0")
    private Float price;
    private String thumbnail;
    private String description;

    @JsonProperty("category_id")
    private String categoryID;

    private List<MultipartFile> file;


}
