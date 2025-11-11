-- 插入分类数据
INSERT INTO category (name, english_name, description, type, sort_order, image_url) VALUES
('新鲜水果', 'fresh-fruits', '各种时令新鲜水果，产地直供，新鲜送达', 'FRUIT', 1, 'https://d3sx9glhrpxv9q.cloudfront.net/fruits-category.jpg'),
('有机蔬菜', 'organic-vegetables', '无农药有机蔬菜，健康生活首选', 'VEGETABLE', 2, 'https://d3sx9glhrpxv9q.cloudfront.net/vegetables-category.jpg'),
('进口水果', 'imported-fruits', '全球优质进口水果，品质保证', 'FRUIT', 3, 'https://d3sx9glhrpxv9q.cloudfront.net/imported-fruits.jpg'),
('叶菜类', 'leafy-vegetables', '新鲜叶菜，绿色健康', 'VEGETABLE', 4, 'https://d3sx9glhrpxv9q.cloudfront.net/leafy-vegetables.jpg');

-- 插入苹果产品数据
INSERT INTO product (
    name, english_name, description, short_description, category_type, 
    base_price, origin, storage_method, shelf_life, nutritional_info,
    main_image_url, is_featured, sort_order, tags, season, taste_description,
    growing_method, certification, category_id
) VALUES (
    '栖霞红富士苹果',
    'apple',
    '产自山东栖霞的红富士苹果，色泽红艳，果形端正，果肉脆爽多汁，甜度适中，富含维生素C和膳食纤维，是日常健康饮食的优质选择。每一颗苹果都经过精心挑选，确保品质上乘。',
    '红艳诱人，脆爽多汁',
    'FRUIT',
    12.50,
    '山东栖霞',
    '冷藏保存，避免阳光直射，适宜温度0-4℃',
    '30天',
    '{"vitaminC": "4.6mg/100g", "dietaryFiber": "2.4g/100g", "calories": "52kcal/100g", "carbohydrates": "13.8g/100g", "sugar": "10.4g/100g"}',
    'https://d3sx9glhrpxv9q.cloudfront.net/apples.jpg',
    true,
    1,
    '["新鲜", "脆甜", "红富士", "山东特产", "维生素C"]',
    '秋季',
    '脆爽多汁，甜度适中，果香浓郁',
    '有机种植',
    '绿色食品认证',
    1
);

-- 插入苹果变体数据
INSERT INTO product_variant (product_id, variant_name, weight, price, original_price, stock_quantity, sort_order, sku) VALUES
(1, '3斤尝鲜装', 3, 35.00, 38.00, 50, 1, 'APPLE-3J'),
(1, '5斤家庭装', 5, 55.00, 60.00, 80, 2, 'APPLE-5J'),
(1, '8斤实惠装', 8, 85.00, 95.00, 30, 3, 'APPLE-8J'),
(1, '10斤礼盒装', 10, 105.00, 118.00, 20, 4, 'APPLE-10J');

-- 插入库存数据
INSERT INTO inventory (variant_id, current_stock, available_stock, total_sold) VALUES
(1, 50, 50, 120),
(2, 80, 80, 200),
(3, 30, 30, 80),
(4, 20, 20, 45);

-- 插入猕猴桃产品数据
INSERT INTO product (
    name, english_name, description, short_description, category_type, 
    base_price, origin, storage_method, shelf_life, nutritional_info,
    main_image_url, is_featured, sort_order, tags, season, taste_description,
    growing_method, certification, category_id
) VALUES (
    '秦岭猕猴桃',
    'kiwi',
    '产自陕西秦岭的优质猕猴桃，果形饱满，果肉翠绿，酸甜适中，富含维生素C和多种矿物质，营养价值极高。自然成熟，口感绝佳。',
    '秀色可餐，果香浓郁',
    'FRUIT',
    25.80,
    '陕西秦岭',
    '常温避光保存，成熟后需冷藏',
    '15天',
    '{"vitaminC": "62mg/100g", "dietaryFiber": "3.0g/100g", "calories": "61kcal/100g", "potassium": "312mg/100g", "vitaminE": "1.5mg/100g"}',
    'https://d3sx9glhrpxv9q.cloudfront.net/kiwi.png',
    true,
    2,
    '["猕猴桃", "维生素C", "秦岭特产", "酸甜可口", "营养丰富"]',
    '秋季',
    '酸甜适中，果香浓郁，口感细腻',
    '山地种植',
    '无公害认证',
    1
);

-- 插入猕猴桃变体数据
INSERT INTO product_variant (product_id, variant_name, weight, price, original_price, stock_quantity, sort_order, sku) VALUES
(2, '6个尝鲜装', 1.2, 28.00, 32.00, 40, 1, 'KIWI-6P'),
(2, '12个家庭装', 2.4, 52.00, 60.00, 60, 2, 'KIWI-12P'),
(2, '24个礼盒装', 4.8, 98.00, 115.00, 25, 3, 'KIWI-24P');

-- 插入库存数据
INSERT INTO inventory (variant_id, current_stock, available_stock, total_sold) VALUES
(5, 40, 40, 95),
(6, 60, 60, 150),
(7, 25, 25, 60);

