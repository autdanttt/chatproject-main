package com.forcy.chatapp.user;

import com.forcy.chatapp.auth.*;
import com.forcy.chatapp.entity.Role;
import com.forcy.chatapp.entity.User;
import com.forcy.chatapp.media.AssetService;
import com.forcy.chatapp.security.jwt.JwtUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService{

    @Autowired
    private AssetService assetService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtility jwtUtility;

    public void registerUser(UserRegisterDTO dto, MultipartFile multipartFile){
        log.info("Registering user");
        if(userRepository.findByEmail(dto.getEmail()).isPresent()){
            throw new UserAlreadyExistsException("User already exists with email: " + dto.getEmail());
        }
        String avatar = "";
        if (multipartFile != null) {
            log.info("Uploading avatar");
            avatar = assetService.uploadToCloudinary(multipartFile, "avatar");
        }
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setFullName(dto.getFullName());
        user.setCreateAt(new Date());
        if(!"".equals(avatar)){
            user.setAvatarUrl(avatar);
        }else {
             user.setAvatarUrl("https://res.cloudinary.com/dm8tfyppk/image/upload/v1751297593/avatar/default.jpg");
        }

        Role role = (Role) roleRepository.findByName("ROLE_USER")
                .orElseThrow(()-> new RoleNotFoundException("Role not found"));

        user.setRoles(Set.of(role));
        User savedUser = userRepository.save(user);

        String token = jwtUtility.generateEmailVerificationToken(savedUser);

        emailService.sendVerificationEmail2(savedUser.getEmail(), token);

//        AuthUserDTO authUserDTO = new AuthUserDTO();
//        authUserDTO.setEmail(savedUser.getEmail());
//        authUserDTO.setFullName(savedUser.getFullName());
//        authUserDTO.setAvatarUrl(savedUser.getAvatarUrl());
//        Set<RoleDTO> roles = new HashSet<>();
//        for (Role savedRole : savedUser.getRoles()){
//            RoleDTO roleDTO = new RoleDTO();
//            roleDTO.setId(savedRole.getId());
//            roleDTO.setName(savedRole.getName());
//            roleDTO.setDescription(savedRole.getDescription());
//            roles.add(roleDTO);
//        }
//        authUserDTO.setRoles(roles);
//        authUserDTO.setId(savedUser.getId());
//        return authUserDTO;
    }
    public String updateAvatar(Long id, MultipartFile file) {
        User user = userRepository.findById(id).orElseThrow(()-> new UserNotFoundException("User not found: " + id));

        String avatar = "";
        if(file != null){
            assetService.deleteAvatarIfNotDefault(user.getAvatarUrl());
            avatar = assetService.uploadToCloudinary(file, "avatar");
        }

        user.setUpdateAt(new Date());
        user.setAvatarUrl(avatar);
        User savedUser = userRepository.save(user);

        return savedUser.getAvatarUrl();
    }

    public User getByEmail(String email){
        return userRepository.getUserByEmail(email);
    }

    public List<UserDTO> getAllUserExcept(Long userId) {
        return userRepository.findAll().stream()
                .filter(user -> !user.getId().equals(userId) && user.isVerified())
                .map(user -> new UserDTO(user.getId(), user.getFullName(), user.getAvatarUrl()))
                .collect(Collectors.toList());
    }
}
