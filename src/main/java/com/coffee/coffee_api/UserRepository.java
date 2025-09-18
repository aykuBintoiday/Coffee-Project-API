package com.coffee.coffee_api;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * UserRepository
 *
 * Ý NGHĨA:
 * - Đây là interface kế thừa JpaRepository để thao tác với entity User trong DB.
 * - Cung cấp sẵn các method CRUD cơ bản (save, findAll, findById, delete...).
 * - Định nghĩa thêm các method query theo email (Spring Data JPA tự sinh SQL).
 *
 * CÁC METHOD:
 * - existsByEmailIgnoreCase(String email):
 *   + Trả về true nếu có user tồn tại với email (không phân biệt hoa/thường).
 *   + Dùng để kiểm tra khi đăng ký (tránh trùng email).
 *
 * - findByEmailIgnoreCase(String email):
 *   + Tìm user theo email (không phân biệt hoa/thường).
 *   + Trả về Optional<User> (có thể rỗng nếu không tìm thấy).
 *   + Dùng trong quá trình đăng nhập.
 *
 * TÓM LẠI:
 * Repository này là cầu nối giữa Service/Controller và database
 * cho các thao tác liên quan đến User.
 */

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmailIgnoreCase(String email);
    Optional<User> findByEmailIgnoreCase(String email);
}
