-- 更新产品图片URL为完整的CloudFront路径
UPDATE product SET 
    main_image_url = 
        CASE english_name
            WHEN 'apple' THEN 'https://d3sx9glhrpxv9q.cloudfront.net/apples.jpg'
            WHEN 'kiwi' THEN 'https://d3sx9glhrpxv9q.cloudfront.net/kiwi.png'
            WHEN 'watermelon' THEN 'https://d3sx9glhrpxv9q.cloudfront.net/watermelon.png'
            WHEN 'tomato' THEN 'https://d3sx9glhrpxv9q.cloudfront.net/tomatoes.jpg'
            WHEN 'chilli-pepper' THEN 'https://d3sx9glhrpxv9q.cloudfront.net/chilli-pepper.jpg'
            ELSE main_image_url
        END,
    image_urls = 
        CASE english_name
            WHEN 'apple' THEN '["https://d3sx9glhrpxv9q.cloudfront.net/apples-detail-1.jpg", "https://d3sx9glhrpxv9q.cloudfront.net/apples-detail-2.jpg"]'::jsonb
            WHEN 'kiwi' THEN '["https://d3sx9glhrpxv9q.cloudfront.net/kiwi-detail-1.jpg", "https://d3sx9glhrpxv9q.cloudfront.net/kiwi-detail-2.jpg"]'::jsonb
            WHEN 'watermelon' THEN '["https://d3sx9glhrpxv9q.cloudfront.net/watermelon-detail-1.jpg", "https://d3sx9glhrpxv9q.cloudfront.net/watermelon-detail-2.jpg"]'::jsonb
            WHEN 'tomato' THEN '["https://d3sx9glhrpxv9q.cloudfront.net/tomatoes-detail-1.jpg", "https://d3sx9glhrpxv9q.cloudfront.net/tomatoes-detail-2.jpg"]'::jsonb
            WHEN 'chilli-pepper' THEN '["https://d3sx9glhrpxv9q.cloudfront.net/chilli-pepper-detail-1.jpg", "https://d3sx9glhrpxv9q.cloudfront.net/chilli-pepper-detail-2.jpg"]'::jsonb
            ELSE image_urls
        END
WHERE english_name IN ('apple', 'kiwi', 'watermelon', 'tomato', 'chilli-pepper');

-- 更新变体图片URL
UPDATE product_variant 
SET variant_image_url = p.main_image_url
FROM product p 
WHERE product_variant.product_id = p.id 
  AND product_variant.variant_image_url IS NULL;

-- 为热门产品添加更多描述信息
UPDATE product SET 
    description = 
        CASE english_name
            WHEN 'apple' THEN description || ' 本品采用传统种植方法，无化学农药残留，确保每一颗苹果都天然健康。适合直接食用、制作果盘或榨汁。'
            WHEN 'kiwi' THEN description || ' 自然成熟采摘，果肉细腻，酸甜比例恰到好处。富含抗氧化物质，有助于提高免疫力。'
            WHEN 'watermelon' THEN description || ' 宁夏特有的砂石地种植，昼夜温差大，糖分积累充足。瓜皮坚韧，便于运输和储存。'
            WHEN 'tomato' THEN description || ' 有机认证，无农药化肥，果实自然成熟。皮薄多汁，适合生食、沙拉或烹饪。'
            WHEN 'chilli-pepper' THEN description || ' 传统晾晒工艺，保持辣椒原味。辣度适中，香气持久，是川菜调味的灵魂。'
            ELSE description
        END
WHERE english_name IN ('apple', 'kiwi', 'watermelon', 'tomato', 'chilli-pepper');