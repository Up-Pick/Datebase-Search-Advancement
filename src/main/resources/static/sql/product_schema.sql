CREATE TABLE IF NOT EXISTS product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    view_count BIGINT NOT NULL DEFAULT 0,
    registered_at DATETIME NOT NULL,
    image VARCHAR(255),
    category_id BIGINT NOT NULL,
    register_id BIGINT NOT NULL,
    big_category VARCHAR(255) NOT NULL,
    small_category VARCHAR(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 복합 인덱스 (Full-Text Search)
-- 1. 테이블 존재 여부
SET @table_exists := (SELECT COUNT(*)
                      FROM INFORMATION_SCHEMA.TABLES
                      WHERE TABLE_SCHEMA = DATABASE()
                        AND TABLE_NAME = 'product');

-- 2. 인덱스 존재 여부
SET @index_exists := (SELECT COUNT(*)
                      FROM INFORMATION_SCHEMA.STATISTICS
                      WHERE TABLE_SCHEMA = DATABASE()
                        AND TABLE_NAME = 'product'
                        AND INDEX_NAME = 'idx_product_name');

-- 3. 생성 조건: 테이블 존재 AND 인덱스 없음
SET @sql := IF(@table_exists > 0 AND @index_exists = 0,
               'CREATE FULLTEXT INDEX idx_product_name ON product(name)',
               'SELECT "table missing or index already exists"');

-- 4. 동적 SQL 실행
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;