package com.project.Ecommerce.Controller;

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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/order_details")
@RequiredArgsConstructor

public class OrderDetailsController {

    private final OrderDetailService orderDetailService;

    //lay danh sach orderdetail theo order
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getOrderDetails(@Valid @PathVariable("orderId") long orderId ) {
        List<OrderDetail> orderDetails = orderDetailService.findByOrderId(orderId);
        List<OrderDetailResponses> orderDetailResponses = orderDetails
                .stream()
                .map(OrderDetailResponses::fromOrderDetail)
                .toList();
        return ResponseEntity.ok(orderDetailResponses);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getOderDetailsByID(@Valid @PathVariable("id") long orderId) {
        try {
            OrderDetail orderDetail = orderDetailService.getOrderDetail(orderId);
            return ResponseEntity.ok().body(OrderDetailResponses.fromOrderDetail(orderDetail));
        } catch (DataNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found with id: " + orderId);
        }
    }

    @PostMapping("")
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderDetailDTOs orderDetailDTO){

        try {
            OrderDetail newOrderDetail = orderDetailService.createOrderDetail(orderDetailDTO);
            return ResponseEntity.ok().body(OrderDetailResponses.fromOrderDetail(newOrderDetail));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder( @Valid @RequestBody OrderDetailDTOs orderDetailDTO,@PathVariable("id") long id){
        try {
            OrderDetail orderDetail = orderDetailService.updateOrderDetail(id, orderDetailDTO);
            return ResponseEntity.ok().body(orderDetail);
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public   ResponseEntity<?> deleteOrder(@PathVariable("id") long  id){
        orderDetailService.deleteById(id);
        return ResponseEntity.ok()
                .body("deleted successfully");

    }
}
