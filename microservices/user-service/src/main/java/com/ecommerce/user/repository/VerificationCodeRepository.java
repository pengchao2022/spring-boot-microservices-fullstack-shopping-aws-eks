package com.ecommerce.user.repository;

import com.ecommerce.user.model.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    
    // 根据手机号和验证码查找未使用的有效验证码（向后兼容）
    Optional<VerificationCode> findByPhoneAndCodeAndUsedFalseAndExpiresAtAfter(String phone, String code, LocalDateTime now);
    
    // 根据国家区号、手机号和验证码查找未使用的有效验证码（新增）
    Optional<VerificationCode> findByCountryCodeAndPhoneAndCodeAndUsedFalseAndExpiresAtAfter(
        String countryCode, String phone, String code, LocalDateTime now);
    
    // 查找手机号最新的未使用验证码（向后兼容）
    Optional<VerificationCode> findFirstByPhoneAndUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(String phone, LocalDateTime now);
    
    // 查找国家区号和手机号最新的未使用验证码（新增）
    Optional<VerificationCode> findFirstByCountryCodeAndPhoneAndUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
        String countryCode, String phone, LocalDateTime now);
    
    // 根据手机号查找所有验证码（向后兼容）
    List<VerificationCode> findByPhoneOrderByCreatedAtDesc(String phone);
    
    // 根据国家区号和手机号查找所有验证码（新增）
    List<VerificationCode> findByCountryCodeAndPhoneOrderByCreatedAtDesc(String countryCode, String phone);
    
    // 标记验证码为已使用（向后兼容）
    @Modifying
    @Transactional
    @Query("UPDATE VerificationCode v SET v.used = true WHERE v.phone = :phone AND v.code = :code")
    void markAsUsed(@Param("phone") String phone, @Param("code") String code);
    
    // 标记验证码为已使用（支持国家区号，新增）
    @Modifying
    @Transactional
    @Query("UPDATE VerificationCode v SET v.used = true WHERE v.countryCode = :countryCode AND v.phone = :phone AND v.code = :code")
    void markAsUsed(@Param("countryCode") String countryCode, @Param("phone") String phone, @Param("code") String code);
    
    // 清理过期的验证码
    @Modifying
    @Transactional
    @Query("DELETE FROM VerificationCode v WHERE v.expiresAt < :now")
    void deleteExpiredCodes(@Param("now") LocalDateTime now);
    
    // 统计手机号在指定时间内的验证码发送次数（向后兼容）
    @Query("SELECT COUNT(v) FROM VerificationCode v WHERE v.phone = :phone AND v.createdAt >= :startTime")
    long countByPhoneAndCreatedAtAfter(@Param("phone") String phone, @Param("startTime") LocalDateTime startTime);
    
    // 统计国家区号和手机号在指定时间内的验证码发送次数（新增）
    @Query("SELECT COUNT(v) FROM VerificationCode v WHERE v.countryCode = :countryCode AND v.phone = :phone AND v.createdAt >= :startTime")
    long countByCountryCodeAndPhoneAndCreatedAtAfter(
        @Param("countryCode") String countryCode, 
        @Param("phone") String phone, 
        @Param("startTime") LocalDateTime startTime);
    
    // 根据国家区号查找验证码
    List<VerificationCode> findByCountryCode(String countryCode);
    
    // 根据国家区号和状态查找验证码
    List<VerificationCode> findByCountryCodeAndUsed(String countryCode, Boolean used);
    
    // 统计各国家区号的验证码数量
    @Query("SELECT v.countryCode, COUNT(v) FROM VerificationCode v GROUP BY v.countryCode")
    List<Object[]> countVerificationCodesByCountryCode();
    
    // 查找重复的验证码记录
    @Query("SELECT v.countryCode, v.phone, COUNT(v) as count FROM VerificationCode v GROUP BY v.countryCode, v.phone HAVING COUNT(v) > 1")
    List<Object[]> findDuplicateVerificationCodes();
    
    // 根据国家区号删除验证码（用于数据清理）
    @Modifying
    @Transactional
    @Query("DELETE FROM VerificationCode v WHERE v.countryCode = :countryCode AND v.expiresAt < :expiryTime")
    void deleteExpiredCodesByCountryCode(@Param("countryCode") String countryCode, @Param("expiryTime") LocalDateTime expiryTime);
}