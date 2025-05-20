package com.project.Ecommerce.Service.Iml;

import com.project.Ecommerce.DTOs.OrderDTOs;
import com.project.Ecommerce.Respones.OrderResponse;
import jakarta.persistence.criteria.Order;

import java.util.List;

public interface OrderIml {
    Order createOrder(OrderDTOs order);
    List<OrderResponse> getAllOrders();
    Order updateOrder(Long id, OrderDTOs order);
    Order deleteOrder(Long id);
}
