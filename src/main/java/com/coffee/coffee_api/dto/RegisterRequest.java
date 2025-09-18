package com.coffee.coffee_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * RegisterRequest.java
 *
 * Ý nghĩa:
 * - Đây là DTO (Data Transfer Object) dạng record, dùng để nhận dữ liệu từ client khi gọi API đăng ký tài khoản.
 * - Có thêm ràng buộc chi tiết về độ dài chuỗi để nâng cao tính bảo mật và hợp lệ của dữ liệu đầu vào.
 *
 * Các trường:
 * - fullName:
 *   + @NotBlank → không được để trống.
 *   + @Size(min = 2, max = 120) → họ tên phải có từ 2 đến 120 ký tự.
 *
 * - email:
 *   + @NotBlank → không được để trống.
 *   + @Email → phải đúng định dạng email.
 *
 * - password:
 *   + @NotBlank → không được để trống.
 *   + @Size(min = 8, max = 255) → mật khẩu phải có ít nhất 8 ký tự, tối đa 255 ký tự.
 *
 * Tóm lại: file này định nghĩa "yêu cầu đăng ký" với kiểm tra đầu vào chặt chẽ hơn,
 * giúp backend tránh lỗi dữ liệu và tăng tính an toàn trong quá trình tạo tài khoản mới.
 */
public record RegisterRequest(
    @NotBlank @Size(min = 2, max = 120) String fullName,
    @NotBlank @Email String email,
    @NotBlank @Size(min = 8, max = 255) String password
) {}
