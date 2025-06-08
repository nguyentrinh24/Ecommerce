package com.project.Ecommerce.Fillter;

import com.project.Ecommerce.Component.JwtUtil;
import com.project.Ecommerce.Model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.internal.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        final String path = request.getServletPath();
        final String method = request.getMethod();

        System.out.println("JWT Filter triggered: " + method + " " + path);

        try {
            if (isByPassToken(request)) {
                System.out.println(">> Bypass JWT check: " + method + " " + path);
                filterChain.doFilter(request, response);
                return;
            }

            final String authorizationHeader = request.getHeader("Authorization");
            System.out.println(">> Authorization header: " + authorizationHeader);

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                final String token = authorizationHeader.substring(7);
                final String phoneNumber = jwtUtil.extractPhoneNumber(token);

                if (phoneNumber != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    User userDetails = (User) userDetailsService.loadUserByUsername(phoneNumber);

                    if (jwtUtil.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities());

                        authenticationToken.setDetails(new WebAuthenticationDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                        System.out.println(">> JWT authenticated for user: " + phoneNumber);

                        System.out.println(">> SecurityContext authentication: " + SecurityContextHolder.getContext().getAuthentication());
                    } else {
                        System.out.println(">> Token invalid for user: " + phoneNumber);
                    }
                }
            }

            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            System.out.println("JWT ERROR: " + ex.getMessage());
            throw new RuntimeException("Token không hợp lệ hoặc đã hết hạn");
        }
    }

    private boolean isByPassToken(@NotNull HttpServletRequest request) {
        final String path = request.getServletPath();
        final String method = request.getMethod();

        final List<Pair<String, String>> byPassTokens = Arrays.asList(
                Pair.of("/api/v1/categories/get-orders-by-keyword", "GET"),
                Pair.of("/api/v1/order_details/order/**", "GET"),
                Pair.of("/api/v1/order_details/**", "GET"),
                Pair.of("/api/v1/products", "GET"),
                Pair.of("/api/v1/products/**", "GET"),
                Pair.of("/api/v1/categories", "GET"),
                Pair.of("/api/v1/user/register", "POST"),
                Pair.of("/api/v1/user/login", "POST"),
                Pair.of("/api/v1/role", "GET"),
                Pair.of("/api/v1/products/images/**", "GET"),
                Pair.of("/api/v1/products/generateProductFake", "POST"),
                Pair.of("//api/v1/order/**", "POST")

        );

        for (Pair<String, String> entry : byPassTokens) {
            if (pathMatcher.match(entry.getLeft(), path) && method.equalsIgnoreCase(entry.getRight())) {
                return true;
            }
        }

        return false;
    }
}
