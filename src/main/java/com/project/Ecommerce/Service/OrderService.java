package com.project.Ecommerce.Service;

import com.project.Ecommerce.DTOs.OrderDTOs;
import com.project.Ecommerce.Exceptions.DataNotFoundException;
import com.project.Ecommerce.Model.User;
import com.project.Ecommerce.Repository.*;
import com.project.Ecommerce.Respones.OrderResponse;
import com.project.Ecommerce.Service.Iml.OrderIml;
import jakarta.persistence.criteria.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class OrderService implements OrderIml {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;


    @Override
    public Order createOrder(OrderDTOs order) {
        //check user_id
        try {
            User exits_user = userRepository.findById(order.getUser_id()).orElseThrow(()->new DataNotFoundException("User not found"));
        } catch (DataNotFoundException e) {
            throw new RuntimeException(e);
        }

        // convert -> dùng modelampper


        return null;
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return List.of();
    }

    @Override
    public Order updateOrder(Long id, OrderDTOs order) {
        return null;
    }

    @Override
    public Order deleteOrder(Long id) {
        return null;
    }
}
