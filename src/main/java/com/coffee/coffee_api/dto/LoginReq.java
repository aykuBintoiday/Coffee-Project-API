package com.coffee.coffee_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * LoginReq.java
 *
 * Ý nghĩa:
 * - Đây là DTO (Data Transfer Object) dạng record, dùng để nhận dữ liệu từ client khi gọi API đăng nhập.
 * - Kết hợp với @Valid trong Controller để tự động kiểm tra tính hợp lệ của input.
 *
 * Các trường:
 * - email:
 *   + Có annotation @Email → phải đúng định dạng email.
 *   + Có annotation @NotBlank → không được để trống.
 * - password:
 *   + Có annotation @NotBlank → không được để trống.
 *
 * Tóm lại: file này định nghĩa "yêu cầu đăng nhập" (login request),
 * đảm bảo dữ liệu client gửi lên hợp lệ trước khi xử lý.
 */
public record LoginReq(
    @Email @NotBlank String email,
    @NotBlank String password
) {}
