package com.forcy.chatapp.user;

import com.forcy.chatapp.auth.AuthUserDTO;
import com.forcy.chatapp.auth.RoleDTO;
import com.forcy.chatapp.entity.Role;
import com.forcy.chatapp.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(AuthUserDTO dto) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPhoneNumber(dto.getPhoneNumber());

        Set<Role> roles = new HashSet<>();

        for (RoleDTO roleDTO : dto.getRoles()) {
            Role role = (Role) roleRepository.findByName(roleDTO.getName())
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleDTO.getName()));
            roles.add(role);
        }
        user.setRoles(roles);
        return userRepository.save(user);
    }

    public List<UserDTO> getAllUserExcept(String username) {
        return userRepository.findAll().stream()
                .filter(user -> !user.getUsername().equals(username))
                .map(user -> new UserDTO(user.getId(), user.getUsername()))
                .collect(Collectors.toList());
    }

    public User getByUsername(String username) {
        return userRepository.getUserByUsername(username);
    }

}
