package com.project.Ecommerce.Service.Iml;

import com.project.Ecommerce.DTOs.UserDTOs;
import com.project.Ecommerce.Exceptions.DataNotFoundException;
import com.project.Ecommerce.Exceptions.ExpiredTokenException;
import com.project.Ecommerce.Exceptions.PermissionDenyException;
import com.project.Ecommerce.Model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public interface UserServiceIml {
    User findById(Long id);
    User createUser(UserDTOs user) throws Exception;
    User updateUser(Long id, UserDTOs userDTO) throws DataNotFoundException, PermissionDenyException;
    User deleteUser(Long id);
    Page<User> findAllUsers(String key, Pageable pageable);

    String login(String phoneNumber, String password);

    boolean existsByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);

    User getUserDetailsFromToken(String token) throws ExpiredTokenException;

}

