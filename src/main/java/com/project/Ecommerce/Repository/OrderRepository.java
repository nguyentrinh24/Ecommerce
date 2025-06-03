package com.project.Ecommerce.Repository;

import com.project.Ecommerce.Model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    // lay don hang cua 1 user
    List<Order> findByUserId(Long id);

    void deleteById(Long id);
    @Query("SELECT o FROM Order o WHERE o.active = true AND (:keyword IS NULL OR :keyword = '' OR " +
            "o.fullName LIKE %:keyword% " +
            "OR o.address LIKE %:keyword% " +
            "OR o.note LIKE %:keyword% " +
            "OR o.email LIKE %:keyword%)")
    Page<Order> getOrderByKeyword(@Param(("keyword"))String keyword, Pageable pageRequest);
}
