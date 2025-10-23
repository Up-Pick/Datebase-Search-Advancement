package uppick.dsadvancement.dsa.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.sql.MySQLTemplates;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;

import jakarta.persistence.EntityManager;

@Configuration
public class QueryDslConfig {

	@Bean
	public JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
		return new JPAQueryFactory(entityManager);
	}

	@Bean
	public SQLQueryFactory sqlQueryFactory(DataSource dataSource) {
		SQLTemplates templates = MySQLTemplates.builder().printSchema().build();
		com.querydsl.sql.Configuration configuration = new com.querydsl.sql.Configuration(templates);

		return new SQLQueryFactory(configuration, dataSource);
	}
}
