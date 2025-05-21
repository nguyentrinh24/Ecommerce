package com.project.Ecommerce.Repository;

import com.project.Ecommerce.Model.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail,Long> {
    List<OrderDetail> findByOrderId(Long orderId);
}
