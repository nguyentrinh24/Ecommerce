package com.project.Ecommerce.Repository;

import com.project.Ecommerce.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByEmail(String email);
    List<User> findByPhoneNumber(String phoneNumber);
}
