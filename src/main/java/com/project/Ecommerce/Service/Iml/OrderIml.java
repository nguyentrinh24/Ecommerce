package com.project.Ecommerce.Service.Iml;

import com.project.Ecommerce.DTOs.OrderDTOs;
import com.project.Ecommerce.Exceptions.DataNotFoundException;
import com.project.Ecommerce.Model.Order;
import com.project.Ecommerce.Respones.Order.OrderResponse;

import java.util.List;
import java.util.Optional;

public interface OrderIml {
    OrderResponse createOrder(OrderDTOs order);
    List<OrderResponse> getAllOrders();
    com.project.Ecommerce.Model.Order updateOrder(Long id, OrderDTOs order) throws DataNotFoundException;
    String deleteOrder(Long id);
    Optional<Order> findByIdUser(Long id);

}
