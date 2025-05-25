package com.project.Ecommerce.Controller;

import com.project.Ecommerce.Component.LocalizationUtil;
import com.project.Ecommerce.DTOs.OrderDetailDTOs;
import com.project.Ecommerce.Exceptions.DataNotFoundException;
import com.project.Ecommerce.Model.OrderDetail;
import com.project.Ecommerce.Respones.Order.OrderDetailResponses;
import com.project.Ecommerce.Service.OrderDetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import static com.project.Ecommerce.Util.MessagesKey.*;

@RestController
@RequestMapping("${api.prefix}/order_details")
@RequiredArgsConstructor
public class OrderDetailsController {

    private final OrderDetailService orderDetailService;
    private final LocalizationUtil localizationUtil;

    // ✅ Lấy danh sách order detail theo orderId
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getOrderDetails(@Valid @PathVariable("orderId") long orderId) {
        List<OrderDetail> orderDetails = orderDetailService.findByOrderId(orderId);
        List<OrderDetailResponses> response = orderDetails.stream()
                .map(OrderDetailResponses::fromOrderDetail)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // ✅ Lấy order detail theo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetailById(@Valid @PathVariable("id") long id) {
        try {
            OrderDetail orderDetail = orderDetailService.getOrderDetail(id);
            return ResponseEntity.ok(OrderDetailResponses.fromOrderDetail(orderDetail));
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(localizationUtil.getMessage(ORDER_DETAIL_NOT_FOUND, id));
        }
    }

    // ✅ Tạo order detail
    @PostMapping("")
    public ResponseEntity<?> createOrderDetail(@Valid @RequestBody OrderDetailDTOs orderDetailDTO) {
        try {
            OrderDetail created = orderDetailService.createOrderDetail(orderDetailDTO);
            return ResponseEntity.ok(OrderDetailResponses.fromOrderDetail(created));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(localizationUtil.getMessage(ORDER_DETAIL_CREATE_FAILED, e.getMessage()));
        }
    }

    // ✅ Cập nhật order detail
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrderDetail(@Valid @RequestBody OrderDetailDTOs orderDetailDTO,
                                               @PathVariable("id") long id) {
        try {
            OrderDetail updated = orderDetailService.updateOrderDetail(id, orderDetailDTO);
            return ResponseEntity.ok(OrderDetailResponses.fromOrderDetail(updated));
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(localizationUtil.getMessage(ORDER_DETAIL_UPDATE_FAILED, e.getMessage()));
        }
    }

    // ✅ Xóa order detail
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrderDetail(@PathVariable("id") long id) {
        try {
            orderDetailService.deleteById(id);
            return ResponseEntity.ok(
                    localizationUtil.getMessage(DELETE_ORDER_DETAIL_SUCCESSFULLY, id)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi xóa: " + e.getMessage());
        }
    }
}
