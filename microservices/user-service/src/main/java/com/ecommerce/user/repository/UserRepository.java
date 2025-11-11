package com.ecommerce.user.repository;

import com.ecommerce.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // 根据手机号查找用户（兼容旧版本，使用默认国家区号）
    Optional<User> findByPhone(String phone);
    
    // 根据国家区号和手机号查找用户
    Optional<User> findByCountryCodeAndPhone(String countryCode, String phone);
    
    // 根据支付宝用户ID查找用户
    Optional<User> findByAlipayUserId(String alipayUserId);
    
    // 根据淘宝用户ID查找用户
    Optional<User> findByTaobaoUserId(String taobaoUserId);
    
    // 检查手机号是否存在（兼容旧版本，使用默认国家区号）
    boolean existsByPhone(String phone);
    
    // 检查国家区号和手机号是否存在
    boolean existsByCountryCodeAndPhone(String countryCode, String phone);
    
    // 检查支付宝用户ID是否存在
    boolean existsByAlipayUserId(String alipayUserId);
    
    // 检查淘宝用户ID是否存在
    boolean existsByTaobaoUserId(String taobaoUserId);
    
    // 根据国家区号查找用户
    List<User> findByCountryCode(String countryCode);
    
    // 根据国家区号和状态查找用户
    List<User> findByCountryCodeAndStatus(String countryCode, User.UserStatus status);
    
    // 查找所有活跃用户
    @Query("SELECT u FROM User u WHERE u.status = 'ACTIVE'")
    List<User> findAllActiveUsers();
    
    // 根据手机号模糊查询
    @Query("SELECT u FROM User u WHERE u.phone LIKE %:phone%")
    List<User> findByPhoneContaining(@Param("phone") String phone);
    
    // 根据国家区号和手机号模糊查询
    @Query("SELECT u FROM User u WHERE u.countryCode = :countryCode AND u.phone LIKE %:phone%")
    List<User> findByCountryCodeAndPhoneContaining(@Param("countryCode") String countryCode, 
                                                   @Param("phone") String phone);
    
    // 根据用户名模糊查询
    @Query("SELECT u FROM User u WHERE u.name LIKE %:name%")
    List<User> findByNameContaining(@Param("name") String name);
    
    // 根据注册来源统计用户数量
    @Query("SELECT COUNT(u) FROM User u WHERE u.registerSource = :source")
    long countByRegisterSource(@Param("source") User.RegisterSource source);
    
    // 根据国家区号和注册来源统计用户数量
    @Query("SELECT COUNT(u) FROM User u WHERE u.countryCode = :countryCode AND u.registerSource = :source")
    long countByCountryCodeAndRegisterSource(@Param("countryCode") String countryCode, 
                                             @Param("source") User.RegisterSource source);
    
    // 根据状态查找用户
    List<User> findByStatus(User.UserStatus status);
    
    // 查找最近登录的用户
    @Query("SELECT u FROM User u WHERE u.lastLoginAt IS NOT NULL ORDER BY u.lastLoginAt DESC")
    List<User> findRecentActiveUsers();
    
    // 根据国家区号查找最近登录的用户
    @Query("SELECT u FROM User u WHERE u.countryCode = :countryCode AND u.lastLoginAt IS NOT NULL ORDER BY u.lastLoginAt DESC")
    List<User> findRecentActiveUsersByCountryCode(@Param("countryCode") String countryCode);
    
    // 统计今日新增用户
    @Query("SELECT COUNT(u) FROM User u WHERE DATE(u.createdAt) = CURRENT_DATE")
    long countTodayNewUsers();
    
    // 统计今日新增用户（按国家区号）
    @Query("SELECT COUNT(u) FROM User u WHERE u.countryCode = :countryCode AND DATE(u.createdAt) = CURRENT_DATE")
    long countTodayNewUsersByCountryCode(@Param("countryCode") String countryCode);
    
    // 根据登录次数排序
    @Query("SELECT u FROM User u ORDER BY u.loginCount DESC")
    List<User> findByLoginCountDesc();
    
    // 根据国家区号和登录次数排序
    @Query("SELECT u FROM User u WHERE u.countryCode = :countryCode ORDER BY u.loginCount DESC")
    List<User> findByCountryCodeAndLoginCountDesc(@Param("countryCode") String countryCode);
    
    // 查找需要清理的非活跃用户（30天未登录）
    @Query("SELECT u FROM User u WHERE u.lastLoginAt < :cutoffDate AND u.status = 'ACTIVE'")
    List<User> findInactiveUsers(@Param("cutoffDate") java.time.LocalDateTime cutoffDate);
    
    // 查找需要清理的非活跃用户（按国家区号，30天未登录）
    @Query("SELECT u FROM User u WHERE u.countryCode = :countryCode AND u.lastLoginAt < :cutoffDate AND u.status = 'ACTIVE'")
    List<User> findInactiveUsersByCountryCode(@Param("countryCode") String countryCode, 
                                              @Param("cutoffDate") java.time.LocalDateTime cutoffDate);
    
    // 统计各国家区号的用户数量
    @Query("SELECT u.countryCode, COUNT(u) FROM User u GROUP BY u.countryCode")
    List<Object[]> countUsersByCountryCode();
    
    // 根据国家区号统计用户总数
    @Query("SELECT COUNT(u) FROM User u WHERE u.countryCode = :countryCode")
    long countByCountryCode(@Param("countryCode") String countryCode);
    
    // 根据国家区号和状态统计用户数量
    @Query("SELECT COUNT(u) FROM User u WHERE u.countryCode = :countryCode AND u.status = :status")
    long countByCountryCodeAndStatus(@Param("countryCode") String countryCode, 
                                     @Param("status") User.UserStatus status);
    
    // 查找重复的手机号（同一手机号在不同国家区号下的用户）
    @Query("SELECT u.phone, COUNT(u) as count FROM User u GROUP BY u.phone HAVING COUNT(u) > 1")
    List<Object[]> findDuplicatePhones();
    
    // 根据手机号查找所有国家区号的用户
    @Query("SELECT u FROM User u WHERE u.phone = :phone")
    List<User> findAllByPhone(@Param("phone") String phone);
    
    // 根据国家区号删除用户（用于数据清理）
    @Query("DELETE FROM User u WHERE u.countryCode = :countryCode AND u.status = 'DELETED'")
    void deleteDeletedUsersByCountryCode(@Param("countryCode") String countryCode);
}