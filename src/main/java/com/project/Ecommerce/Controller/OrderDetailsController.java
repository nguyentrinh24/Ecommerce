package com.project.Ecommerce.Controller;


import com.project.Ecommerce.DTOs.OrderDetailDTOs;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/order_details")
public class OrderDetailsController {

    // lấy danh sách của order_detalis trong order
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getOrderDetails(@Valid @PathVariable("orderId") long orderId ) {
        return ResponseEntity.ok().body("get order details with order id " + orderId);
    }
    // lấy danh sáchh của order_Details
    @GetMapping("/{id}")
    public ResponseEntity<?> getOderDetailsByID(@Valid @PathVariable("id") long orderId ) {
        return ResponseEntity.ok().body("get order details with order id " + orderId);
    }

    @PostMapping("")
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderDetailDTOs orderDetailDTOs){
        return  ResponseEntity.status(HttpStatus.CREATED).body("createOrder");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder( @Valid @RequestBody OrderDetailDTOs orderDetailDTOs,@PathVariable("id") long id){
        return ResponseEntity.ok().body("updateOrder with id " + id +" new order details " + orderDetailDTOs);
    }

    @DeleteMapping("/{id}")
    public   ResponseEntity<?> deleteOrder(@PathVariable("id") long  id){
        return ResponseEntity.ok().body("deleteOrder with id " + id);
    }
}
