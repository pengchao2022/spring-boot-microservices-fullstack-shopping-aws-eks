-- V5__Add_apple_specific_fields.sql

-- 为现有产品表添加苹果专用字段
ALTER TABLE product ADD COLUMN IF NOT EXISTS sweetness_level INT;
ALTER TABLE product ADD COLUMN IF NOT EXISTS crunchiness_level INT;
ALTER TABLE product ADD COLUMN IF NOT EXISTS apple_variety VARCHAR(100);
ALTER TABLE product ADD COLUMN IF NOT EXISTS harvest_season VARCHAR(100);

-- 添加约束
ALTER TABLE product ADD CONSTRAINT chk_sweetness_level CHECK (sweetness_level IS NULL OR (sweetness_level >= 1 AND sweetness_level <= 5));
ALTER TABLE product ADD CONSTRAINT chk_crunchiness_level CHECK (crunchiness_level IS NULL OR (crunchiness_level >= 1 AND crunchiness_level <= 5));

-- 添加注释
COMMENT ON COLUMN product.sweetness_level IS '甜度等级：1-5星（苹果专用）';
COMMENT ON COLUMN product.crunchiness_level IS '脆度等级：1-5星（苹果专用）';
COMMENT ON COLUMN product.apple_variety IS '苹果品种（苹果专用）';
COMMENT ON COLUMN product.harvest_season IS '采收季节（苹果专用）';