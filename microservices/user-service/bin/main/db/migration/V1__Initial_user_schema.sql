-- V1__Initial_user_schema.sql
-- 创建用户表
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    phone VARCHAR(20) NOT NULL,
    country_code VARCHAR(10) DEFAULT '+86' NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100),
    alipay_user_id VARCHAR(100) UNIQUE,
    taobao_user_id VARCHAR(100) UNIQUE,
    avatar_url VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    register_source VARCHAR(20) NOT NULL DEFAULT 'PHONE',
    last_login_at TIMESTAMP,
    last_login_ip VARCHAR(45),
    login_count INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    -- 国家区号+手机号的复合唯一约束
    UNIQUE (country_code, phone)
);

-- 创建索引
CREATE INDEX idx_users_country_code_phone ON users(country_code, phone);
CREATE INDEX idx_users_phone ON users(phone);
CREATE INDEX idx_users_alipay_user_id ON users(alipay_user_id);
CREATE INDEX idx_users_taobao_user_id ON users(taobao_user_id);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_register_source ON users(register_source);
CREATE INDEX idx_users_created_at ON users(created_at);
CREATE INDEX idx_users_updated_at ON users(updated_at);

-- 创建验证码表（用于手机验证码登录）
CREATE TABLE verification_codes (
    id BIGSERIAL PRIMARY KEY,
    phone VARCHAR(20) NOT NULL,
    country_code VARCHAR(10) DEFAULT '+86' NOT NULL,
    code VARCHAR(10) NOT NULL,
    used BOOLEAN DEFAULT FALSE NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_verification_codes_country_code_phone ON verification_codes(country_code, phone);
CREATE INDEX idx_verification_codes_phone ON verification_codes(phone);
CREATE INDEX idx_verification_codes_code ON verification_codes(code);
CREATE INDEX idx_verification_codes_used ON verification_codes(used);
CREATE INDEX idx_verification_codes_expires_at ON verification_codes(expires_at);
CREATE INDEX idx_verification_codes_created_at ON verification_codes(created_at);

-- 创建用户登录历史表
CREATE TABLE user_login_history (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    login_type VARCHAR(20) NOT NULL,
    login_ip VARCHAR(45),
    user_agent TEXT,
    success BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_login_history_user_id ON user_login_history(user_id);
CREATE INDEX idx_user_login_history_login_type ON user_login_history(login_type);
CREATE INDEX idx_user_login_history_success ON user_login_history(success);
CREATE INDEX idx_user_login_history_created_at ON user_login_history(created_at);

-- 创建触发器：自动更新 updated_at 时间戳
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 插入默认数据（可选）
-- INSERT INTO users (phone, country_code, password, name, register_source) 
-- VALUES ('13800138000', '+86', '$2a$10$examplehashedpassword', '测试用户', 'PHONE');