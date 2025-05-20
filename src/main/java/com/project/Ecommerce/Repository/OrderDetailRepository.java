package com.project.Ecommerce.Repository;

import com.project.Ecommerce.Model.OderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OderDetail,Long> {
}
