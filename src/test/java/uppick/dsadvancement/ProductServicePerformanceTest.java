package uppick.dsadvancement;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import lombok.extern.slf4j.Slf4j;
import uppick.dsadvancement.fullTextSearch.Dto.response.ProductSearchDto;
import uppick.dsadvancement.fullTextSearch.entity.Product;
import uppick.dsadvancement.fullTextSearch.service.ProductService;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class ProductServicePerformanceTest {

	@Autowired
	private ProductService productService;

	private static final String KEYWORD = "123번이에요";

	@Test
	void compareAveragePerformance() {
		int iterations = 5;

		long jpaWithFullText = 0;
		long dslWithFullText = 0;

		long jpaWithLike = 0;
		long dslWithLike = 0;

		for (int i = 0; i < iterations; i++) {
			jpaWithFullText += measure(() -> productService.findProductsWithJpaByFullText(KEYWORD));
			dslWithFullText += measure(() -> productService.findProductsWithDslByFullText(KEYWORD));

			jpaWithLike += measure(() -> productService.findProductsWithJpaByLike(KEYWORD));
			dslWithLike += measure(() -> productService.findProductsWithDslByLike(KEYWORD));
		}

		log.info("[JPA With Index Average] : {} ms", jpaWithFullText / iterations);
		log.info("[DSL With Index Average] : {} ms", dslWithFullText / iterations);

		log.info("[JPA With No Index Average] : {} ms", jpaWithLike / iterations);
		log.info("[DSL With No Index Average] : {} ms", dslWithLike / iterations);

		List<Product> p1 = productService.findProductsWithJpaByFullText(KEYWORD);
		List<ProductSearchDto> p2 = productService.findProductsWithDslByFullText(KEYWORD);

		List<Product> p3 = productService.findProductsWithJpaByLike(KEYWORD);
		List<ProductSearchDto> p4 = productService.findProductsWithDslByLike(KEYWORD);

		log.info("Test done");
	}

	private long measure(Runnable runnable) {
		Instant start = Instant.now();
		runnable.run();
		return Duration.between(start, Instant.now()).toMillis();
	}
}
