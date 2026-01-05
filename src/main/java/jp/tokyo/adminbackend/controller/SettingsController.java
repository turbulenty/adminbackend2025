package jp.tokyo.adminbackend.controller;

import jp.tokyo.adminbackend.entity.Settings;
import jp.tokyo.adminbackend.repository.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    @Autowired
    private SettingsRepository settingsRepository;

    // 获取设置
    @GetMapping
    public ResponseEntity<Settings> getSettings() {
        // 查找所有设置，如果没有则创建默认设置
        List<Settings> allSettings = settingsRepository.findAll();

        Settings settings;
        if (allSettings.isEmpty()) {
            // 创建默认设置（不设置ID，让数据库自动生成）
            settings = new Settings();
            settings = settingsRepository.save(settings);
        } else {
            // 返回第一条记录
            settings = allSettings.get(0);
        }

        return ResponseEntity.ok(settings);
    }

    // 保存设置
    @PutMapping
    public ResponseEntity<Map<String, Object>> saveSettings(@RequestBody Settings newSettings) {
        // 查找现有设置
        List<Settings> allSettings = settingsRepository.findAll();

        Settings settings;
        if (allSettings.isEmpty()) {
            // 如果没有记录，创建新的（ID自动生成）
            settings = new Settings();
        } else {
            // 使用第一条记录（已有ID）
            settings = allSettings.get(0);
        }

        // 更新字段
        settings.setSystemName(newSettings.getSystemName());
        settings.setApiEndpoint(newSettings.getApiEndpoint());
        settings.setRefreshInterval(newSettings.getRefreshInterval());
        settings.setEnableNotifications(newSettings.getEnableNotifications());
        settings.setEnableAutoRefresh(newSettings.getEnableAutoRefresh());

        // 保存（如果settings有ID就是UPDATE，没有ID就是INSERT）
        Settings savedSettings = settingsRepository.save(settings);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "设置已成功保存");
        response.put("data", savedSettings);

        return ResponseEntity.ok(response);
    }
}
