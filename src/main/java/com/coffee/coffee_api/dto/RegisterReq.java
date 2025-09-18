package com.coffee.coffee_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * RegisterReq.java
 *
 * Ý nghĩa:
 * - Đây là DTO (Data Transfer Object) dạng record, dùng để nhận dữ liệu từ client khi gọi API đăng ký tài khoản.
 * - Kết hợp với @Valid trong Controller để tự động kiểm tra tính hợp lệ của input trước khi xử lý.
 *
 * Các trường:
 * - fullName:
 *   + @NotBlank → không được để trống (yêu cầu nhập họ tên đầy đủ).
 *
 * - email:
 *   + @Email → phải đúng định dạng email.
 *   + @NotBlank → không được để trống.
 *
 * - password:
 *   + @Size(min = 8) → mật khẩu phải có ít nhất 8 ký tự.
 *
 * Tóm lại: file này định nghĩa "yêu cầu đăng ký" (register request),
 * đảm bảo dữ liệu client gửi lên (họ tên, email, mật khẩu) hợp lệ trước khi tạo user mới.
 */
public record RegisterReq(
    @NotBlank String fullName,
    @Email @NotBlank String email,
    @Size(min = 8) String password
) {}
