package com.ecommerce.user.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_addresses")
public class UserAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "recipient_name")
    private String recipientName;
    
    @Column(name = "phone")
    private String phone;
    
    @Column(name = "country_code")
    private String countryCode = "+86";
    
    @Column(name = "province")
    private String province;
    
    @Column(name = "city")
    private String city;
    
    @Column(name = "district")
    private String district;
    
    @Column(name = "detail_address")
    private String detailAddress;
    
    @Column(name = "postal_code")
    private String postalCode;
    
    @Column(name = "is_default")
    private Boolean isDefault = false;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "address_tag")
    private AddressTag addressTag;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AddressStatus status = AddressStatus.ACTIVE;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 构造函数
    public UserAddress() {}
    
    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
    
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    
    public String getDetailAddress() { return detailAddress; }
    public void setDetailAddress(String detailAddress) { this.detailAddress = detailAddress; }
    
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    
    public Boolean getIsDefault() { return isDefault; }
    public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }
    
    public AddressTag getAddressTag() { return addressTag; }
    public void setAddressTag(AddressTag addressTag) { this.addressTag = addressTag; }
    
    public AddressStatus getStatus() { return status; }
    public void setStatus(AddressStatus status) { this.status = status; }
    
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
    
    // 便捷方法
    public String getFullAddress() {
        return province + city + district + detailAddress;
    }
}

enum AddressTag {
    HOME, COMPANY, SCHOOL, OTHER
}

enum AddressStatus {
    ACTIVE, INACTIVE, DELETED
}