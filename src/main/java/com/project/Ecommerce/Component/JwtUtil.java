package com.project.Ecommerce.Component;


import com.project.Ecommerce.Model.Token;
import com.project.Ecommerce.Model.User;
import com.project.Ecommerce.Repository.TokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor

public class JwtUtil {
    private final TokenRepository tokenRepository;
    @Value("${jwt.expiration}")
    private Long expiration; // time live-> save local
    @Value("${jwt.secretKey}")
    private String secretKey;

    public String generateToken(User  user){
        //properties -> claims
        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("phoneNumber", user.getPhoneNumber());
        claims.put("user", user.getId());
        claims.put("authorities", List.of("ROLE_" + user.getRoleId().getName()));
        try{
            String token = Jwts.builder()
                    .setClaims(claims) // ? extract claims
                    .setSubject(user.getPhoneNumber())
                    .setExpiration(new Date(System.currentTimeMillis() + expiration *10000))
                    .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                    .compact();
            return token;
        }
        catch (Exception e){
            System.out.println("JWT Generation Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    // tao secretkey dung jjwt
    private Key getSecretKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    // extract claims
    public Claims exctractClaims (String token)
    {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    //extract claims flow field theo dang xxxx.yyyy.zzzz
    public <T> T getClaim (String token, Function<Claims, T> claimsResolver)
    {
        final  Claims claims = this.exctractClaims(token);
        return claimsResolver.apply(claims);
    }

    // check expiration date
    public boolean checkExpiration(String token)
    {
        Date expirationDate = this.exctractClaims(token).getExpiration();
        return expirationDate.before(new Date());
    }

    //extract phonenumber
    public String extractPhoneNumber(String token) {
        return getClaim(token, Claims::getSubject);
    }


    // validate token

    public boolean validateToken(String token, User userDetails) {
        try {
            String phoneNumber = extractPhoneNumber(token);
            Token existingToken = tokenRepository.findByToken(token);
            if (existingToken == null ||
                    existingToken.isRevoked() ||
                    !userDetails.is_active()
            ) {
                return false;
            }
            return (phoneNumber.equals(userDetails.getUsername()))
                    && !checkExpiration(token);
        } catch (MalformedJwtException e) {
            throw new JwtException("Invalid JWT token: {}"+ e.getMessage());
        } catch (ExpiredJwtException e) {
            throw new JwtException("JWT token is expired: {}" +e.getMessage());

        } catch (UnsupportedJwtException e) {
            throw new JwtException("IJWT token is unsupported: {}" +e.getMessage());

        } catch (IllegalArgumentException e) {
            throw new JwtException("JWT claims string is empty: {}"+e.getMessage());

        }

    }

}
