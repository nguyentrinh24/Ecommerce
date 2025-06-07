package com.project.Ecommerce.Repository;

import com.project.Ecommerce.Model.Token;
import com.project.Ecommerce.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.stereotype.Repository;


@Repository
public interface TokenRepository extends JpaRepository<Token,Long> {
    List<Token> findAllByUser(User user);

    Token findByToken(String token);

    Token findByRefreshToken(String token);



}
