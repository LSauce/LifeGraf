-- 开启 TimescaleDB 扩展 (如果尚未开启)
CREATE EXTENSION IF NOT EXISTS timescaledb;

-- 1. 创建表结构
-- 注意：这里将 Java 的 String 类型的数值字段映射为了 NUMERIC，以便进行计算
CREATE TABLE IF NOT EXISTS electricity_metric (
    -- 核心时间字段 (对应 LocalDateTime)
                                                  time        TIMESTAMPTZ NOT NULL,

    -- 对应 electricId (建议建索引)
                                                  electric_id VARCHAR(64) NOT NULL,

    -- 对应 money (对应 String，但在 DB 存为高精度数值)
    money       NUMERIC(15, 4),

    -- 对应 initPower
    init_power  NUMERIC(15, 4),

    -- 对应 lastPower
    last_power  NUMERIC(15, 4),

    -- 对应 date (对应 LocalDate)
    -- 注意：其实可以通过 time 字段直接推算日期，但为了业务方便保留此列
    date        DATE,

    -- 【重要】TimescaleDB 要求主键必须包含时间列
    -- 如果你需要防止同一设备在同一时间点重复插入，请使用此主键：
    PRIMARY KEY (electric_id, time)
    );

-- 2. 转换为时序超表 (Hypertable)
-- 这一步是关键，将普通表转为时序表
SELECT create_hypertable('electricity_metric', 'time', if_not_exists => TRUE);

-- 3. 创建辅助索引 (可选，但推荐)
-- TimescaleDB 会自动为 time 建索引，但我们需要按设备查询
-- 创建一个 (electric_id, time) 的联合索引，大幅提升 "查询某设备某段时间数据" 的速度
CREATE INDEX IF NOT EXISTS idx_electricity_metric_device_time
    ON electricity_metric (electric_id, time DESC);