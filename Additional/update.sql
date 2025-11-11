UPDATE product SET 
  main_image_url = 'https://s3fruits202511010101.s3.us-east-1.amazonaws.com/kiwi-category/meihaokiwi.jpg',
  image_urls = '[
    "https://s3fruits202511010101.s3.us-east-1.amazonaws.com/kiwi-detail-images/kiwi-farm1.jpg",
    "https://s3fruits202511010101.s3.us-east-1.amazonaws.com/kiwi-detail-images/kiwivitamin.jpg"
  ]'::jsonb
WHERE id = 17;
