-- V7__Add_kiwi_specific_fields.sql

-- 为现有产品表添加猕猴桃专用字段
ALTER TABLE product ADD COLUMN IF NOT EXISTS acidity_level INT;
ALTER TABLE product ADD COLUMN IF NOT EXISTS kiwi_variety VARCHAR(100);
ALTER TABLE product ADD COLUMN IF NOT EXISTS vitamin_c_content VARCHAR(50);
ALTER TABLE product ADD COLUMN IF NOT EXISTS skin_type VARCHAR(20);

-- 添加约束
ALTER TABLE product ADD CONSTRAINT chk_acidity_level CHECK (acidity_level IS NULL OR (acidity_level >= 1 AND acidity_level <= 5));
ALTER TABLE product ADD CONSTRAINT chk_skin_type CHECK (skin_type IS NULL OR skin_type IN ('HAIRY', 'SMOOTH', 'THIN'));

-- 添加注释
COMMENT ON COLUMN product.acidity_level IS '酸度等级：1-5星（猕猴桃专用）';
COMMENT ON COLUMN product.kiwi_variety IS '猕猴桃品种（猕猴桃专用）';
COMMENT ON COLUMN product.vitamin_c_content IS '维生素C含量（猕猴桃专用）';
COMMENT ON COLUMN product.skin_type IS '果皮类型：HAIRY-有毛, SMOOTH-光滑, THIN-薄皮（猕猴桃专用）';