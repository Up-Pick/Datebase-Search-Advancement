package uppick.dsadvancement.search.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "uppick.dsadvancement.search.repository")
public class ElasticsearchConfig {
}
