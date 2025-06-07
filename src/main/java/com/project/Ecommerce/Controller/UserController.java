package com.project.Ecommerce.Controller;

import com.project.Ecommerce.DTOs.UserDTOs;
import com.project.Ecommerce.DTOs.UserLoginDTOs;
import com.project.Ecommerce.Exceptions.DataNotFoundException;
import com.project.Ecommerce.Exceptions.PermissionDenyException;
import com.project.Ecommerce.Model.Token;
import com.project.Ecommerce.Model.User;
import com.project.Ecommerce.Repository.TokenRepository;
import com.project.Ecommerce.Respones.Product.ProductListResponses;
import com.project.Ecommerce.Respones.User.LoginResponse;
import com.project.Ecommerce.Respones.User.RegisterResponses;
import com.project.Ecommerce.Respones.User.UserResponses;
import com.project.Ecommerce.Service.UserService;
import com.project.Ecommerce.Component.LocalizationUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import  com.project.Ecommerce.Service.TokenService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static com.project.Ecommerce.Util.MessagesKey.*;

@RestController
@RequestMapping("${api.prefix}/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final LocalizationUtil localizationUtil;
    private final TokenRepository tokenRepository;

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
            User user = userService.validateUser(userLoginDTOs.getPhoneNumber(), userLoginDTOs.getPassword());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        LoginResponse.builder()
                                .message("Sai tài khoản hoặc mật khẩu")
                                .token(null)
                                .build()
                );
            }
            // Sinh token (accessToken) cho user
             String token = userService.generateToken(user);
            // Lưu vào bảng tokens, phải set user
            tokenRepository.save(
                    Token.builder()
                            .user(user)         // <<<< Lưu đúng user vào đây
                            .token(token)
                            .tokenType("BEARER")
                            .expirationDate(LocalDateTime.now().plusDays(7))
                            .revoked(false)
                            .expired(false)
                            .build()
            );
            return ResponseEntity.ok(
                    LoginResponse.builder()
                            .message(localizationUtil.getMessage(LOGIN_SUCCESSFULLY))
                            .id(user.getId())
                            .phoneNumber(user.getUsername())
                            .roles(user.getRoleId().getId())
                            .tokenType("BEARER ")
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
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/detail")
    public ResponseEntity<UserResponses> getUserDetail(
            @RequestHeader("Authorization") String authorization
    )
    {
        try {
            String extractToken = authorization.substring(7); //loại bỏ bear
            User user = userService.getUserDetailsFromToken(extractToken);
            return ResponseEntity.ok(UserResponses.fromUser(user));
        }
        catch (Exception e) {
            return
                    ResponseEntity.badRequest().build();
        }
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("/detail/{userID}")
    public ResponseEntity<?> updateUserDetail(
            @RequestBody UserDTOs userDTOs,
            @PathVariable Long userID,
            @RequestHeader("Authorization") String authorization
    ) {
        try {
            String token = authorization.startsWith("Bearer ") ? authorization.substring(7) : authorization;
            User userFromToken = userService.getUserDetailsFromToken(token);

            if (!userFromToken.getId().equals(userID)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Bạn không có quyền cập nhật thông tin người dùng khác.");
            }
            userDTOs.setId(userID);
            User updatedUser = userService.updateUser(userID, userDTOs);
            UserResponses userResponse = UserResponses.fromUser(updatedUser);
            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi hệ thống: " + e.getMessage());
        }
    }




}
