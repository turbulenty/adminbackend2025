package jp.tokyo.adminbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/*安全配置 - 开发阶段临时全部放行*/
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 关闭 CSRF
                .csrf(csrf -> csrf.disable())

                // 所有请求都允许访问（开发阶段）
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )

                // 关闭表单登录
                .formLogin(form -> form.disable())

                // 关闭 http basic
                .httpBasic(basic -> basic.disable());

        return http.build();
    }

    // 添加这个Bean - 用于密码加密
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}



//package jp.tokyo.adminbackend.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
///*安全配置*/
//@Configuration
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                // 前后端分离：先关掉 CSRF
//                .csrf(csrf -> csrf.disable())
//
//                // 接口权限规则
//                .authorizeHttpRequests(auth -> auth
//                        // 所有 /api/ 开头的接口都放行（包括健康检查和用户管理）
//                        .requestMatchers("/api/**").permitAll()
//                        // 其她接口先全部需要登录
//                        .anyRequest().authenticated()
//                )
//
//                // 关闭默认表单登录页
//                .formLogin(form -> form.disable())
//                // 关闭默认 http basic 弹窗
//                .httpBasic(basic -> basic.disable());
//
//        return http.build();
//    }
//}
