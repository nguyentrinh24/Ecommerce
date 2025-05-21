package com.project.Ecommerce.Respones.Order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class OrderListResponses {
    private List<OrderResponse> orders;
    private int totalPages;
}
