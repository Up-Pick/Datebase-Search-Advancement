package uppick.dsadvancement.dsa.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "uppick.dsadvancement.dsa")
public class JpaConfig {
}
