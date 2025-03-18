package com.dlim2012.security.config;

import com.dlim2012.security.dto.Roles;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final Roles roles;
    private final JwtDecoder jwtDecoder;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String jwtToken = authHeader.substring(7);
        Jwt jwt;
        try {
            jwt = jwtDecoder.decode(jwtToken);
        } catch (Exception e) {
            filterChain.doFilter(request, response);
            return;
        }
        // Safely check the "scope" claim.
        String scopeClaim = (String) jwt.getClaims().get("scope");
        if (scopeClaim == null || scopeClaim.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }
        String[] scopes = scopeClaim.split(" ");
        Authentication authentication = new JwtAuthenticationToken(
                jwt,
                Arrays.stream(scopes)
                      .map(SimpleGrantedAuthority::new)
                      .toList());
        // Set authentication in the context
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Continue the filter chain only if one of the roles is allowed.
        for (String role : scopes) {
            if (roles.hasRole(role)) {
                filterChain.doFilter(request, response);
                return;
            }
        }
        System.out.println("jwt authentication null");
        SecurityContextHolder.getContext().setAuthentication(null);
        filterChain.doFilter(request, response);
    }
}
