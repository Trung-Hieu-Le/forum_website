-- Migration: Insert default topics
-- This migration inserts initial topics into the topics table
-- Only inserts if topics table is empty or topic doesn't exist

INSERT INTO topics (name, created_at, updated_at)
SELECT * FROM (
    SELECT 'Công nghệ thông tin' AS name, NOW() AS created_at, NOW() AS updated_at
    UNION ALL SELECT 'Lập trình', NOW(), NOW()
    UNION ALL SELECT 'Thảo luận chung', NOW(), NOW()
    UNION ALL SELECT 'Hỏi đáp', NOW(), NOW()
    UNION ALL SELECT 'Tin tức', NOW(), NOW()
    UNION ALL SELECT 'Giải trí', NOW(), NOW()
    UNION ALL SELECT 'Học tập', NOW(), NOW()
    UNION ALL SELECT 'Thể thao', NOW(), NOW()
) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM topics WHERE topics.name = tmp.name
);

