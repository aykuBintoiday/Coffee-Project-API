package com.coffee.coffee_api;

import com.coffee.coffee_api.dto.AuthResp;
import com.coffee.coffee_api.dto.LoginReq;
import com.coffee.coffee_api.dto.RegisterReq;
import com.coffee.coffee_api.security.JwtService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwt;

    @PostMapping("/register")
    public ResponseEntity<AuthResp> register(@Valid @RequestBody RegisterReq req) {
        if (users.existsByEmailIgnoreCase(req.email())) {
            return ResponseEntity.badRequest().body(new AuthResp(false, null, null, null, null, null));
        }

        User u = User.builder()
                .fullName(req.fullName())
                .email(req.email())
                .passwordHash(passwordEncoder.encode(req.password()))
                .role(Role.CUSTOMER)          // ✅ enum
                .isActive(true)
                .build();

        u = users.save(u);

        String token = jwt.generate(u.getId(), u.getEmail(), u.getRole().name()); // ✅ to String
        return ResponseEntity.ok(new AuthResp(
                true, token, u.getId(), u.getFullName(), u.getEmail(), u.getRole().name()
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResp> login(@Valid @RequestBody LoginReq req) {
        User u = users.findByEmailIgnoreCase(req.email()).orElse(null);
        if (u == null || !Boolean.TRUE.equals(u.getIsActive())
                || !passwordEncoder.matches(req.password(), u.getPasswordHash())) {
            return ResponseEntity.status(401).body(new AuthResp(false, null, null, null, null, null));
        }

        String token = jwt.generate(u.getId(), u.getEmail(), u.getRole().name()); // ✅ to String
        return ResponseEntity.ok(new AuthResp(
                true, token, u.getId(), u.getFullName(), u.getEmail(), u.getRole().name()
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResp> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(401).build();
        }
        // principal được set trong JwtAuthFilter là User
        Object principal = auth.getPrincipal();
        if (!(principal instanceof User u)) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(new AuthResp(
                true, null, u.getId(), u.getFullName(), u.getEmail(), u.getRole().name() // ✅ to String
        ));
    }
}
