package com.project.Ecommerce.Service.Iml;

import com.project.Ecommerce.DTOs.UserDTOs;
import com.project.Ecommerce.Exceptions.DataNotFoundException;
import com.project.Ecommerce.Model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public interface UserServiceIml {
    User findById(Long id);
    User createUser(UserDTOs user) throws DataNotFoundException;
    User updateUser(Long id, UserDTOs user);
    User deleteUser(Long id);
    Page<User> findAllUsers(String key,Pageable pageable);

    String login(String phoneNumber, String password);

    boolean existsByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);

}
