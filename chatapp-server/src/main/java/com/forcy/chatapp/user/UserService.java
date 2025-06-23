package com.forcy.chatapp.user;

import com.forcy.chatapp.entity.User;
import com.forcy.chatapp.user.dto.UserRegisterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService{

    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserMapper userMapper;

    public void registerUser(UserRegisterDTO userDTO) {
        User user = userMapper.dto2Entity(userDTO);

        if(userRepository.findByUsername(user.getUsername()).isPresent()){
            throw new UsernameAlreadyExistsException(user.getUsername());
        }
        //Rui ro khi chua kiem tra so dien thoai la duy nhat va chua co xac thuc sdt
        encodePassword(user);
        userRepository.save(user);
    }

    public User getByUsername(String username){
        return userRepository.getUserByUsername(username);
    }

    private void encodePassword(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }
}
