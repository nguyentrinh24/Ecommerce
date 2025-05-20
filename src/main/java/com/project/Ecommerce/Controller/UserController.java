package com.project.Ecommerce.Controller;

import com.project.Ecommerce.DTOs.UserDTOs;
import com.project.Ecommerce.DTOs.UserLoginDTOs;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/user")
public class UserController {

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDTOs user, BindingResult bindingResult) {
        try {
            if(bindingResult.hasErrors()) {
                List<String> messages = bindingResult.getFieldErrors().stream().map((err)->err.getDefaultMessage()).collect(Collectors.toList());
                return ResponseEntity.badRequest().body(messages);
            }
            if(!user.getPassword().equals(user.getRetypePassword()))
            {
                return ResponseEntity.badRequest().body("Passwords do not match");
            }
            return ResponseEntity.ok().body("done register");
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> login( @Valid @RequestBody UserLoginDTOs userLoginDTOs,BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            List<String> message= bindingResult.getFieldErrors().stream().map((err)->err.getDefaultMessage()).collect(Collectors.toList());

            return   ResponseEntity.badRequest().body(message);
        }
        return   ResponseEntity.ok().body("done login");
    }
}
