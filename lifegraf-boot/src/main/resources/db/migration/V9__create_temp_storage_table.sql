-- 创建用于存储B站登录临时数据的表
CREATE TABLE IF NOT EXISTS temp_storage (
    id BIGSERIAL PRIMARY KEY,
    key_name VARCHAR(255) NOT NULL UNIQUE,
    value TEXT,
    expire_time TIMESTAMPTZ,
    create_time TIMESTAMPTZ DEFAULT NOW(),
    update_time TIMESTAMPTZ DEFAULT NOW()
);

-- 创建索引以提高查询性能
CREATE INDEX IF NOT EXISTS idx_temp_storage_key_name ON temp_storage(key_name);
CREATE INDEX IF NOT EXISTS idx_temp_storage_expire_time ON temp_storage(expire_time);

-- 添加注释
COMMENT ON TABLE temp_storage IS '临时存储表，用于存储B站登录二维码等临时数据';
COMMENT ON COLUMN temp_storage.id IS '主键ID';
COMMENT ON COLUMN temp_storage.key_name IS '键名';
COMMENT ON COLUMN temp_storage.value IS '键值';
COMMENT ON COLUMN temp_storage.expire_time IS '过期时间';
COMMENT ON COLUMN temp_storage.create_time IS '创建时间';
COMMENT ON COLUMN temp_storage.update_time IS '更新时间';