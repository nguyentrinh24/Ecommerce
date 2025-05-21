package com.project.Ecommerce.Controller;

import com.project.Ecommerce.DTOs.OrderDTOs;
import com.project.Ecommerce.Model.Order;
import com.project.Ecommerce.Repository.OrderRepository;
import com.project.Ecommerce.Respones.Order.OrderResponse;
import com.project.Ecommerce.Service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderByID(@PathVariable("id") long id) {
        try {
            Optional<Order> orders = orderRepository.findById(id);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/users/{user_id}")
    public ResponseEntity<?> getOrderByUserID(@PathVariable("user_id") Long id) {
        try {
            Optional<Order> orders = orderService.findByIdUser(id);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("")
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderDTOs order, BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<String> message = result.getFieldErrors().stream().map((err) -> err.getDefaultMessage())
                        .collect(Collectors.toList());

                return ResponseEntity.badRequest().body(message);
            }

            OrderResponse orderResponse = orderService.createOrder(order);
            return ResponseEntity.ok(orderResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@Valid @RequestBody OrderDTOs orderDTO, @PathVariable("id") Long id) {

        try {
            Order order = orderService.updateOrder(id, orderDTO);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable("id") long id) {
        // xóa mềm => cập nhật trường active = false
        orderService.deleteOrder(id);
        return ResponseEntity.ok().body("Order has been deleted");
    }

}
