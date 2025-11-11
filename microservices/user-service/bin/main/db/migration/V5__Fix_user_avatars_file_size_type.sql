-- V5__Fix_user_avatars_file_size_type.sql
-- 修复 user_avatars 表 file_size 列的数据类型

ALTER TABLE user_avatars 
ALTER COLUMN file_size TYPE BIGINT;