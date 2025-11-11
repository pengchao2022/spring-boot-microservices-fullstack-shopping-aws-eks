-- 创建 payments 表
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(50) UNIQUE NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(20) NOT NULL DEFAULT 'ALIPAY',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    alipay_trade_no VARCHAR(100),
    subject VARCHAR(255),
    body TEXT,
    currency VARCHAR(3) DEFAULT 'CNY',
    payer_user_id VARCHAR(100),
    payer_email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    paid_at TIMESTAMP NULL,
    refunded_at TIMESTAMP NULL,
    cancelled_at TIMESTAMP NULL,
    failure_reason TEXT,
    version INT DEFAULT 0
);

-- 创建 refunds 表
CREATE TABLE refunds (
    id BIGSERIAL PRIMARY KEY,
    payment_id BIGINT NOT NULL,
    refund_number VARCHAR(50) UNIQUE NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    alipay_refund_no VARCHAR(100),
    reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    refunded_at TIMESTAMP NULL,
    failure_reason TEXT,
    
    FOREIGN KEY (payment_id) REFERENCES payments(id) ON DELETE CASCADE
);

-- 创建索引
CREATE INDEX idx_payments_order_number ON payments(order_number);
CREATE INDEX idx_payments_alipay_trade_no ON payments(alipay_trade_no);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_created_at ON payments(created_at);

CREATE INDEX idx_refunds_refund_number ON refunds(refund_number);
CREATE INDEX idx_refunds_payment_id ON refunds(payment_id);
CREATE INDEX idx_refunds_status ON refunds(status);

-- 创建 updated_at 自动更新的触发器
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- 为 payments 表创建触发器
CREATE TRIGGER update_payments_updated_at BEFORE UPDATE ON payments
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 为 refunds 表创建触发器
CREATE TRIGGER update_refunds_updated_at BEFORE UPDATE ON refunds
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();