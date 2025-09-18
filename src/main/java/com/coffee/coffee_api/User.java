package com.coffee.coffee_api;

import jakarta.persistence.*;
import lombok.*;

/**
 * User entity
 *
 * Ý NGHĨA:
 * - Đây là Entity ánh xạ với bảng "users" trong database.
 * - Lưu trữ thông tin cơ bản của người dùng phục vụ cho xác thực & phân quyền.
 *
 * CÁC TRƯỜNG:
 * - id:
 *   + Khóa chính (Primary Key).
 *   + AUTO_INCREMENT (IDENTITY).
 *
 * - fullName:
 *   + Họ và tên đầy đủ của user.
 *   + NOT NULL, tối đa 120 ký tự.
 *
 * - email:
 *   + Email đăng nhập của user.
 *   + UNIQUE (không được trùng), NOT NULL.
 *   + Có index để tối ưu tìm kiếm.
 *
 * - passwordHash:
 *   + Mật khẩu đã được mã hoá (hash bằng BCrypt).
 *   + NOT NULL.
 *   + Không bao giờ lưu plain text password.
 *
 * - role:
 *   + Kiểu Enum Role (ví dụ: CUSTOMER, ADMIN).
 *   + Lưu dưới dạng chuỗi (EnumType.STRING).
 *   + Giá trị mặc định = CUSTOMER.
 *   + Quyết định quyền truy cập (authorities).
 *
 * - isActive:
 *   + Trạng thái tài khoản (true = đang hoạt động, false = bị vô hiệu hóa).
 *   + Giá trị mặc định = true.
 *
 * ANNOTATION:
 * - @Entity: đánh dấu class là một thực thể JPA.
 * - @Table: ánh xạ với bảng "users", có index trên cột email.
 * - @Data: Lombok, sinh getter/setter/toString/equals/hashCode.
 * - @NoArgsConstructor, @AllArgsConstructor: constructor mặc định & đầy đủ.
 * - @Builder: hỗ trợ builder pattern.
 *
 * TÓM LẠI:
 * Đây là lớp ánh xạ user trong DB, dùng trong quy trình đăng ký/đăng nhập,
 * xác thực JWT, và phân quyền trong hệ thống.
 */
@Entity
@Table(name = "users", indexes = { @Index(columnList = "email") })
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name="full_name", nullable=false, length=120)
  private String fullName;

  @Column(nullable=false, unique=true, length=191)
  private String email;

  @Column(name="password_hash", nullable=false, length=255)
  private String passwordHash;

  @Enumerated(EnumType.STRING)              
  @Column(nullable=false, length=16)
  @Builder.Default
  private Role role = Role.CUSTOMER;        

  @Column(name="is_active")
  @Builder.Default
  private Boolean isActive = true;
}
