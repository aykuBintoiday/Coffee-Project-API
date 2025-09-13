package com.coffee.coffee_api.security;

import com.coffee.coffee_api.User;
import com.coffee.coffee_api.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
  private final JwtService jwt;
  private final UserRepository users;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest req) {
    String path = req.getRequestURI();
    return path.startsWith("/api/auth/") || path.startsWith("/api/public/");
  }

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {
    String header = req.getHeader(HttpHeaders.AUTHORIZATION);
    if (header != null && header.startsWith("Bearer ")) {
      String token = header.substring(7);
      try {
        var claims = jwt.parse(token);
        Long uid = Long.valueOf(claims.getSubject());
        String role = (String) claims.get("role");
        User u = users.findById(uid).orElse(null);
        if (u != null && Boolean.TRUE.equals(u.getIsActive())) {
          var auth = new UsernamePasswordAuthenticationToken(
              u, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
          SecurityContextHolder.getContext().setAuthentication(auth);
        }
      } catch (Exception e) {
        SecurityContextHolder.clearContext();
      }
    }
    chain.doFilter(req, res);
  }
}