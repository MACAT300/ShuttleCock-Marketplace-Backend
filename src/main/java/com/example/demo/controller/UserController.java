package com.example.demo.controller;

import com.example.demo.dto.ChangePasswordRequest;
import com.example.demo.dto.UpdateNameRequest;
import com.example.demo.model.Image;
import com.example.demo.model.User;
import com.example.demo.service.AvatarService;
import com.example.demo.service.ImageCacheService;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class UserController {

    private final UserService userService;
    private final AvatarService avatarService; // 记得在constructor里也加上这个依赖
    public UserController(UserService userService, AvatarService avatarService){
        this.userService = userService;
        this.avatarService = avatarService;
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers(){
        var currentProds = userService.getUsers();
        return ResponseEntity.ok(currentProds);
    }

    // GET by id
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable int id) {
        User user = userService.getUserById(id);
        user.setPassword(null); // 永远不把密码哈希传回前端
        return ResponseEntity.ok(user);
    }

    // 改名字
    @PutMapping("/users/{id}/name")
    public ResponseEntity<?> updateName(
            @PathVariable int id,
            @Valid @RequestBody UpdateNameRequest request) {
        try {
            User updated = userService.updateName(id, request.getName());
            updated.setPassword(null);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // 改密码
    @PutMapping("/users/{id}/password")
    public ResponseEntity<?> changePassword(
            @PathVariable int id,
            @Valid @RequestBody ChangePasswordRequest request) {
        try {
            userService.changePassword(id, request.getCurrentPassword(), request.getNewPassword());
            return ResponseEntity.ok(Map.of("message", "Password updated"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    // DELETE
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        boolean deleted = userService.deleteUser(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }



    // 上传/更换头像。前端要用 multipart/form-data 传一个叫 "file" 的字段
    @PostMapping(value = "/users/{id}/avatar", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadAvatar(
            @PathVariable int id,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {

        try {
            String avatarUrl = avatarService.saveAvatar(id, file);
            User updated = userService.updateAvatarUrl(id, avatarUrl);
            updated.setPassword(null);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}