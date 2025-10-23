package uppick.dsadvancement;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import lombok.extern.slf4j.Slf4j;
import uppick.dsadvancement.dsa.Dto.response.ProductSearchDto;
import uppick.dsadvancement.dsa.entity.Product;
import uppick.dsadvancement.dsa.service.ProductService;
import uppick.dsadvancement.search.document.ProductDocument;
import uppick.dsadvancement.search.service.ProductSearchService;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class ProductServicePerformanceTest {

	@Autowired
	private ProductService productService;
	@Autowired
	private ProductSearchService productSearchService;

	private static final String KEYWORD = "123번 신발";

	@Test
	void compareAveragePerformance() {
		int iterations = 5;

		long jpaWithFullText = 0;
		long dslWithFullText = 0;

		long jpaWithLike = 0;
		long dslWithLike = 0;

		long elasticWithQuery = 0;

		for (int i = 0; i < iterations; i++) {
			jpaWithFullText += measure(() -> productService.findProductsWithJpaByFullText(KEYWORD));
			dslWithFullText += measure(() -> productService.findProductsWithDslByFullText(KEYWORD));

			jpaWithLike += measure(() -> productService.findProductsWithJpaByLike(KEYWORD));
			dslWithLike += measure(() -> productService.findProductsWithDslByLike(KEYWORD));

			elasticWithQuery += measure(() -> productSearchService.findProductsWithElasticsearch(KEYWORD));
		}

		log.info("[JPA With Index Average] : {} ms", jpaWithFullText / iterations);
		log.info("[DSL With Index Average] : {} ms", dslWithFullText / iterations);

		log.info("[JPA With No Index Average] : {} ms", jpaWithLike / iterations);
		log.info("[DSL With No Index Average] : {} ms", dslWithLike / iterations);

		log.info("[Elastic With Query] : {} ms", elasticWithQuery / iterations);

		List<Product> p1 = productService.findProductsWithJpaByFullText(KEYWORD);
		List<ProductSearchDto> p2 = productService.findProductsWithDslByFullText(KEYWORD);

		List<Product> p3 = productService.findProductsWithJpaByLike(KEYWORD);
		List<ProductSearchDto> p4 = productService.findProductsWithDslByLike(KEYWORD);

		List<ProductDocument> p5 = productSearchService.findProductsWithElasticsearch(KEYWORD);

		// List 디버깅
		log.info("Test done");
	}

	private long measure(Runnable runnable) {
		Instant start = Instant.now();
		runnable.run();
		return Duration.between(start, Instant.now()).toMillis();
	}
}
