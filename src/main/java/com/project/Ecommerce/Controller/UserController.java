package com.project.Ecommerce.Controller;

import com.project.Ecommerce.DTOs.UserDTOs;
import com.project.Ecommerce.DTOs.UserLoginDTOs;
import com.project.Ecommerce.Model.User;
import com.project.Ecommerce.Respones.User.LoginResponse;
import com.project.Ecommerce.Respones.User.RegisterRespones;
import com.project.Ecommerce.Service.Iml.UserServiceIml;
import com.project.Ecommerce.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/user")
@RequiredArgsConstructor
public class UserController {
    //DI
    private final UserService userService;
    private final UserServiceIml userServiceIml;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDTOs userDTO, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                List<String> messages = bindingResult.getFieldErrors()
                        .stream()
                        .map(err -> err.getDefaultMessage())
                        .collect(Collectors.toList());
                return ResponseEntity.badRequest().body(messages);
            }

            if (!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
                return ResponseEntity.badRequest().body("Passwords do not match");
            }

            User createdUser = userService.createUser(userDTO);

            RegisterRespones response = new RegisterRespones();
            response.setMessage("Register successfully");
            response.setUser(createdUser);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDTOs userLoginDTOs, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> message = bindingResult.getFieldErrors()
                    .stream()
                    .map(err -> err.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(message);
        }

        // check login
        String token = userService.login(userLoginDTOs.getPhoneNumber() ,userLoginDTOs.getPassword());
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }
}
