package jp.tokyo.adminbackend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "settings")
public class Settings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "system_name", nullable = false)
    private String systemName;

    @Column(name = "api_endpoint", nullable = false)
    private String apiEndpoint;

    @Column(name = "refresh_interval", nullable = false)
    private Integer refreshInterval;

    @Column(name = "enable_notifications", nullable = false)
    private Boolean enableNotifications;

    @Column(name = "enable_auto_refresh", nullable = false)
    private Boolean enableAutoRefresh;

    // 构造函数
    public Settings() {
        // 默认值
        this.systemName = "Health Monitoring System";
        this.apiEndpoint = "http://localhost:8080";
        this.refreshInterval = 30;
        this.enableNotifications = true;
        this.enableAutoRefresh = true;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSystemName() { return systemName; }
    public void setSystemName(String systemName) { this.systemName = systemName; }

    public String getApiEndpoint() { return apiEndpoint; }
    public void setApiEndpoint(String apiEndpoint) { this.apiEndpoint = apiEndpoint; }

    public Integer getRefreshInterval() { return refreshInterval; }
    public void setRefreshInterval(Integer refreshInterval) { this.refreshInterval = refreshInterval; }

    public Boolean getEnableNotifications() { return enableNotifications; }
    public void setEnableNotifications(Boolean enableNotifications) { this.enableNotifications = enableNotifications; }

    public Boolean getEnableAutoRefresh() { return enableAutoRefresh; }
    public void setEnableAutoRefresh(Boolean enableAutoRefresh) { this.enableAutoRefresh = enableAutoRefresh; }
}
