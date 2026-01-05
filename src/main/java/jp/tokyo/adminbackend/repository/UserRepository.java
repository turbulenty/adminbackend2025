package jp.tokyo.adminbackend.repository;

import jp.tokyo.adminbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 根据状态统计用户数
    long countByStatus(String status);

    // 根据角色统计用户数
    long countByRole(String role);

    // 12.28 新增：统计指定日期之前创建的用户数量
    long countByCreatedAtBefore(LocalDateTime dateTime);

    // 1.1 添加查找邮箱的方法（怎么1.2 没了...）
    User findByEmail(String email);
    //    1.4 分页、搜索
    Page<User> findByDeletedFalse(Pageable pageable);
    Page<User> findByDeletedFalseAndNameContainingOrEmailContaining(
            String name, String email, Pageable pageable
    );


    // 1.3 软删除
    List<User> findByDeletedFalse();  // ← 加这行


}
