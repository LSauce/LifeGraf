-- 开启 TimescaleDB 扩展 (如果尚未开启)
CREATE EXTENSION IF NOT EXISTS timescaledb;

-- 1. 创建 activity_watch_bucket 表（普通表）
CREATE TABLE IF NOT EXISTS activity_watch_bucket (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(255),
    client VARCHAR(255),
    hostname VARCHAR(255),
    description TEXT,
    create_time TIMESTAMPTZ,
    update_time TIMESTAMPTZ
);

-- 2. 创建 activity_watch_event 表（时序表）
CREATE TABLE IF NOT EXISTS activity_watch_event (
    id BIGSERIAL,
    bucket_id BIGINT NOT NULL,
    start_time TIMESTAMPTZ NOT NULL,
    end_time TIMESTAMPTZ,
    duration DOUBLE PRECISION,
    app TEXT,
    title TEXT,
    PRIMARY KEY (id, start_time)
);

-- 3. 转换为时序超表 (Hypertable)
-- 将 activity_watch_event 表转为时序表
SELECT create_hypertable('activity_watch_event', 'start_time', if_not_exists => TRUE);

-- 4. 创建辅助索引
CREATE INDEX IF NOT EXISTS idx_activity_watch_event_bucket_time 
    ON activity_watch_event (bucket_id, start_time DESC);

-- 为 app 和 title 创建索引以提高查询性能
CREATE INDEX IF NOT EXISTS idx_activity_watch_event_app 
    ON activity_watch_event (app);

CREATE INDEX IF NOT EXISTS idx_activity_watch_event_title 
    ON activity_watch_event (title);