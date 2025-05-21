package com.project.Ecommerce.Repository;

import com.project.Ecommerce.Model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {
    // lay don hang cua 1 user
    List<Order> findByUserId(Long id);

    void deleteById(Long id);
}
