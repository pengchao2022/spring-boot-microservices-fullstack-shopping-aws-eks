-- 创建性能优化索引
CREATE INDEX IF NOT EXISTS idx_product_category_type_active ON product(category_type, is_active, sort_order);
CREATE INDEX IF NOT EXISTS idx_product_featured_active ON product(is_featured, is_active, sort_order);
CREATE INDEX IF NOT EXISTS idx_variant_product_active ON product_variant(product_id, is_active, sort_order);
CREATE INDEX IF NOT EXISTS idx_inventory_status_tracked ON inventory(status, is_tracked);
CREATE INDEX IF NOT EXISTS idx_category_active_sort ON category(is_active, sort_order);

-- 创建视图用于常用查询
CREATE OR REPLACE VIEW vw_featured_products AS
SELECT 
    p.id,
    p.name,
    p.english_name,
    p.short_description,
    p.base_price,
    p.main_image_url,
    p.origin,
    p.is_featured,
    p.category_type,
    c.name as category_name,
    (SELECT COUNT(*) FROM product_variant pv WHERE pv.product_id = p.id AND pv.is_active = true) as variant_count
FROM product p
LEFT JOIN category c ON p.category_id = c.id
WHERE p.is_active = true AND p.is_featured = true
ORDER BY p.sort_order ASC, p.created_at DESC;

-- 创建库存预警视图
CREATE OR REPLACE VIEW vw_low_stock_alerts AS
SELECT 
    p.name as product_name,
    pv.variant_name,
    pv.sku,
    i.current_stock,
    i.available_stock,
    i.status,
    i.reorder_point,
    CASE 
        WHEN i.current_stock = 0 THEN '缺货'
        WHEN i.current_stock <= i.reorder_point THEN '需要补货'
        ELSE '库存充足'
    END as stock_status
FROM inventory i
JOIN product_variant pv ON i.variant_id = pv.id
JOIN product p ON pv.product_id = p.id
WHERE i.is_tracked = true AND (i.current_stock = 0 OR i.current_stock <= i.reorder_point)
ORDER BY i.current_stock ASC;

-- 创建产品统计视图
CREATE OR REPLACE VIEW vw_product_stats AS
SELECT 
    p.category_type,
    COUNT(*) as total_products,
    COUNT(CASE WHEN p.is_featured = true THEN 1 END) as featured_products,
    COUNT(CASE WHEN p.is_active = true THEN 1 END) as active_products,
    SUM(p.view_count) as total_views,
    SUM(p.sales_count) as total_sales,
    AVG(p.base_price) as avg_price
FROM product p
GROUP BY p.category_type;