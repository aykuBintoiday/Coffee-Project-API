package com.coffee.coffee_api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

/**
 * JwtService
 *
 * Ý NGHĨA:
 * - Service chịu trách nhiệm sinh, parse và kiểm tra JWT.
 * - Được sử dụng trong quá trình xác thực (login/register) và trong filter (JwtAuthFilter).
 *
 * THÀNH PHẦN CHÍNH:
 * - SECRET_KEY: key bí mật dùng để ký JWT (HS256). 
 *   ⚠️ Cần thay bằng key đủ mạnh, lưu trong config/ENV, không hard-code trong code.
 *
 * - getSignKey(): trả về key dạng HMAC-SHA từ SECRET_KEY.
 *
 * - generateToken(String subject):
 *   + Sinh JWT đơn giản chỉ chứa subject (ví dụ username).
 *   + Thời hạn: 10h.
 *
 * - extractUsername(token):
 *   + Parse token → lấy ra subject (username).
 *
 * - validateToken(token, username):
 *   + Kiểm tra username từ token có khớp username truyền vào không.
 *   + Kiểm tra token có hết hạn không.
 *
 * - isTokenExpired(token):
 *   + Trả về true nếu token đã hết hạn.
 *
 * - parse(token):
 *   + Parse JWT và trả về toàn bộ Claims (payload).
 *   + Có thể lấy subject, role, email... từ đây.
 *
 * - generate(Long id, String email, String role):
 *   + Sinh JWT nâng cao: subject = id (userId), 
 *     đồng thời thêm claim email và role.
 *   + Thời hạn: 10h.
 *
 * TÓM LẠI:
 * File này cung cấp công cụ để:
 *  - Sinh token mới (sau khi user login/register).
 *  - Parse token để đọc claim (id, email, role).
 *  - Xác thực token hợp lệ (chữ ký đúng, chưa hết hạn).
 *
 * LƯU Ý BẢO MẬT:
 * - SECRET_KEY phải có độ dài >= 256-bit, lưu trong biến môi trường hoặc config.
 * - Có thể thêm các claim như "issuer", "audience" để bảo mật tốt hơn.
 * - Nên hỗ trợ refresh token cho UX tốt.
 */
@Service
public class JwtService {
    private static final String SECRET_KEY = "your-256-bit-secret-your-256-bit-secret"; // ⚠️ Thay bằng key an toàn hơn

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // Sinh token chỉ với subject (vd: username)
    public String generateToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10h
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Lấy username (subject) từ token
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Kiểm tra token hợp lệ so với username
    public boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    // Kiểm tra token hết hạn chưa
    private boolean isTokenExpired(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }

    // Parse toàn bộ claims từ token
    public Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Sinh token nâng cao (có id, email, role)
    public String generate(Long id, String email, String role) {
        return Jwts.builder()
                .setSubject(String.valueOf(id))        // lưu id làm subject
                .claim("email", email)                 // thêm claim email
                .claim("role", role)                   // thêm claim role
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10h
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
