-- V2__Create_user_profile_address_avatar_tables.sql
-- 为用户中心功能新增三张表

-- 创建用户信息详情表
CREATE TABLE user_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    email VARCHAR(255),
    gender VARCHAR(20) CHECK (gender IN ('MALE', 'FEMALE', 'OTHER', 'UNKNOWN')),
    birthday DATE,
    personal_signature TEXT,
    wechat_id VARCHAR(100),
    qq_number VARCHAR(20),
    location_province VARCHAR(100),
    location_city VARCHAR(100),
    location_district VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    -- 确保每个用户只有一个profile
    UNIQUE (user_id)
);

-- 创建用户头像表（支持头像历史记录）
CREATE TABLE user_avatars (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    avatar_url VARCHAR(500) NOT NULL,
    avatar_type VARCHAR(20) DEFAULT 'CUSTOM' CHECK (avatar_type IN ('DEFAULT', 'CUSTOM', 'GRAVATAR')),
    file_name VARCHAR(255),
    file_size INTEGER,
    mime_type VARCHAR(100),
    is_current BOOLEAN DEFAULT TRUE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 创建用户收货地址表
CREATE TABLE user_addresses (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    recipient_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    country_code VARCHAR(10) DEFAULT '+86' NOT NULL,
    province VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    district VARCHAR(100) NOT NULL,
    detail_address TEXT NOT NULL,
    postal_code VARCHAR(20),
    is_default BOOLEAN DEFAULT FALSE NOT NULL,
    address_tag VARCHAR(50) CHECK (address_tag IN ('HOME', 'COMPANY', 'SCHOOL', 'OTHER')),
    status VARCHAR(20) DEFAULT 'ACTIVE' NOT NULL CHECK (status IN ('ACTIVE', 'INACTIVE', 'DELETED')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX idx_user_profiles_user_id ON user_profiles(user_id);
CREATE INDEX idx_user_profiles_email ON user_profiles(email);
CREATE INDEX idx_user_profiles_updated_at ON user_profiles(updated_at);

CREATE INDEX idx_user_avatars_user_id ON user_avatars(user_id);
CREATE INDEX idx_user_avatars_is_current ON user_avatars(is_current);
CREATE INDEX idx_user_avatars_created_at ON user_avatars(created_at);

CREATE INDEX idx_user_addresses_user_id ON user_addresses(user_id);
CREATE INDEX idx_user_addresses_is_default ON user_addresses(is_default);
CREATE INDEX idx_user_addresses_status ON user_addresses(status);
CREATE INDEX idx_user_addresses_created_at ON user_addresses(created_at);

-- 为每个用户只能有一个当前头像创建部分唯一索引
CREATE UNIQUE INDEX idx_user_avatars_unique_current 
ON user_avatars(user_id) 
WHERE is_current = TRUE;

-- 为每个用户只能有一个默认地址创建部分唯一索引
CREATE UNIQUE INDEX idx_user_addresses_unique_default 
ON user_addresses(user_id) 
WHERE is_default = TRUE AND status = 'ACTIVE';

-- 创建触发器：自动更新 updated_at 时间戳
CREATE TRIGGER update_user_profiles_updated_at BEFORE UPDATE ON user_profiles
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_user_addresses_updated_at BEFORE UPDATE ON user_addresses
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();