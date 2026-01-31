-- 开启 TimescaleDB 扩展 (如果尚未开启)
CREATE EXTENSION IF NOT EXISTS timescaledb;

-- 创建 leetcode_submission 表（时序表）
CREATE TABLE IF NOT EXISTS leetcode_submission (
    id BIGSERIAL,
    submission_id BIGINT NOT NULL,
    submit_time TIMESTAMPTZ NOT NULL,
    question_title TEXT,
    question_translated_title TEXT,
    title_slug VARCHAR(255),
    question_frontend_id VARCHAR(50),
    original_json JSONB,
    create_time TIMESTAMPTZ DEFAULT NOW(),
    update_time TIMESTAMPTZ DEFAULT NOW(),
    PRIMARY KEY (id, submit_time)
);

-- 转换为时序超表 (Hypertable)
SELECT create_hypertable('leetcode_submission', 'submit_time', if_not_exists => TRUE);

-- 创建辅助索引
CREATE INDEX IF NOT EXISTS idx_leetcode_submission_submission_id
    ON leetcode_submission (submission_id);

CREATE INDEX IF NOT EXISTS idx_leetcode_submission_title_slug
    ON leetcode_submission (title_slug);

CREATE INDEX IF NOT EXISTS idx_leetcode_submission_question_frontend_id
    ON leetcode_submission (question_frontend_id);

-- 创建唯一约束，防止重复提交记录
CREATE UNIQUE INDEX IF NOT EXISTS idx_leetcode_submission_unique
    ON leetcode_submission (submission_id, submit_time);
