package com.example.demo.service;

import com.example.demo.dto.SignUpRequest;
import com.example.demo.model.Image;
import com.example.demo.model.User;
import com.example.demo.repo.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // GET ALL
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    // GET by ID
    public User getUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));
    }

    // ADD User with Hash Password / reject same Email
    public User addUser(SignUpRequest request){
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setGoogleAccount(false);
        return userRepository.save(user);
    }

    // 只改名字，绝对不碰密码字段（修掉了原本会误清空密码的漏洞）
    public User updateName(int id, String newName) {
        User existing = getUserById(id);
        existing.setName(newName);
        return userRepository.save(existing);
    }

    // 改密码：普通账号要先验证现在的密码；Google账号跳过验证，直接设新密码
    public User changePassword(int id, String currentPassword, String newPassword) {
        User existing = getUserById(id);

        if (!existing.isGoogleAccount()) {
            if (currentPassword == null ||
                    !passwordEncoder.matches(currentPassword, existing.getPassword())) {
                throw new IllegalArgumentException("Current password is incorrect");
            }
        }

        existing.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(existing);
    }

    // 设置/更换头像
    public User updateAvatarUrl(int id, String avatarUrl) {
        User existing = getUserById(id);
        existing.setAvatarUrl(avatarUrl);
        return userRepository.save(existing);
    }

    // DELETE
    public boolean deleteUser(int id) {
        if (!userRepository.existsById(id)) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }

    // LOGIN — verify email + raw password against stored hash
    public User login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return user;
    }

    // Google Login
    public User findOrCreateGoogleUser(String email, String name) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User user = new User();
                    user.setName(name);
                    user.setEmail(email);
                    user.setPassword(passwordEncoder.encode(java.util.UUID.randomUUID().toString()));
                    user.setGoogleAccount(true);
                    return userRepository.save(user);
                });
    }
}