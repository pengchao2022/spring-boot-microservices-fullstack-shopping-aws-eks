CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(50) UNIQUE NOT NULL,
    user_id BIGINT,
    guest_email VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    total_amount DECIMAL(10,2) NOT NULL,
    subtotal_amount DECIMAL(10,2) NOT NULL,
    shipping_amount DECIMAL(10,2) DEFAULT 0.00,
    tax_amount DECIMAL(10,2) DEFAULT 0.00,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    currency VARCHAR(3) DEFAULT 'CNY',
    
    shipping_first_name VARCHAR(50),
    shipping_last_name VARCHAR(50),
    shipping_email VARCHAR(100),
    shipping_phone VARCHAR(20),
    shipping_company VARCHAR(100),
    shipping_address_line1 VARCHAR(255),
    shipping_address_line2 VARCHAR(255),
    shipping_city VARCHAR(50),
    shipping_state VARCHAR(50),
    shipping_country VARCHAR(50),
    shipping_postal_code VARCHAR(20),
    
    payment_method VARCHAR(20),
    payment_status VARCHAR(20) DEFAULT 'PENDING',
    payment_id VARCHAR(100),
    
    shipping_method VARCHAR(50),
    tracking_number VARCHAR(100),
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    confirmed_at TIMESTAMP NULL,
    shipped_at TIMESTAMP NULL,
    delivered_at TIMESTAMP NULL,
    cancelled_at TIMESTAMP NULL,
    
    notes TEXT,
    cancellation_reason TEXT,
    version INT DEFAULT 0
);

CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    sku VARCHAR(100),
    unit_price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    image_url VARCHAR(500),
    weight DECIMAL(8,3),
    is_digital BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

CREATE TABLE order_status_history (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    notes TEXT,
    created_by VARCHAR(100) DEFAULT 'system',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

-- 创建索引
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);

CREATE INDEX idx_order_status_history_order_id ON order_status_history(order_id);
CREATE INDEX idx_order_status_history_created_at ON order_status_history(created_at);

CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_created_at ON orders(created_at);
CREATE INDEX idx_orders_order_number ON orders(order_number);

-- 创建更新时间的触发器函数
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- 为 orders 表创建触发器
CREATE TRIGGER update_orders_updated_at BEFORE UPDATE ON orders
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 添加注释
COMMENT ON TABLE orders IS '订单主表';
COMMENT ON TABLE order_items IS '订单项表';
COMMENT ON TABLE order_status_history IS '订单状态历史表';

COMMENT ON COLUMN orders.id IS '主键ID';
COMMENT ON COLUMN orders.order_number IS '订单号';
COMMENT ON COLUMN orders.user_id IS '用户ID';
COMMENT ON COLUMN orders.guest_email IS '游客邮箱';
COMMENT ON COLUMN orders.status IS '订单状态：PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED';
COMMENT ON COLUMN orders.total_amount IS '订单总金额';
COMMENT ON COLUMN orders.subtotal_amount IS '商品小计金额';
COMMENT ON COLUMN orders.shipping_amount IS '运费';
COMMENT ON COLUMN orders.tax_amount IS '税费';
COMMENT ON COLUMN orders.discount_amount IS '折扣金额';
COMMENT ON COLUMN orders.currency IS '货币类型';

COMMENT ON COLUMN orders.payment_method IS '支付方式';
COMMENT ON COLUMN orders.payment_status IS '支付状态：PENDING, PAID, FAILED, REFUNDED';
COMMENT ON COLUMN orders.payment_id IS '支付ID';

COMMENT ON COLUMN orders.created_at IS '创建时间';
COMMENT ON COLUMN orders.updated_at IS '更新时间';
COMMENT ON COLUMN orders.confirmed_at IS '确认时间';
COMMENT ON COLUMN orders.shipped_at IS '发货时间';
COMMENT ON COLUMN orders.delivered_at IS '送达时间';
COMMENT ON COLUMN orders.cancelled_at IS '取消时间';