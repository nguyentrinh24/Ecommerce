package com.project.Ecommerce.Fillter;


import com.project.Ecommerce.Component.JwtUtil;
import com.project.Ecommerce.Model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.persister.collection.OneToManyPersister;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFillter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {


        if (isByPassToken(request)) {
            filterChain.doFilter(request, response);
        }
        final  String authorizationHeader = request.getHeader("Authorization");
        //'Bearer ' bo qua 7 ki tu trong token
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            final String token = authorizationHeader.substring(7);
            final String phoneNumber = jwtUtil.extractPhoneNumber(token);

            if (phoneNumber != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User userDetails = (User) userDetailsService.loadUserByUsername(phoneNumber);
                if(jwtUtil.validateToken(token,userDetails) )
                {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities());

                    authenticationToken.setDetails(new WebAuthenticationDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
                filterChain.doFilter(request, response);
            }

        }


    }

    private boolean isByPassToken(@NotNull HttpServletRequest request) {
        final String path = request.getServletPath();
        final String method = request.getMethod(); // GET, POST, etc.

        // Danh sách API bỏ qua xác thực
        final List<Pair<String, String>> byPassTokens = Arrays.asList(
                Pair.of("/api/products", "GET"),
                Pair.of("/api/categories", "GET"),
                Pair.of("/api/user/register", "POST"),
                Pair.of("/api/user/login", "POST")
        );

        for (Pair<String, String> byPassToken : byPassTokens) {
            if (path.equals(byPassToken.getLeft()) && method.equalsIgnoreCase(byPassToken.getRight())) {
                return true;
            }
        }

        return false; // Nếu không khớp gì thì không bỏ qua
    }


}
