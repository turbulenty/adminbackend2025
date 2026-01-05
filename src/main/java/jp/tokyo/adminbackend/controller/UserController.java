package jp.tokyo.adminbackend.controller;

import jp.tokyo.adminbackend.entity.User;
import jp.tokyo.adminbackend.entity.Notification;
import jp.tokyo.adminbackend.repository.UserRepository;
import jp.tokyo.adminbackend.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.HashMap;
import java.util.Map;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;  // 添加通知仓库

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

//    @GetMapping
//    public List<User> getAllUsers() {
////        return userRepository.findAll();
////        查询时过滤掉已删除用户
//          return userRepository.findByDeletedFalse();
//    }

    @PostMapping
    //（新增用户）
    public ResponseEntity<User> createUser(@RequestBody User user) {
        // 加密密码
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(passwordEncoder.encode("123456")); // 默认密码
        }

        User savedUser = userRepository.save(user);

        // 创建通知
        Notification notification = new Notification();
        notification.setTitle("新用户注册");
        notification.setContent("用户\"" + savedUser.getName() + "\"已成功注册");
        notification.setTime(formatTime(LocalDateTime.now()));
        notification.setRead(false);
        notificationRepository.save(notification);

        return ResponseEntity.ok(savedUser);
    }

    @PutMapping("/{id}")
    //（更改用户信息）
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setName(userDetails.getName());
            user.setRole(userDetails.getRole());
            user.setStatus(userDetails.getStatus());

            // 如果提供了新密码，则更新密码
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            }

            User updatedUser = userRepository.save(user);

            // 1.2 修改用户信息也要通知
            Notification notification = new Notification();
            notification.setTitle("用户信息更新");
            notification.setContent("用户\"" + user.getName() + "\"的信息已更新");
            notification.setTime(formatTime(LocalDateTime.now()));
            notification.setRead(false);
            notificationRepository.save(notification);

            return ResponseEntity.ok(updatedUser);
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    //（删除用户）
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();  // 1.2 获取用户信息

            // 创建通知
            Notification notification = new Notification();
            notification.setTitle("用户删除");
            notification.setContent("用户\"" + user.getName() + "\"已被删除");
            notification.setTime(formatTime(LocalDateTime.now()));
            notification.setRead(false);
            notificationRepository.save(notification);

//   硬删除  userRepository.deleteById(id);
            user.setDeleted(true);
            user.setDeletedAt(LocalDateTime.now());
            userRepository.save(user);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.notFound().build();
    }

    // 格式化时间为相对时间
    private String formatTime(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.Duration.between(dateTime, now).toMinutes();

        if (minutes < 1) {
            return "刚刚";
        } else if (minutes < 60) {
            return minutes + "分钟前";
        } else if (minutes < 1440) {
            return (minutes / 60) + "小时前";
        } else {
            return (minutes / 1440) + "天前";
        }
    }
//    分页、搜索
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findByDeletedFalse(pageable);
        if (search != null && !search.trim().isEmpty()) {
            // 有搜索词：按姓名或邮箱搜索
            userPage = userRepository.findByDeletedFalseAndNameContainingOrEmailContaining(
                    search, search, pageable
            );
        } else {
            // 无搜索词：查询所有未删除用户
            userPage = userRepository.findByDeletedFalse(pageable);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("users", userPage.getContent());
        response.put("currentPage", userPage.getNumber());
        response.put("totalPages", userPage.getTotalPages());
        response.put("totalItems", userPage.getTotalElements());

        return ResponseEntity.ok(response);
    }

    // 获取当前登录用户信息
    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getCurrentUser(
            @RequestParam String email
    ) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("role", user.getRole());

        return ResponseEntity.ok(response);
    }


}
