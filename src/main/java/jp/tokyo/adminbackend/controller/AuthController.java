package jp.tokyo.adminbackend.controller;

import jp.tokyo.adminbackend.entity.User;
import jp.tokyo.adminbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jp.tokyo.adminbackend.entity.Notification;
import jp.tokyo.adminbackend.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;  // ← 添加
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private NotificationRepository notificationRepository;

    // 注册接口
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> registerData) {
        Map<String, Object> response = new HashMap<>();

        try {
            String name = registerData.get("name");
            String email = registerData.get("email");
            String password = registerData.get("password");

            // 检查邮箱是否已存在
            if (userRepository.findByEmail(email) != null) {
                response.put("success", false);
                response.put("message", "该邮箱已被注册");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // 创建新用户
            User newUser = new User();
            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setPassword(passwordEncoder.encode(password)); // 加密密码
            newUser.setRole("User"); // 默认角色
            newUser.setStatus("Active");

            userRepository.save(newUser);

            // 1.1创建通知
            Notification notification = new Notification();
            notification.setTitle("新用户注册");
            notification.setContent("用户\"" + name + "\"已成功注册");
            notification.setTime(formatTime(LocalDateTime.now()));  // ← 1.3 测试注册
            notification.setRead(false);
            notificationRepository.save(notification);

            response.put("success", true);
            response.put("message", "注册成功");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "注册失败：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 登录接口
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        Map<String, Object> response = new HashMap<>();

        try {
            String email = loginData.get("email");
            String password = loginData.get("password");

            // 查找用户
            User user = userRepository.findByEmail(email);

            if (user == null) {
                response.put("success", false);
                response.put("message", "用户不存在");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // 验证密码
            if (!passwordEncoder.matches(password, user.getPassword())) {
                response.put("success", false);
                response.put("message", "密码错误");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // 1.3新增：检查账户是否已被删除
            if (user.getDeleted() != null && user.getDeleted()) {
                response.put("success", false);
                response.put("message", "账户已被删除");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            // 1.3新增结束

            // 检查账户状态
            if ("Inactive".equals(user.getStatus())) {
                response.put("success", false);
                response.put("message", "账户已被禁用");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            // 登录成功
            response.put("success", true);
            response.put("message", "登录成功");
            response.put("user", Map.of(
                    "id", user.getId(),
                    "name", user.getName(),
                    "email", user.getEmail(),
                    "role", user.getRole()
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "登录失败：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 修改密码接口
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody Map<String, String> passwordData) {
        Map<String, Object> response = new HashMap<>();

        try {
            String email = passwordData.get("email");
            String currentPassword = passwordData.get("currentPassword");
            String newPassword = passwordData.get("newPassword");

            // 查找用户
            User user = userRepository.findByEmail(email);

            if (user == null) {
                response.put("success", false);
                response.put("message", "用户不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // 验证当前密码
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                response.put("success", false);
                response.put("message", "当前密码错误");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // 更新密码
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            response.put("success", true);
            response.put("message", "密码修改成功");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "修改密码失败：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private String formatTime(LocalDateTime dateTime) {
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }


    // 临时接口：为没有密码的用户设置默认密码
//    @PostMapping("/init-passwords")
//    public ResponseEntity<Map<String, Object>> initPasswords() {
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            List<User> allUsers = userRepository.findAll();
//            int updatedCount = 0;
//
//            for (User user : allUsers) {
//                // 如果密码为空或null，设置默认密码
//                if (user.getPassword() == null || user.getPassword().isEmpty()) {
//                    String rawPassword = "123456";  // 默认密码
//                    user.setPassword(passwordEncoder.encode(rawPassword));
//                    userRepository.save(user);
//                    updatedCount++;
//                }
//            }
//
//            response.put("success", true);
//            response.put("message", "成功为 " + updatedCount + " 个用户初始化密码");
//            response.put("defaultPassword", "123456");
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            response.put("success", false);
//            response.put("message", "初始化失败：" + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }


}
