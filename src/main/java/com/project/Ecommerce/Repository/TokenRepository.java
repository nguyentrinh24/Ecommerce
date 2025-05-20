package com.project.Ecommerce.Repository;

import com.project.Ecommerce.Model.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token,Long> {
}
