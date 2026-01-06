# 全栈管理系统 - 后端

基于 Spring Boot 构建的 RESTful API 后端服务,提供用户管理、数据统计等功能。

## 技术栈

- **Java 17+** - 编程语言
- **Spring Boot 3.x** - 后端框架
- **Spring Web** - Web 应用开发
- **Spring Data JPA** - 数据持久化
- **MySQL 8.0** - 关系型数据库
- **Maven** - 项目构建工具
- **Lombok** - 简化 Java 代码

## 主要功能

### 1. 用户认证
- 用户登录
- 用户注册
- 密码加密存储
- Session 会话管理

### 2. 用户管理
- 用户 CRUD 操作
- 分页查询
- 用户搜索(按姓名/邮箱)
- 角色权限管理

### 3. 数据统计
- 用户增长趋势统计
- 角色分布统计
- 今日新增用户统计
- 活跃用户统计

### 4. 系统设置
- 系统配置管理
- 参数持久化存储

### 5. 通知功能
- 系统通知创建
- 通知查询
- 未读数量统计
- 批量标记已读

## 项目结构

```
adminbackend/
├── src/main/java/jp/tokyo/adminbackend/
│   ├── config/          # 配置类
│   │   ├── SecurityConfig.java
│   │   └── WebConfig.java
│   ├── controller/      # 控制器
│   │   ├── AuthController.java
│   │   ├── UserController.java
│   │   ├── DashboardController.java
│   │   ├── SettingsController.java
│   │   └── NotificationController.java
│   ├── entity/          # 实体类
│   │   ├── User.java
│   │   ├── Settings.java
│   │   └── Notification.java
│   ├── repository/      # 数据访问层
│   │   ├── UserRepository.java
│   │   ├── SettingsRepository.java
│   │   └── NotificationRepository.java
│   ├── service/         # 业务逻辑层
│   └── AdminBackend2025Application.java
├── src/main/resources/
│   ├── application.properties
│   └── static/
├── pom.xml
└── README.md
```

## 环境要求

- JDK 17 或更高版本
- Maven 3.6+
- MySQL 8.0+

## 安装与运行

### 1. 克隆项目

```bash
git clone <repository-url>
cd adminbackend
```

### 2. 配置数据库

创建 MySQL 数据库:

```sql
CREATE DATABASE admin_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

修改 `src/main/resources/application.properties`:

```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/admin_system?useSSL=false&serverTimezone=Asia/Tokyo
spring.datasource.username=root
spring.datasource.password=your_password

# JPA 配置
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# 服务器端口
server.port=8080
```

### 3. 安装依赖

```bash
mvn clean install
```

### 4. 运行应用

**使用 Maven:**
```bash
mvn spring-boot:run
```

**或使用 IDE (IntelliJ IDEA):**
- 打开项目
- 运行 `AdminBackend2025Application.java`

应用启动在 `http://localhost:8080`

### 5. 访问 API

API 基础路径: `http://localhost:8080/api`

## 主要依赖

```xml
<dependencies>
    <!-- Spring Boot Starter Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Spring Boot Starter Data JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- MySQL Driver -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

## API 文档

### 认证相关

#### 登录
```
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

#### 注册
```
POST /api/auth/register
Content-Type: application/json

{
  "name": "张三",
  "email": "user@example.com",
  "password": "password123"
}
```

### 用户管理

#### 获取用户列表
```
GET /api/users?page=0&size=10&search=keyword
```

#### 创建用户
```
POST /api/users
Content-Type: application/json

{
  "name": "新用户",
  "email": "new@example.com",
  "password": "password123",
  "role": "User",
  "status": "Active"
}
```

#### 更新用户
```
PUT /api/users/{id}
Content-Type: application/json

{
  "name": "更新后的名字",
  "role": "Manager"
}
```

#### 删除用户
```
DELETE /api/users/{id}
```

### 仪表盘数据

#### 获取统计数据
```
GET /api/dashboard/stats
```

#### 获取用户增长数据
```
GET /api/dashboard/user-growth
```

### 系统设置

#### 获取设置
```
GET /api/settings
```

#### 保存设置
```
POST /api/settings
Content-Type: application/json

{
  "systemName": "管理系统",
  "apiEndpoint": "http://localhost:8080",
  "refreshInterval": 30,
  "enableNotifications": true
}
```

### 通知管理

#### 获取通知列表
```
GET /api/notifications
```

#### 获取未读数量
```
GET /api/notifications/unread-count
```

#### 标记全部已读
```
PUT /api/notifications/mark-all-read
```

## 数据库表结构

### users 表
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'User',
    status VARCHAR(50) DEFAULT 'Active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### settings 表
```sql
CREATE TABLE settings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    system_name VARCHAR(255),
    api_endpoint VARCHAR(255),
    refresh_interval INT,
    enable_notifications BOOLEAN,
    enable_auto_refresh BOOLEAN
);
```

### notifications 表
```sql
CREATE TABLE notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    is_read BOOLEAN DEFAULT FALSE,
    time VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 开发规范

- 遵循 RESTful API 设计规范
- 使用统一的响应格式
- 异常统一处理
- 日志记录关键操作
- 数据库字段使用下划线命名

## 部署

### 打包应用

```bash
mvn clean package
```

生成的 JAR 文件在 `target/` 目录

### 运行 JAR

```bash
java -jar target/adminbackend-1.0.0.jar
```

## 许可证

MIT License

## 作者

开发者: CHEN YANYE
