package com.project.Ecommerce.Respones;

import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@SuperBuilder
@Getter
@Setter
public class ProductListResponses {
    List<ProductResponses> productResponses;
    int total;
}
