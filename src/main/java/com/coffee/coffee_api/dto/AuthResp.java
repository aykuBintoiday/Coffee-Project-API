package com.coffee.coffee_api.dto;

/**
 * AuthResp.java
 *
 * Ý nghĩa:
 * - Đây là một DTO (Data Transfer Object) dạng record, dùng để trả kết quả phản hồi cho API xác thực (register/login).
 * - Khi client gọi API, backend sẽ trả về AuthResp để thông báo trạng thái và dữ liệu liên quan.
 * - Giúp chuẩn hóa phản hồi: FE chỉ cần đọc các field này để biết login thành công hay thất bại.
 *
 * Các trường:
 * - ok: true nếu thành công, false nếu thất bại.
 * - token: JWT token (dùng cho các request sau khi login thành công).
 * - id: mã định danh (ID) của người dùng trong DB.
 * - fullName: họ và tên đầy đủ của user.
 * - email: địa chỉ email đăng nhập.
 * - role: vai trò của user trong hệ thống (ví dụ: CUSTOMER, ADMIN).
 *
 * Nói ngắn gọn: file này chính là "tấm vé" trả về cho client sau khi xác thực,
 * kèm token và thông tin cơ bản của user.
 */
public record AuthResp(
    boolean ok,
    String token,
    Long id,
    String fullName,
    String email,
    String role
) {}
