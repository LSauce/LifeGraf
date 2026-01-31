-- 开启 TimescaleDB 扩展 (如果尚未开启)
CREATE EXTENSION IF NOT EXISTS timescaledb;

-- 1. 创建 steam_player 表（普通表）
CREATE TABLE IF NOT EXISTS steam_player (
    steam_id VARCHAR(64) PRIMARY KEY,
    avatar VARCHAR(255),
    avatar_full VARCHAR(255),
    avatar_medium VARCHAR(255),
    community_visibility_state BIGINT,
    last_logoff BIGINT,
    loc_city_id BIGINT,
    loc_country_code VARCHAR(10),
    loc_state_code VARCHAR(10),
    persona_name VARCHAR(255),
    persona_state BIGINT,
    persona_state_flags BIGINT,
    primary_clan_id VARCHAR(64),
    profiles_state BIGINT,
    profile_url VARCHAR(255),
    real_name VARCHAR(255),
    game_id VARCHAR(64),
    game_name VARCHAR(255),
    time_created TIMESTAMP
);

-- 2. 创建 steam_player_gaming 表（时序表）
CREATE TABLE IF NOT EXISTS steam_player_gaming (
    time TIMESTAMPTZ NOT NULL,
    persona_state BIGINT,
    steam_id VARCHAR(64) NOT NULL,
    game_id VARCHAR(64),
    game_name VARCHAR(255),
    PRIMARY KEY (steam_id, time)
);

-- 3. 转换为时序超表 (Hypertable)
-- 将 steam_player_gaming 表转为时序表
SELECT create_hypertable('steam_player_gaming', 'time', if_not_exists => TRUE);

-- 4. 创建辅助索引
CREATE INDEX IF NOT EXISTS idx_steam_player_gaming_steam_id_time 
    ON steam_player_gaming (steam_id, time DESC);