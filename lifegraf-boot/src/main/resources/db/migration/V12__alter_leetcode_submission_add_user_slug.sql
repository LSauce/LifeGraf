-- 添加 user_slug 字段到 leetcode_submission 表
ALTER TABLE leetcode_submission ADD COLUMN IF NOT EXISTS user_slug VARCHAR(255);

-- 创建索引以加速按用户查询
CREATE INDEX IF NOT EXISTS idx_leetcode_submission_user_slug
    ON leetcode_submission (user_slug);

-- 创建复合索引用于查询用户最后提交时间
CREATE INDEX IF NOT EXISTS idx_leetcode_submission_user_submit_time
    ON leetcode_submission (user_slug, submit_time DESC);
