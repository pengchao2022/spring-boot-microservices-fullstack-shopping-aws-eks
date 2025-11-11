-- 创建库存预留表
CREATE TABLE IF NOT EXISTS stock_reservation (
    id BIGSERIAL PRIMARY KEY,
    reservation_id VARCHAR(100) NOT NULL UNIQUE,
    variant_id BIGINT NOT NULL,
    order_id VARCHAR(100) NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 添加检查约束
ALTER TABLE stock_reservation ADD CONSTRAINT stock_reservation_status_check 
    CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'EXPIRED'));
