package com.ecommerce.product.model.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CategoryDTO {
    private Long id;
    private String name;
    private String englishName;
    private String description;
    private String imageUrl;
    private String iconUrl;
    private String type;
    private Long parentId;
    private String parentName;
    private Integer level;
    private Integer sortOrder;
    private Boolean isActive;
    private Boolean isShowInMenu;
    private Integer productCount;
    private List<CategoryDTO> children;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
