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

/**
 * JwtAuthFilter
 *
 * Ý NGHĨA & NHIỆM VỤ:
 * - Là một Spring Security filter chạy MỖI REQUEST (extends OncePerRequestFilter).
 * - Trích xuất JWT từ header Authorization (Bearer ...), xác thực token, nạp user & set Authentication vào SecurityContext.
 * - BỎ QUA lọc cho các endpoint công khai như /api/auth/** (đăng nhập/đăng ký) và /api/public/**.
 *
 * LUỒNG XỬ LÝ TÓM TẮT:
 * 1) shouldNotFilter(): nếu request vào đường dẫn công khai → bỏ qua filter.
 * 2) doFilterInternal():
 *    - Lấy header Authorization; nếu có dạng "Bearer <token>" thì parse JWT.
 *    - Lấy subject (uid), claim "role", tìm User trong DB.
 *    - Nếu user tồn tại & isActive=true → tạo Authentication với quyền ROLE_<role> và set vào SecurityContext.
 *    - Nếu có lỗi (token sai/het hạn/parse fail) → clear SecurityContext (đảm bảo request không coi là đã đăng nhập).
 * 3) chain.doFilter(): chuyển tiếp cho filter/handler tiếp theo.
 *
 * LƯU Ý BẢO MẬT:
 * - Chỉ tin token hợp lệ & còn hạn: JwtService.parse() phải kiểm chữ ký, thời hạn (exp), issuer, audience...
 * - Nên tiền tố quyền với "ROLE_" để tương thích @PreAuthorize("hasRole('...')").
 * - Nên cân nhắc revoke/blacklist token khi user logout/đổi mật khẩu (tuỳ yêu cầu).
 * - Tránh lạm dụng claim "role" từ token nếu phân quyền thay đổi thường xuyên → có thể lấy quyền từ DB mỗi request.
 *
 * GỢI Ý NÂNG CẤP (tuỳ nhu cầu):
 * - Thêm log debug/warn khi bắt exception để tiện trace.
 * - Hỗ trợ refresh token & xử lý clock skew.
 * - Cho phép nhiều quyền (authorities) thay vì 1 role đơn.
 * - Chuẩn hoá đường dẫn bỏ lọc qua cấu hình (properties) thay vì hard-code.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
  private final JwtService jwt;
  private final UserRepository users;

  /**
   * BỎ QUA FILTER cho các route công khai (auth/public).
   * Tránh việc bắt buộc phải có JWT khi gọi các API đăng nhập/đăng ký hay public assets.
   */
  @Override
  protected boolean shouldNotFilter(HttpServletRequest req) {
    String path = req.getRequestURI();
    return path.startsWith("/api/auth/") || path.startsWith("/api/public/");
  }

  /**
   * LỌC CHÍNH:
   * - Đọc header Authorization.
   * - Nếu có Bearer token → parse & xác thực → set Authentication.
   * - Nếu lỗi → xoá context để đảm bảo request là ẩn danh (anonymous).
   */
  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {
    String header = req.getHeader(HttpHeaders.AUTHORIZATION);

    // Chỉ xử lý khi có header dạng "Bearer <token>"
    if (header != null && header.startsWith("Bearer ")) {
      String token = header.substring(7);
      try {
        // Parse & validate token (JwtService phải kiểm chữ ký, hạn dùng, issuer/audience...)
        var claims = jwt.parse(token);

        // subject thường là user id (string) → chuyển Long
        Long uid = Long.valueOf(claims.getSubject());

        // Lấy role từ claim "role" trong JWT (ví dụ: CUSTOMER / ADMIN)
        String role = (String) claims.get("role");

        // Tải user từ DB để kiểm tra trạng thái & nạp vào SecurityContext
        User u = users.findById(uid).orElse(null);

        // Chỉ xác thực khi user hợp lệ & đang active
        if (u != null && Boolean.TRUE.equals(u.getIsActive())) {
          // Tạo Authentication với quyền ROLE_<role>
          var auth = new UsernamePasswordAuthenticationToken(
              u,                           // principal: đối tượng người dùng (có thể là username/email tuỳ bạn)
              null,                        // credentials: null vì đã xác thực bằng JWT rồi
              List.of(new SimpleGrantedAuthority("ROLE_" + role)) // authorities
          );
          SecurityContextHolder.getContext().setAuthentication(auth);
        } else {
          // User không tồn tại hoặc bị vô hiệu → không set auth
          SecurityContextHolder.clearContext();
        }
      } catch (Exception e) {
        // Token không hợp lệ/expired/parse fail → không xác thực
        SecurityContextHolder.clearContext();
        // (Tuỳ chọn) có thể set 401 ở đây nếu muốn chặn luôn:
        // res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // return;
      }
    }

    // Tiếp tục chuỗi filter/handler
    chain.doFilter(req, res);
  }
}
