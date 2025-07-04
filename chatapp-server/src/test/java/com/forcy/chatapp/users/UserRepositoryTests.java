package com.forcy.chatapp.users;

import com.forcy.chatapp.entity.Role;
import com.forcy.chatapp.entity.User;
import com.forcy.chatapp.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private UserRepository userRepository;

    @Autowired private TestEntityManager entityManager;

    @Test
    public void testCreateUser() {
        Role role = entityManager.find(Role.class, 1);
        User user = new User();
        user.setEmail("autdant@gmail.com");
        user.setPassword(passwordEncoder.encode("12345678"));
        user.setFullName("Autdant");
        user.setCreateAt(new Date());
        user.setAvatarUrl("https://res.cloudinary.com/dm8tfyppk/image/upload/v1751360443/avatar/c4d30890-c6e2-48e6-a3af-d86089639b5d.jpg");
        user.setVerified(false);
        user.addRole(role);

        User savedUser = userRepository.save(user);
        assertThat(savedUser.getId()).isGreaterThan(0);
    }
    @Test
    void testCreateUserWithFullFields() {
        // 1. Lấy role có ID = 1 từ database
        Role role = entityManager.find(Role.class, 1);

        // 2. Tạo user mới
        User user = new User();
        user.setEmail("autdant111@gmail.com");
        user.setPassword(passwordEncoder.encode("12345678"));
        user.setFullName("Autdant");
        user.setAvatarUrl("https://res.cloudinary.com/dm8tfyppk/image/upload/v1751360443/avatar/c4d30890-c6e2-48e6-a3af-d86089639b5d.jpg");
        user.setCreateAt(Date.from(Instant.now().minus(2, ChronoUnit.DAYS))); // test case xóa
        user.setVerified(false);

        user.addRole(role);

        // 3. Lưu user
        User savedUser = userRepository.save(user);

        // 4. Kiểm tra user đã lưu thành công
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.isVerified()).isFalse();
        assertThat(savedUser.getRoles()).contains(role);
    }
    @Test
    void testDeleteUnverifiedUsersBefore() {
        // Giả lập expiredDate là 1 ngày trước
        Date expiredDate = Date.from(Instant.now().minus(1, ChronoUnit.DAYS));

        // Gọi phương thức xóa
        int deletedCount = userRepository.deleteUnverifiedUsersBefore(expiredDate);

        System.out.println("🧹 Đã xóa " + deletedCount + " tài khoản chưa xác thực quá hạn.");
    }
}
