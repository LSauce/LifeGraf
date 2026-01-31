-- 第一步：添加新的 time 字段 (带时区的时间戳)
ALTER TABLE gps_points
    ADD COLUMN time TIMESTAMPTZ;

-- 第二步：处理历史数据 (如果表里已有数据，必须执行这一步)
-- 将原有的 timestamp 字段的值赋给新的 time 字段
-- 注意：这里假设服务器时区正确，直接转换。如果原数据是UTC，建议写成 timestamp AT TIME ZONE 'UTC'
UPDATE gps_points
SET time = timestamp::TIMESTAMPTZ
WHERE time IS NULL;

-- 如果表是空的，或者原 timestamp 也没数据，为了防止下一步报错，给个默认值（可选）
-- UPDATE gps_points SET time = NOW() WHERE time IS NULL;

-- 第三步：设置 time 字段非空 (TimescaleDB 的硬性要求)
ALTER TABLE gps_points
    ALTER COLUMN time SET NOT NULL;

-- 第四步：修改主键 (最关键的一步)
-- TimescaleDB 要求主键必须包含分区键(即 time 字段)
-- 1. 先删除旧的主键 (默认名称通常是 表名_pkey)
ALTER TABLE gps_points DROP CONSTRAINT gps_points_pkey;

-- 2. 添加新的复合主键
ALTER TABLE gps_points ADD PRIMARY KEY (id, time);