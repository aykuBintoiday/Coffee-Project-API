package com.coffee.coffee_api.dto;

/**
 * AuthResponse.java
 *
 * Ý nghĩa:
 * - Đây là một DTO (Data Transfer Object) dạng record, dùng để trả phản hồi cho API xác thực (login/register).
 * - Giúp FE nhận biết trạng thái xác thực và lấy thông tin user (nếu thành công).
 *
 * Các trường:
 * - ok: trạng thái thành công (true) hoặc thất bại (false).
 * - token: JWT token được cấp khi xác thực thành công.
 * - id: mã định danh (ID) của người dùng.
 * - fullName: họ tên đầy đủ của người dùng.
 * - email: địa chỉ email dùng để đăng nhập.
 * - role: vai trò trong hệ thống (ví dụ: CUSTOMER, ADMIN).
 *
 * Phương thức tiện ích:
 * - fail(): tạo sẵn một AuthResponse thất bại (ok=false, các trường khác null).
 *   -> Giúp code gọn hơn khi cần trả về kết quả đăng nhập/đăng ký thất bại.
 *
 * Nói gọn lại: file này đại diện cho "gói dữ liệu phản hồi xác thực",
 * có thể chứa token + thông tin user nếu thành công, hoặc trả về fail() nếu thất bại.
 */
public record AuthResponse(
  boolean ok,
  String token,
  Long id,
  String fullName,
  String email,
  String role
) {
  public static AuthResponse fail() {
    return new AuthResponse(false, null, null, null, null, null);
  }
}
