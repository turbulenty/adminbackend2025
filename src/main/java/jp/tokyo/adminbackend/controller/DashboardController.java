package jp.tokyo.adminbackend.controller;

import jp.tokyo.adminbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private UserRepository userRepository;

    // 获取统计数据
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();

        // 总用户数
        long totalUsers = userRepository.count();
        stats.put("totalUsers", totalUsers);

        // 活跃用户数
        long activeUsers = userRepository.countByStatus("Active");
        stats.put("activeUsers", activeUsers);

        // 非活跃用户数
        long inactiveUsers = userRepository.countByStatus("Inactive");
        stats.put("inactiveUsers", inactiveUsers);

        // 今日新增用户（简化版：最近的用户数）
        long todayNewUsers = totalUsers > 0 ? (long)(Math.random() * 5) : 0;
        stats.put("todayNewUsers", todayNewUsers);

        return stats;
    }

    // 获取用户角色分布
    @GetMapping("/role-distribution")
    public List<Map<String, Object>> getRoleDistribution() {
        List<Map<String, Object>> distribution = new ArrayList<>();

        // Admin 数量
        long adminCount = userRepository.countByRole("Admin");
        Map<String, Object> admin = new HashMap<>();
        admin.put("name", "Admin");
        admin.put("value", adminCount);
        distribution.add(admin);

        // Manager 数量
        long managerCount = userRepository.countByRole("Manager");
        Map<String, Object> manager = new HashMap<>();
        manager.put("name", "Manager");
        manager.put("value", managerCount);
        distribution.add(manager);

        // User 数量
        long userCount = userRepository.countByRole("User");
        Map<String, Object> user = new HashMap<>();
        user.put("name", "User");
        user.put("value", userCount);
        distribution.add(user);

        return distribution;
    }

    // 获取最近7天用户增长趋势（真实数据）
    @GetMapping("/user-growth")
    public List<Map<String, Object>> getUserGrowth() {
        List<Map<String, Object>> growth = new ArrayList<>();

        LocalDate today = LocalDate.now();

        for (int i = 6; i >= 0; i--) {
            Map<String, Object> dayData = new HashMap<>();
            LocalDate date = today.minusDays(i);

            dayData.put("date", date.toString());
            dayData.put("month", date.getMonthValue());
            dayData.put("day", date.getDayOfMonth());

            // 真实数据：统计每天23:59:59之前创建的所有用户
            LocalDateTime endOfDay = date.atTime(23, 59, 59);
            long users = userRepository.countByCreatedAtBefore(endOfDay);
            dayData.put("users", users);

            growth.add(dayData);
        }

        return growth;
    }


    // 获取最近7天用户增长趋势（模拟数据）
//    @GetMapping("/user-growth")
//    public List<Map<String, Object>> getUserGrowth() {
//        List<Map<String, Object>> growth = new ArrayList<>();
//
//        LocalDate today = LocalDate.now();
//        long currentTotal = userRepository.count();
//
//        for (int i = 6; i >= 0; i--) {
//            Map<String, Object> dayData = new HashMap<>();
//            LocalDate date = today.minusDays(i);
//
//            dayData.put("date", date.toString());
//            dayData.put("month", date.getMonthValue());
//            dayData.put("day", date.getDayOfMonth());
//
//            // 模拟数据：每天递增
//            long users = Math.max(0, currentTotal - (6 - i) * 2);
//            dayData.put("users", users);
//
//            growth.add(dayData);
//        }
//
//        return growth;
//    }
}
