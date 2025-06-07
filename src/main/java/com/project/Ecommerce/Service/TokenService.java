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
                .token(token)              // token JWT
                .tokenType(role.getName()) // role làm tokenType
                .revoked(false)
                .expired(false)
                .build());
    }

    @Override
    public void revokeAllUserTokens(User user) {
        var tokens = tokenRepository.findAllByUser(user);
        for (Token token : tokens) {
            if (!token.isExpired() && !token.isRevoked()) {
                token.setExpired(true);
                token.setRevoked(true);
            }
        }
        tokenRepository.saveAll(tokens);
    }
}
