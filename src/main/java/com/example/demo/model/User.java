package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String email;
    private String password;

    // Google登录建的账号 = true，普通邮箱密码注册 = false
    private boolean isGoogleAccount = false;

    // 直接存头像的可访问路径，比如 /cached-images/avatars/14.jpg
    private String avatarUrl;
}