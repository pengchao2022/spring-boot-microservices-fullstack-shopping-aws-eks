package com.ecommerce.user.repository;

import com.ecommerce.user.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    
    Optional<UserProfile> findByUserId(Long userId);
    
    boolean existsByEmail(String email);
    
    Optional<UserProfile> findByEmail(String email);
}