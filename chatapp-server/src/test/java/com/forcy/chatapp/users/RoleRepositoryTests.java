package com.forcy.chatapp.users;

import com.forcy.chatapp.entity.Role;
import com.forcy.chatapp.user.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class RoleRepositoryTests {

    @Autowired private RoleRepository roleRepository;

    @Test
    public void testCreateFirstRole(){
        Role role = new Role();
        role.setName("ROLE_USER");
        role.setDescription("Regular user who can send and receive messages");
        roleRepository.save(role);
    }

    @Test
    public void testCreateMultipleRoles(){
        Role admin = Role.builder().name("ROLE_ADMIN").description("Admin who can manage users and rooms").build();
        Role moderator = Role.builder().name("ROLE_MODERATOR").description("Moderator who can delete messages or mute users").build();
        Role guest = Role.builder().name("ROLE_GUEST").description("Guest who can only view public chats").build();

        roleRepository.saveAll(List.of(admin, moderator, guest));

    }
}
