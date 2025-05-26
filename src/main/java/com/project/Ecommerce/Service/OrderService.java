package com.project.Ecommerce.Service;

import com.project.Ecommerce.DTOs.OrderDTOs;
import com.project.Ecommerce.Exceptions.DataNotFoundException;
import com.project.Ecommerce.Model.OrderStatus;
import com.project.Ecommerce.Model.User;
import com.project.Ecommerce.Repository.*;
import com.project.Ecommerce.Respones.Order.OrderResponse;
import com.project.Ecommerce.Service.Iml.OrderIml;
import com.project.Ecommerce.Model.Order;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.*;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class OrderService implements OrderIml {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelmapper;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderDTOs orderDTOs) {
        //check user_id
        try {
            User exits_user = userRepository.findById(orderDTOs.getUser_id())
                                .orElseThrow(()->new DataNotFoundException("User not found"));

            // convert -> dùng modelmapper
           modelmapper.typeMap(OrderDTOs.class,Order.class)
                   .addMappings(mapper -> {mapper.skip(Order ::setId);});

            Order order = new Order();
            modelmapper.map(orderDTOs, order);
            order.setUser(exits_user);
            order.setOrderDate(LocalDateTime.now());// lay thoi gian cu the
            order.setStatus(OrderStatus.PENDING);
            // check shipping date == date now
            LocalDateTime shippingDate = order.getShippingDate(); // từ DTO map sang
            if (shippingDate != null && !shippingDate.toLocalDate().isEqual(LocalDateTime.now().toLocalDate())) {
                throw new DataNotFoundException("Shipping date must be today");
            }
            order.setShippingDate(shippingDate);
            order.setActive(true);
            orderRepository.save(order);
            return modelmapper.map(order, (Type) OrderResponse.class);
        }
        catch (DataNotFoundException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    @Transactional
    public List<OrderResponse> getAllOrders() {

        return List.of();
    }

    @Override
    @Transactional
    public Order updateOrder(Long id, OrderDTOs orderDTO) throws DataNotFoundException {
        Order order = orderRepository.findById(id).orElseThrow(() ->
                new DataNotFoundException("Cannot find order with id: " + id));
        User existingUser = userRepository.findById(
                orderDTO.getUser_id()).orElseThrow(() ->
                new DataNotFoundException("Cannot find user with id: " + id));
        // Tạo một luồng bảng ánh xạ riêng để kiểm soát việc ánh xạ
        modelmapper.typeMap(OrderDTOs.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        // Cập nhật các trường của đơn hàng từ orderDTO
        modelmapper.map(orderDTO, order);
        order.setUser(existingUser);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public String  deleteOrder(Long id) {
        try {
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find order with id: " + id));

            orderRepository.delete(order);
            return "Xóa đơn hàng thành công!";
        } catch (DataNotFoundException e) {
            return "Lỗi: " + e.getMessage();
        } catch (Exception e) {
            // Thường sẽ bắt lỗi ràng buộc FK ở đây
            return "Xóa thất bại: Đơn hàng đang được tham chiếu bởi dữ liệu khác.";
        }
    }

    @Override
    @Transactional
    public Optional<Order> findByIdUser(Long id) {
        return orderRepository.findById(id);
    }





}
