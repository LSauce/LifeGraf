-- 为 TimescaleDB 时序表配置压缩策略
-- 参考文档: https://docs.timescale.com/timescaledb/latest/how-to-guides/compression/

-- 1. 为 electricity_metric 表启用压缩
-- 按 electric_id 分段压缩，时间越久的数据压缩比越高
ALTER TABLE electricity_metric SET (
    timescaledb.compress,
    timescaledb.compress_segmentby = 'electric_id'
);

-- 2. 为 steam_player_gaming 表启用压缩
-- 按 steam_id 分段压缩
ALTER TABLE steam_player_gaming SET (
    timescaledb.compress,
    timescaledb.compress_segmentby = 'steam_id'
);

-- 3. 为 electricity_metric 表添加压缩策略
-- 自动压缩 7 天前的数据
SELECT add_compression_policy('electricity_metric', INTERVAL '7 days');

-- 4. 为 steam_player_gaming 表添加压缩策略
-- 自动压缩 7 天前的数据
SELECT add_compression_policy('steam_player_gaming', INTERVAL '7 days');