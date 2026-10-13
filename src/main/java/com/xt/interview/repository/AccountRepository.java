package com.xt.interview.repository;

import com.xt.interview.entity.Account;
import com.xt.interview.dto.AccountPageQuery;
import com.xt.interview.dto.AccountPageView;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query(value = """
            SELECT a.id, a.username, a.name, a.gender, a.phone,
                   a.role_level AS roleLevel, a.status, a.is_super AS isSuper,
                   a.department_id AS departmentId, d.name AS departmentName,
                   a.create_time AS createTime, a.update_time AS updateTime
            FROM sys_account a
            LEFT JOIN sys_department d ON d.id = a.department_id
            WHERE a.deleted = 0
              AND (:#{#query.usernamePattern} IS NULL OR a.username LIKE :#{#query.usernamePattern} ESCAPE '!')
              AND (:#{#query.status} IS NULL OR a.status = :#{#query.status})
              AND (:#{#query.roleLevel} IS NULL OR a.role_level = :#{#query.roleLevel})
            ORDER BY a.id DESC
            """, countQuery = """
            SELECT COUNT(*) FROM sys_account a
            WHERE a.deleted = 0
              AND (:#{#query.usernamePattern} IS NULL OR a.username LIKE :#{#query.usernamePattern} ESCAPE '!')
              AND (:#{#query.status} IS NULL OR a.status = :#{#query.status})
              AND (:#{#query.roleLevel} IS NULL OR a.role_level = :#{#query.roleLevel})
            """, nativeQuery = true)
    Page<AccountPageView> findPage(@Param("query") AccountPageQuery query, Pageable pageable);

    @EntityGraph(attributePaths = "department")
    List<Account> findByDeleted(Integer deleted);
    @EntityGraph(attributePaths = "department")
    Optional<Account> findByIdAndDeleted(Long id, Integer deleted);
    @EntityGraph(attributePaths = "department")
    Optional<Account> findByUsernameAndDeleted(String username, Integer deleted);
    @EntityGraph(attributePaths = "department")
    Optional<Account> findByUsernameAndPasswordAndDeleted(String username, String password, Integer deleted);
    boolean existsByUsernameAndDeleted(String username, Integer deleted);
}
