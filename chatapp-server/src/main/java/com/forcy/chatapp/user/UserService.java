package com.forcy.chatapp.user;

import com.forcy.chatapp.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService{


    @Autowired
    private UserRepository userRepository;

    public User registerUser(String username){
        if(userRepository.findByUsername(username).isPresent()){
            throw new RuntimeException("Username already exists");
        }
        User user = new User();
        user.setUsername(username);

        return userRepository.save(user);
    }

    public User getByUsername(String username){
        return userRepository.getUserByUsername(username);
    }
}
