package com.forcy.chatapp.users;

import com.forcy.chatapp.entity.Role;
import com.forcy.chatapp.user.RoleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class RoleRepositoryTests {

    @Autowired private RoleRepository roleRepository;

    @Test
    public void testCreateFirstRole(){
        Role role = new Role();
        role.setName("USER");
        role.setDescription("Regular user who can send and receive messages");
        roleRepository.save(role);
    }
    @Test
    public void testCreateRoleV2(){
        Role role = new Role();
        role.setName("ROLE_ADMIN");
        role.setDescription("Admin who can manage users and rooms");
        roleRepository.save(role);
    }

    @Test
    public void testCreateMultipleRoles(){
        Role admin = Role.builder().name("ADMIN").description("	Admin who can manage users and rooms").build();
        Role moderator = Role.builder().name("MODERATOR").description("	Moderator who can delete messages or mute users").build();
        Role guest = Role.builder().name("GUEST").description("	Guest who can only view public chats").build();

        roleRepository.saveAll(List.of(admin, moderator, guest));

    }


    @Test
    public void testUpdateRoleNames() {
        // Tìm các role hiện tại
        Role admin = roleRepository.findByName("ADMIN");
        Role moderator = roleRepository.findByName("MODERATOR");
        Role user = roleRepository.findByName("USER");

        Role guest = roleRepository.findByName("GUEST");


        // Cập nhật tên và mô tả nếu muốn
        admin.setName("ROLE_ADMIN");
        moderator.setName("ROLE_MODERATOR");
        guest.setName("ROLE_GUEST");
        user.setName("ROLE_USER");
        // Lưu lại
        roleRepository.saveAll(List.of(admin, moderator, guest));
        assertThat(roleRepository.findByName(admin.getName()).getName()).isEqualTo("ROLE_ADMIN");

    }
}
