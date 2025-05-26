package com.project.Ecommerce.Service;

import com.project.Ecommerce.Model.Role;
import com.project.Ecommerce.Model.Token;
import com.project.Ecommerce.Model.User;
import com.project.Ecommerce.Repository.TokenRepository;
import com.project.Ecommerce.Service.Iml.TokenServiceIml;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class TokenService implements TokenServiceIml {
    final  private TokenRepository tokenRepository;

    @Override
    public Token addToken(User user, String token, Role role) {
        return tokenRepository.save(Token.builder()
                .user(user)
                .token(role.getName())
                .token(token).build());
    }
}
