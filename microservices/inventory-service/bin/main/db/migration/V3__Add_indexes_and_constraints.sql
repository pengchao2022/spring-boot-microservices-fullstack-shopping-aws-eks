-- 为 inventory 表添加索引
CREATE INDEX IF NOT EXISTS idx_inventory_variant_id ON inventory(variant_id);
CREATE INDEX IF NOT EXISTS idx_inventory_status ON inventory(status);
CREATE INDEX IF NOT EXISTS idx_inventory_available_stock ON inventory(available_stock);
CREATE INDEX IF NOT EXISTS idx_inventory_is_tracked ON inventory(is_tracked);
CREATE INDEX IF NOT EXISTS idx_inventory_status_tracked ON inventory(status, is_tracked);

-- 为 stock_reservation 表添加索引
CREATE INDEX IF NOT EXISTS idx_stock_reservation_variant_id ON stock_reservation(variant_id);
CREATE INDEX IF NOT EXISTS idx_stock_reservation_order_id ON stock_reservation(order_id);
CREATE INDEX IF NOT EXISTS idx_stock_reservation_status ON stock_reservation(status);
CREATE INDEX IF NOT EXISTS idx_stock_reservation_expires_at ON stock_reservation(expires_at);
CREATE INDEX IF NOT EXISTS idx_stock_reservation_reservation_id ON stock_reservation(reservation_id);

-- 添加外键约束
ALTER TABLE stock_reservation 
ADD CONSTRAINT fk_stock_reservation_variant 
FOREIGN KEY (variant_id) REFERENCES inventory(variant_id) ON DELETE CASCADE;

-- 创建更新时间的触发器函数
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- 为 inventory 表创建触发器
DROP TRIGGER IF EXISTS update_inventory_updated_at ON inventory;
CREATE TRIGGER update_inventory_updated_at
    BEFORE UPDATE ON inventory
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- 为 stock_reservation 表创建触发器
DROP TRIGGER IF EXISTS update_stock_reservation_updated_at ON stock_reservation;
CREATE TRIGGER update_stock_reservation_updated_at
    BEFORE UPDATE ON stock_reservation
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
