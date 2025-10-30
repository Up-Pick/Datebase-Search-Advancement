CREATE TABLE IF NOT EXISTS auction (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    register_id BIGINT NOT NULL,
    last_bidder_id BIGINT,
    status VARCHAR(50) NOT NULL,
    current_price BIGINT,
    min_price BIGINT NOT NULL,
    start_at DATETIME NOT NULL,
    end_at DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================================
-- 복합 인덱스
-- =========================================
-- 1. 테이블 존재 여부
SET @table_exists := (SELECT COUNT(*)
                      FROM INFORMATION_SCHEMA.TABLES
                      WHERE TABLE_SCHEMA = DATABASE()
                        AND TABLE_NAME = 'auction');

-- 2. 인덱스 존재 여부
SET @index_exists := (SELECT COUNT(*)
                      FROM INFORMATION_SCHEMA.STATISTICS
                      WHERE TABLE_SCHEMA = DATABASE()
                        AND TABLE_NAME = 'auction'
                        AND INDEX_NAME = 'idx_lastbidderid_status_endat');

-- 3. 생성 조건: 테이블 존재 AND 인덱스 없음
SET @sql := IF(@table_exists > 0 AND @index_exists = 0,
               'CREATE INDEX idx_lastbidderid_status_endat ON auction(last_bidder_id ASC, status ASC, end_at DESC)',
               'SELECT "table missing or index already exists"');

-- 4. 동적 SQL 실행
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;