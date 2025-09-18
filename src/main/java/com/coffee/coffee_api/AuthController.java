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

/**
 * AuthController
 *
 * Ý NGHĨA:
 * - Cung cấp các API xác thực người dùng: /register, /login, /me.
 * - Dùng DTO: RegisterReq, LoginReq (request) và AuthResp (response).
 * - Kết hợp JwtService để sinh JWT sau khi đăng ký/đăng nhập thành công.
 *
 * THÀNH PHẦN:
 * - users: UserRepository thao tác DB (tìm user, lưu user).
 * - passwordEncoder: mã hoá/băm mật khẩu khi lưu & so khớp khi đăng nhập.
 * - jwt: sinh/parse token JWT chứa id/email/role.
 *
 * LUỒNG CHÍNH:
 * 1) POST /api/auth/register:
 *    - Validate input (@Valid).
 *    - Kiểm tra email đã tồn tại? nếu có → 400 Bad Request.
 *    - Tạo User mới (isActive=true, role=CUSTOMER), băm password.
 *    - Lưu DB → sinh JWT → trả AuthResp(ok=true, token + info user).
 *
 * 2) POST /api/auth/login:
 *    - Tìm user theo email (ignore case).
 *    - Kiểm tra isActive, đối chiếu password (passwordEncoder.matches).
 *    - Nếu fail → 401 Unauthorized với AuthResp(ok=false).
 *    - Nếu pass → sinh JWT → trả AuthResp(ok=true, token + info user).
 *
 * 3) GET /api/auth/me:
 *    - Lấy Authentication từ SecurityContext (đã set bởi JwtAuthFilter nếu token hợp lệ).
 *    - Nếu chưa đăng nhập → 401.
 *    - Nếu principal là User → trả AuthResp(ok=true, info user, token=null).
 *
 * LƯU Ý BẢO MẬT:
 * - Không trả passwordHash ra ngoài.
 * - Khi fail login/register nên hạn chế leak thông tin (giữ thông báo chung).
 * - JWT expiry nên đủ ngắn; có thể bổ sung refresh token tuỳ yêu cầu.
 *
 * GỢI Ý NÂNG CẤP:
 * - Chuẩn hoá lỗi: trả body theo 1 error schema (code/message).
 * - Thêm /logout (token blacklist) nếu cần revoke.
 * - Rate limit /login để chống bruteforce.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwt;

    /**
     * ĐĂNG KÝ TÀI KHOẢN
     * - Nếu email đã tồn tại → 400.
     * - Tạo user mới (role CUSTOMER, isActive=true) → sinh JWT → trả AuthResp.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResp> register(@Valid @RequestBody RegisterReq req) {
        if (users.existsByEmailIgnoreCase(req.email())) {
            return ResponseEntity.badRequest().body(new AuthResp(false, null, null, null, null, null));
        }

        User u = User.builder()
                .fullName(req.fullName())
                .email(req.email())
                .passwordHash(passwordEncoder.encode(req.password()))
                .role(Role.CUSTOMER)          // role mặc định khi đăng ký
                .isActive(true)               // kích hoạt mặc định
                .build();

        u = users.save(u);

        String token = jwt.generate(u.getId(), u.getEmail(), u.getRole().name()); // JWT chứa id/email/role
        return ResponseEntity.ok(new AuthResp(
                true, token, u.getId(), u.getFullName(), u.getEmail(), u.getRole().name()
        ));
    }

    /**
     * ĐĂNG NHẬP
     * - Xác thực email + password + trạng thái isActive.
     * - Nếu hợp lệ → sinh JWT → trả AuthResp (ok=true).
     * - Nếu không → 401 Unauthorized (ok=false).
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResp> login(@Valid @RequestBody LoginReq req) {
        User u = users.findByEmailIgnoreCase(req.email()).orElse(null);
        if (u == null || !Boolean.TRUE.equals(u.getIsActive())
                || !passwordEncoder.matches(req.password(), u.getPasswordHash())) {
            return ResponseEntity.status(401).body(new AuthResp(false, null, null, null, null, null));
        }

        String token = jwt.generate(u.getId(), u.getEmail(), u.getRole().name());
        return ResponseEntity.ok(new AuthResp(
                true, token, u.getId(), u.getFullName(), u.getEmail(), u.getRole().name()
        ));
    }

    /**
     * LẤY THÔNG TIN NGƯỜI DÙNG HIỆN TẠI
     * - Cần gửi kèm Authorization: Bearer <JWT>.
     * - JwtAuthFilter sẽ set principal = User vào SecurityContext nếu token hợp lệ.
     * - Trả về thông tin user, KHÔNG trả token (token=null).
     */
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
                true, null, u.getId(), u.getFullName(), u.getEmail(), u.getRole().name()
        ));
    }
}
