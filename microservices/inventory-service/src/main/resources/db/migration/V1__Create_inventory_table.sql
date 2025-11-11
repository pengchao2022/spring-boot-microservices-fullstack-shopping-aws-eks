-- 创建库存表
CREATE TABLE IF NOT EXISTS inventory (
    id BIGSERIAL PRIMARY KEY,
    variant_id BIGINT NOT NULL,
    current_stock INTEGER NOT NULL DEFAULT 0,
    reserved_stock INTEGER NOT NULL DEFAULT 0,
    available_stock INTEGER NOT NULL DEFAULT 0,
    minimum_stock_level INTEGER NOT NULL DEFAULT 5,
    maximum_stock_level INTEGER NOT NULL DEFAULT 1000,
    reorder_point INTEGER NOT NULL DEFAULT 10,
    total_sold INTEGER NOT NULL DEFAULT 0,
    total_returned INTEGER NOT NULL DEFAULT 0,
    last_restocked_date TIMESTAMP,
    last_sold_date TIMESTAMP,
    is_tracked BOOLEAN NOT NULL DEFAULT true,
    status VARCHAR(20) NOT NULL DEFAULT 'IN_STOCK',
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 添加检查约束
ALTER TABLE inventory ADD CONSTRAINT inventory_status_check 
    CHECK (status IN ('IN_STOCK', 'LOW_STOCK', 'OUT_OF_STOCK', 'DISCONTINUED'));

-- 添加唯一约束
ALTER TABLE inventory ADD CONSTRAINT inventory_variant_id_key UNIQUE (variant_id);
