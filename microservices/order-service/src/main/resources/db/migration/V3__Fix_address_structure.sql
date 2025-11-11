-- V3__Fix_address_structure.sql

-- 1. 添加所有缺失的配送地址字段（只添加实体类需要但数据库没有的）
ALTER TABLE orders 
ADD COLUMN shipping_name VARCHAR(50),
ADD COLUMN shipping_province VARCHAR(50),
ADD COLUMN shipping_district VARCHAR(50),
ADD COLUMN shipping_detail_address VARCHAR(255);

-- 2. 将现有的 first_name 和 last_name 合并到新的 name 字段
UPDATE orders SET shipping_name = 
  CASE 
    WHEN shipping_first_name IS NOT NULL AND shipping_last_name IS NOT NULL THEN shipping_first_name || ' ' || shipping_last_name
    WHEN shipping_first_name IS NOT NULL THEN shipping_first_name
    WHEN shipping_last_name IS NOT NULL THEN shipping_last_name
    ELSE NULL
  END;

-- 3. 添加所有账单地址字段
ALTER TABLE orders 
ADD COLUMN billing_name VARCHAR(50),
ADD COLUMN billing_email VARCHAR(100),
ADD COLUMN billing_phone VARCHAR(20),
ADD COLUMN billing_company VARCHAR(100),
ADD COLUMN billing_province VARCHAR(50),
ADD COLUMN billing_city VARCHAR(50),
ADD COLUMN billing_district VARCHAR(50),
ADD COLUMN billing_detail_address VARCHAR(255),
ADD COLUMN billing_postal_code VARCHAR(20);

-- 4. 只删除确实存在的旧配送地址字段
ALTER TABLE orders 
DROP COLUMN IF EXISTS shipping_first_name,
DROP COLUMN IF EXISTS shipping_last_name,
DROP COLUMN IF EXISTS shipping_state,
DROP COLUMN IF EXISTS shipping_country,
DROP COLUMN IF EXISTS shipping_address_line1,
DROP COLUMN IF EXISTS shipping_address_line2;