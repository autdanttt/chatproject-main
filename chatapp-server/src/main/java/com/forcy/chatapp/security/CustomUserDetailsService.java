package com.forcy.chatapp.security;

import com.forcy.chatapp.entity.User;
import com.forcy.chatapp.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> findByEmail = userRepository.findByEmail(username);

        if(!findByEmail.isPresent()) {
            throw new UsernameNotFoundException("No user found with email: " + findByEmail);
        }
        return new CustomUserDetails(findByEmail.get());
    }
}
