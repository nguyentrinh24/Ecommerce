package com.project.Ecommerce.Controller;

import com.project.Ecommerce.Component.LocalizationUtil;
import com.project.Ecommerce.DTOs.OrderDTOs;
import com.project.Ecommerce.Model.Order;
import com.project.Ecommerce.Repository.OrderRepository;
import com.project.Ecommerce.Respones.Order.OrderResponse;
import com.project.Ecommerce.Service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.project.Ecommerce.Util.MessagesKey.*;

@RestController
@RequestMapping("${api.prefix}/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final LocalizationUtil localizationUtil;

    //  Lấy đơn theo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderByID(@PathVariable("id") long id) {
        try {
            Optional<Order> orders = orderRepository.findById(id);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    //  Lấy đơn theo User ID
    @GetMapping("/users/{user_id}")
    public ResponseEntity<?> getOrderByUserID(@PathVariable("user_id") Long id) {
        try {
            Optional<Order> orders = orderService.findByIdUser(id);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    //  Tạo đơn hàng
    @PostMapping("")
    public ResponseEntity<?> createOrder(
            @Valid @RequestBody OrderDTOs order,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            String message = result.getFieldErrors().stream()
                    .map(err -> err.getDefaultMessage())
                    .collect(Collectors.joining("; "));
            return ResponseEntity.badRequest().body(message);
        }

        try {
            OrderResponse orderResponse = orderService.createOrder(order);
            return ResponseEntity.ok(orderResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi tạo đơn hàng: " + e.getMessage());
        }
    }

    //  Cập nhật đơn hàng
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(
            @Valid @RequestBody OrderDTOs orderDTO,
            @PathVariable("id") Long id
    ) {
        try {
            Order order = orderService.updateOrder(id, orderDTO);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi cập nhật đơn hàng: " + e.getMessage());
        }
    }

    // Xóa đơn hàng (mềm)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable("id") long id) {
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.ok(
                    localizationUtil.getMessage(DELETE_ORDER_SUCCESSFULLY, id)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi xóa đơn hàng: " + e.getMessage());
        }
    }
}
