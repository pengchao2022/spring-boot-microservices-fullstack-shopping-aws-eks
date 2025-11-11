-- 创建分类表
CREATE TABLE IF NOT EXISTS category (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    english_name VARCHAR(100) UNIQUE,
    description TEXT,
    image_url VARCHAR(500),
    icon_url VARCHAR(500),
    type VARCHAR(20) NOT NULL DEFAULT 'FRUIT' CHECK (type IN ('FRUIT', 'VEGETABLE')),
    parent_id BIGINT,
    level INT DEFAULT 1,
    sort_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT true,
    is_show_in_menu BOOLEAN DEFAULT true,
    product_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_id) REFERENCES category(id) ON DELETE SET NULL
);

-- 创建分类索引
CREATE INDEX IF NOT EXISTS idx_category_type ON category(type);
CREATE INDEX IF NOT EXISTS idx_category_parent_id ON category(parent_id);
CREATE INDEX IF NOT EXISTS idx_category_active ON category(is_active);
CREATE INDEX IF NOT EXISTS idx_category_sort_order ON category(sort_order);

-- 创建产品表
CREATE TABLE IF NOT EXISTS product (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    english_name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    short_description VARCHAR(500),
    category_type VARCHAR(20) NOT NULL CHECK (category_type IN ('FRUIT', 'VEGETABLE')),
    base_price DECIMAL(10,2),
    weight_unit VARCHAR(20) DEFAULT '斤',
    origin VARCHAR(100),
    storage_method TEXT,
    shelf_life VARCHAR(50),
    nutritional_info JSONB,
    main_image_url VARCHAR(500),
    image_urls JSONB,
    is_featured BOOLEAN DEFAULT false,
    is_active BOOLEAN DEFAULT true,
    sort_order INT DEFAULT 0,
    tags JSONB,
    season VARCHAR(50),
    taste_description VARCHAR(500),
    growing_method VARCHAR(100),
    certification VARCHAR(100),
    view_count INT DEFAULT 0,
    sales_count INT DEFAULT 0,
    category_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE SET NULL
);

-- 创建产品索引
CREATE INDEX IF NOT EXISTS idx_product_english_name ON product(english_name);
CREATE INDEX IF NOT EXISTS idx_product_category_type ON product(category_type);
CREATE INDEX IF NOT EXISTS idx_product_featured ON product(is_featured);
CREATE INDEX IF NOT EXISTS idx_product_active ON product(is_active);
CREATE INDEX IF NOT EXISTS idx_product_sort_order ON product(sort_order);
CREATE INDEX IF NOT EXISTS idx_product_category_id ON product(category_id);

-- 创建产品变体表
CREATE TABLE IF NOT EXISTS product_variant (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    variant_name VARCHAR(100) NOT NULL,
    weight DECIMAL(8,2) NOT NULL,
    weight_unit VARCHAR(20) DEFAULT '斤',
    price DECIMAL(10,2) NOT NULL,
    original_price DECIMAL(10,2),
    cost_price DECIMAL(10,2),
    stock_quantity INT DEFAULT 0,
    low_stock_threshold INT DEFAULT 5,
    is_in_stock BOOLEAN DEFAULT true,
    is_active BOOLEAN DEFAULT true,
    sort_order INT DEFAULT 0,
    variant_image_url VARCHAR(500),
    sku VARCHAR(100) UNIQUE,
    barcode VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);

-- 创建产品变体索引
CREATE INDEX IF NOT EXISTS idx_variant_product_id ON product_variant(product_id);
CREATE INDEX IF NOT EXISTS idx_variant_in_stock ON product_variant(is_in_stock);
CREATE INDEX IF NOT EXISTS idx_variant_active ON product_variant(is_active);
CREATE INDEX IF NOT EXISTS idx_variant_sort_order ON product_variant(sort_order);
CREATE UNIQUE INDEX IF NOT EXISTS uk_product_weight ON product_variant(product_id, weight, weight_unit);

-- 创建库存表
CREATE TABLE IF NOT EXISTS inventory (
    id BIGSERIAL PRIMARY KEY,
    variant_id BIGINT NOT NULL UNIQUE,
    current_stock INT NOT NULL DEFAULT 0,
    reserved_stock INT DEFAULT 0,
    available_stock INT DEFAULT 0,
    minimum_stock_level INT DEFAULT 5,
    maximum_stock_level INT DEFAULT 1000,
    reorder_point INT DEFAULT 10,
    total_sold INT DEFAULT 0,
    total_returned INT DEFAULT 0,
    last_restocked_date TIMESTAMP,
    last_sold_date TIMESTAMP,
    is_tracked BOOLEAN DEFAULT true,
    status VARCHAR(20) DEFAULT 'IN_STOCK' CHECK (status IN ('IN_STOCK', 'LOW_STOCK', 'OUT_OF_STOCK', 'DISCONTINUED')),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (variant_id) REFERENCES product_variant(id) ON DELETE CASCADE
);

-- 创建库存索引
CREATE INDEX IF NOT EXISTS idx_inventory_status ON inventory(status);
CREATE INDEX IF NOT EXISTS idx_inventory_tracked ON inventory(is_tracked);
CREATE INDEX IF NOT EXISTS idx_inventory_available_stock ON inventory(available_stock);

-- 创建更新时间戳的触发器函数
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- 为需要自动更新的表创建触发器
CREATE TRIGGER update_category_updated_at BEFORE UPDATE ON category FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_product_updated_at BEFORE UPDATE ON product FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_product_variant_updated_at BEFORE UPDATE ON product_variant FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_inventory_updated_at BEFORE UPDATE ON inventory FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();