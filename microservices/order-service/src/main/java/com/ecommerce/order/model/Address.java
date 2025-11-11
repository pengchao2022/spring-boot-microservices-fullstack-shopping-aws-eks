package com.ecommerce.order.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Address {
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "phone")
    private String phone;
    
    @Column(name = "province")
    private String province;
    
    @Column(name = "city")
    private String city;
    
    @Column(name = "district")
    private String district;
    
    @Column(name = "detail_address")
    private String detailAddress;

    // 默认构造函数
    public Address() {}

    // 简化构造函数
    public Address(String name, String phone, String province, String city, String district, String detailAddress) {
        this.name = name;
        this.phone = phone;
        this.province = province;
        this.city = city;
        this.district = district;
        this.detailAddress = detailAddress;
    }

    // getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getDetailAddress() { return detailAddress; }
    public void setDetailAddress(String detailAddress) { this.detailAddress = detailAddress; }

    // 便捷方法 - 获取完整地址
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (province != null) sb.append(province);
        if (city != null) sb.append(city);
        if (district != null) sb.append(district);
        if (detailAddress != null) sb.append(detailAddress);
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Address{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", detailAddress='" + detailAddress + '\'' +
                '}';
    }
}