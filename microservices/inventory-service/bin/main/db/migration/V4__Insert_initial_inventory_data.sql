-- 插入初始库存数据 (基于现有产品变体)
INSERT INTO inventory (variant_id, current_stock, available_stock, minimum_stock_level, maximum_stock_level, status) 
VALUES 
(1, 100, 100, 10, 500, 'IN_STOCK'),
(2, 50, 50, 5, 200, 'IN_STOCK'),
(3, 200, 200, 20, 1000, 'IN_STOCK'),
(4, 75, 75, 8, 300, 'IN_STOCK'),
(5, 30, 30, 5, 150, 'LOW_STOCK')
ON CONFLICT (variant_id) DO NOTHING;