-- 插入西瓜产品数据
INSERT INTO product (
    name, english_name, description, short_description, category_type, 
    base_price, origin, storage_method, shelf_life, nutritional_info,
    main_image_url, is_featured, sort_order, tags, season, taste_description,
    growing_method, category_id
) VALUES (
    '宁夏硒砂瓜',
    'watermelon',
    '宁夏特产硒砂瓜，瓜形饱满，皮薄肉厚，汁多味甜，富含硒元素，营养丰富。夏季消暑解渴的佳品。',
    '绿裳红心玉为魂，清甜如许胜琼浆',
    'FRUIT',
    8.80,
    '宁夏中卫',
    '常温保存，切开后需冷藏',
    '20天',
    '{"vitaminC": "8.1mg/100g", "lycopene": "4532μg/100g", "calories": "30kcal/100g", "water": "91.5g/100g", "selenium": "0.2μg/100g"}',
    'https://d3sx9glhrpxv9q.cloudfront.net/watermelon.png',
    true,
    3,
    '["西瓜", "宁夏特产", "消暑", "多汁", "硒砂瓜"]',
    '夏季',
    '清甜多汁，爽口解渴',
    '砂石地种植',
    1
);

-- 插入西瓜变体数据
INSERT INTO product_variant (product_id, variant_name, weight, price, original_price, stock_quantity, sort_order, sku) VALUES
(3, '单个装(约8-10斤)', 9, 35.00, 40.00, 25, 1, 'WATERMELON-1P'),
(3, '两个装(约16-20斤)', 18, 65.00, 75.00, 15, 2, 'WATERMELON-2P');

-- 插入库存数据
INSERT INTO inventory (variant_id, current_stock, available_stock, total_sold) VALUES
(8, 25, 25, 80),
(9, 15, 15, 45);

-- 插入番茄产品数据（蔬菜）
INSERT INTO product (
    name, english_name, description, short_description, category_type, 
    base_price, origin, storage_method, shelf_life, nutritional_info,
    main_image_url, is_featured, sort_order, tags, season, taste_description,
    growing_method, category_id
) VALUES (
    '有机樱桃番茄',
    'tomato',
    '有机种植的樱桃番茄，果形圆润，色泽鲜红，口感酸甜适中，富含番茄红素和维生素C，是健康饮食的理想选择。',
    '色彩之韵，生长之诗',
    'VEGETABLE',
    15.80,
    '山东寿光',
    '冷藏保存，避免挤压',
    '10天',
    '{"vitaminC": "14mg/100g", "lycopene": "2573μg/100g", "calories": "18kcal/100g", "potassium": "237mg/100g", "fiber": "1.2g/100g"}',
    'https://d3sx9glhrpxv9q.cloudfront.net/tomatoes.jpg',
    true,
    1,
    '["番茄", "有机", "樱桃番茄", "维生素C", "番茄红素"]',
    '全年',
    '酸甜适中，果肉饱满',
    '有机种植',
    2
);

-- 插入番茄变体数据
INSERT INTO product_variant (product_id, variant_name, weight, price, original_price, stock_quantity, sort_order, sku) VALUES
(4, '500g装', 0.5, 15.80, 18.00, 35, 1, 'TOMATO-500G'),
(4, '1kg家庭装', 1, 28.00, 32.00, 50, 2, 'TOMATO-1KG'),
(4, '2kg实惠装', 2, 52.00, 60.00, 20, 3, 'TOMATO-2KG');

-- 插入库存数据
INSERT INTO inventory (variant_id, current_stock, available_stock, total_sold) VALUES
(10, 35, 35, 90),
(11, 50, 50, 120),
(12, 20, 20, 55);

-- 插入辣椒产品数据
INSERT INTO product (
    name, english_name, description, short_description, category_type, 
    base_price, origin, storage_method, shelf_life, nutritional_info,
    main_image_url, is_featured, sort_order, tags, season, taste_description,
    growing_method, category_id
) VALUES (
    '四川二荆条辣椒',
    'chilli-pepper',
    '四川特产二荆条辣椒，辣味醇厚，香气浓郁，是川菜调味的必备佳品。适合制作辣椒油、泡椒等。',
    '酣畅淋漓，辣味十足',
    'VEGETABLE',
    22.80,
    '四川成都',
    '阴凉干燥处保存',
    '60天',
    '{"vitaminC": "144mg/100g", "capsaicin": "0.2%", "calories": "40kcal/100g", "vitaminA": "132μg/100g", "iron": "1.2mg/100g"}',
    'https://d3sx9glhrpxv9q.cloudfront.net/chilli-pepper.jpg',
    false,
    2,
    '["辣椒", "二荆条", "四川特产", "调味品", "辛辣"]',
    '夏秋季',
    '辣味醇厚，香气浓郁',
    '露地种植',
    2
);

-- 插入辣椒变体数据
INSERT INTO product_variant (product_id, variant_name, weight, price, original_price, stock_quantity, sort_order, sku) VALUES
(5, '250g装', 0.25, 22.80, 25.00, 20, 1, 'CHILLI-250G'),
(5, '500g装', 0.5, 42.00, 48.00, 15, 2, 'CHILLI-500G');

-- 插入库存数据
INSERT INTO inventory (variant_id, current_stock, available_stock, total_sold) VALUES
(13, 20, 20, 35),
(14, 15, 15, 25);

-- 更新分类产品数量
UPDATE category SET product_count = (
    SELECT COUNT(*) FROM product WHERE category_id = category.id AND is_active = true
);