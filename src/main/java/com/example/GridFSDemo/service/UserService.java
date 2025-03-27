package com.example.GridFSDemo.service;

import com.example.GridFSDemo.model.User;
import com.example.GridFSDemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void registerNewUser(User user) {
        // Encode the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set default role (USER)
        Set<String> roles = new HashSet<>();
        roles.add("USER");
        user.setRoles(roles);

        // Save the user
        userRepository.save(user);
    }
}