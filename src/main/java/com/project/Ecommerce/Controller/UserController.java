package com.project.Ecommerce.Controller;

import com.project.Ecommerce.DTOs.UserDTOs;
import com.project.Ecommerce.DTOs.UserLoginDTOs;
import com.project.Ecommerce.Model.User;
import com.project.Ecommerce.Respones.User.LoginResponse;
import com.project.Ecommerce.Respones.User.RegisterResponses;
import com.project.Ecommerce.Service.UserService;
import com.project.Ecommerce.Component.LocalizationUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

import static com.project.Ecommerce.Util.MessagesKey.*;

@RestController
@RequestMapping("${api.prefix}/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final LocalizationUtil localizationUtil;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponses> registerUser(
            @Valid @RequestBody UserDTOs userDTO,
            BindingResult bindingResult) {

        try {
            if (bindingResult.hasErrors()) {
                String messages = bindingResult.getFieldErrors().stream()
                        .map(err -> err.getDefaultMessage())
                        .collect(Collectors.joining("; "));

                return ResponseEntity.badRequest().body(
                        RegisterResponses.builder()
                                .message(messages)
                                .user(null)
                                .build()
                );
            }

            if (!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
                return ResponseEntity.badRequest().body(
                        RegisterResponses.builder()
                                .message(localizationUtil.getMessage(PASSWORD_NOT_MATCH))
                                .user(null)
                                .build()
                );
            }

            User createdUser = userService.createUser(userDTO);

            return ResponseEntity.ok(
                    RegisterResponses.builder()
                            .message(localizationUtil.getMessage(REGISTER_SUCCESSFULLY))
                            .user(createdUser)
                            .build()
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    RegisterResponses.builder()
                            .message(localizationUtil.getMessage(REGISTER_FAILED, e.getMessage()))
                            .user(null)
                            .build()
            );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody UserLoginDTOs userLoginDTOs,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getFieldErrors().stream()
                    .map(err -> err.getDefaultMessage())
                    .collect(Collectors.joining("; "));

            return ResponseEntity.badRequest().body(
                    LoginResponse.builder()
                            .message(errorMessages)
                            .token(null)
                            .build()
            );
        }

        try {
            String token = userService.login(userLoginDTOs.getPhoneNumber(), userLoginDTOs.getPassword());

            return ResponseEntity.ok(
                    LoginResponse.builder()
                            .message(localizationUtil.getMessage(LOGIN_SUCCESSFULLY))
                            .token(token)
                            .build()
            );

        } catch (Exception e) {
            String errorMessage;
            try {
                errorMessage = localizationUtil.getMessage(LOGIN_FAILED, e.getMessage());
            } catch (Exception ex) {
                errorMessage = "Lỗi không xác định: " + e.getMessage();
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    LoginResponse.builder()
                            .message(errorMessage)
                            .token(null)
                            .build()
            );
        }
    }
}
