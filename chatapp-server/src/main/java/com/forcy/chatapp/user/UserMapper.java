package com.forcy.chatapp.user;

import com.forcy.chatapp.entity.Role;
import com.forcy.chatapp.entity.User;
import com.forcy.chatapp.user.dto.UserRegisterDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RoleRepository roleRepository;

    public User dto2Entity(UserRegisterDTO dto){
        User user = modelMapper.map(dto, User.class);
        Role role = roleRepository.findByName("USER");

        user.addRole(role);
        return user;
    }

}
