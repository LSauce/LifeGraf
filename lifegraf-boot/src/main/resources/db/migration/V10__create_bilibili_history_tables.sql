-- 开启 TimescaleDB 扩展 (如果尚未开启)
CREATE EXTENSION IF NOT EXISTS timescaledb;

-- 创建 bilibili_history 表（时序表）
CREATE TABLE IF NOT EXISTS bilibili_history (
    id BIGSERIAL,
    title TEXT,
    long_title TEXT,
    cover TEXT,
    uri TEXT,
    oid BIGINT,
    epid BIGINT,
    bvid VARCHAR(50),
    page INTEGER,
    cid BIGINT,
    part TEXT,
    business VARCHAR(50),
    dt INTEGER,
    videos INTEGER,
    author_name TEXT,
    author_face TEXT,
    author_mid BIGINT,
    view_at TIMESTAMPTZ,
    progress INTEGER,
    badge TEXT,
    show_title TEXT,
    duration INTEGER,
    current_val TEXT,
    total INTEGER,
    new_desc TEXT,
    is_finish INTEGER,
    is_fav INTEGER,
    kid BIGINT,
    tag_name TEXT,
    live_status INTEGER,
    original_json JSONB,
    create_time TIMESTAMPTZ DEFAULT NOW(),
    update_time TIMESTAMPTZ DEFAULT NOW(),
    PRIMARY KEY (id, view_at)
);

-- 转换为时序超表 (Hypertable)
SELECT create_hypertable('bilibili_history', 'view_at', if_not_exists => TRUE);

-- 创建辅助索引
CREATE INDEX IF NOT EXISTS idx_bilibili_history_view_at 
    ON bilibili_history (view_at DESC);

CREATE INDEX IF NOT EXISTS idx_bilibili_history_business 
    ON bilibili_history (business);

CREATE INDEX IF NOT EXISTS idx_bilibili_history_author_name 
    ON bilibili_history (author_name);

CREATE INDEX IF NOT EXISTS idx_bilibili_history_bvid 
    ON bilibili_history (bvid);

CREATE INDEX IF NOT EXISTS idx_bilibili_history_kid 
    ON bilibili_history (kid);