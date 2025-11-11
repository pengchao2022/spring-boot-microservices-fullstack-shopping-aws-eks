-- V4__Cleanup_duplicate_addresstag_column.sql
-- 清理重复的 addresstag 列，确保列映射正确

-- 安全地删除可能存在的重复列
ALTER TABLE user_addresses DROP COLUMN IF EXISTS addresstag;

-- 可选：添加注释说明此次修复
COMMENT ON TABLE user_addresses IS 'User addresses with correct column mapping after fixing duplicate addresstag column';