package com.ecommerce.user.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_profiles")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "email", unique = true)
    private String email;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;
    
    @Column(name = "birthday")
    private LocalDate birthday;
    
    @Column(name = "personal_signature")
    private String personalSignature;
    
    @Column(name = "wechat_id")
    private String wechatId;
    
    @Column(name = "qq_number")
    private String qqNumber;
    
    @Column(name = "location_province")
    private String locationProvince;
    
    @Column(name = "location_city")
    private String locationCity;
    
    @Column(name = "location_district")
    private String locationDistrict;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 构造函数
    public UserProfile() {}
    
    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }
    
    public LocalDate getBirthday() { return birthday; }
    public void setBirthday(LocalDate birthday) { this.birthday = birthday; }
    
    public String getPersonalSignature() { return personalSignature; }
    public void setPersonalSignature(String personalSignature) { this.personalSignature = personalSignature; }
    
    public String getWechatId() { return wechatId; }
    public void setWechatId(String wechatId) { this.wechatId = wechatId; }
    
    public String getQqNumber() { return qqNumber; }
    public void setQqNumber(String qqNumber) { this.qqNumber = qqNumber; }
    
    public String getLocationProvince() { return locationProvince; }
    public void setLocationProvince(String locationProvince) { this.locationProvince = locationProvince; }
    
    public String getLocationCity() { return locationCity; }
    public void setLocationCity(String locationCity) { this.locationCity = locationCity; }
    
    public String getLocationDistrict() { return locationDistrict; }
    public void setLocationDistrict(String locationDistrict) { this.locationDistrict = locationDistrict; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // 生命周期回调
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

enum Gender {
    MALE, FEMALE, OTHER, UNKNOWN
}