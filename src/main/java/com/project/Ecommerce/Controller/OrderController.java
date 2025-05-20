package com.project.Ecommerce.Controller;

import com.project.Ecommerce.DTOs.OrderDTOs;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/order")
public class OrderController {

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderByID(@PathVariable("id") int id)
    {
        return ResponseEntity.ok().body(String.format("get order by id=%d successfully",id));
    }
    @PostMapping("")
    public ResponseEntity<?> createOrder(@Valid  @RequestBody OrderDTOs order, BindingResult result)
    {
        try {
            if(result.hasErrors())
            {
                List<String> message = result.getFieldErrors().stream().map((err)->err.getDefaultMessage()).collect(Collectors.toList());

                return ResponseEntity.badRequest().body(message);
            }
            return ResponseEntity.ok().body("create order successfully");
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@Valid @RequestBody OrderDTOs order, @PathVariable("id") int id)
    {
        return ResponseEntity.ok().body(String.format("update order by id=%d successfully",id));
    }
    @DeleteMapping("/{id}")
    public  ResponseEntity<?> deleteOrder(@PathVariable("id") int id)
    {
        return ResponseEntity.ok().body(String.format("delete order by id=%d successfully",id));
    }


}
