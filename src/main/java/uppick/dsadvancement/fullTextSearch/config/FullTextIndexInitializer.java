package uppick.dsadvancement.fullTextSearch.config;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FullTextIndexInitializer {

	private final JdbcTemplate jdbcTemplate;

	@PostConstruct
	public void init() {
		// 테이블 생성
		jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS product (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                product_name VARCHAR(255) NOT NULL,
                content TEXT NOT NULL,
                FULLTEXT INDEX idx_product_name (product_name) WITH PARSER ngram
            ) ENGINE=InnoDB
            DEFAULT CHARSET=utf8mb4
            COLLATE=utf8mb4_unicode_ci;
        """);
	}
}
