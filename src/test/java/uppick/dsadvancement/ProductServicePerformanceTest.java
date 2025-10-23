package uppick.dsadvancement;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

		int virtualThreads = 10;
		int iterationsPerThread = 10;

		try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

			List<Callable<long[]>> tasks = new ArrayList<>();

			for (int t = 0; t < virtualThreads; t++) {
				tasks.add(() -> {
					long jpaWithFullText = 0;
					long dslWithFullText = 0;
					long jpaWithLike = 0;
					long dslWithLike = 0;
					long elasticWithQuery = 0;

					for (int i = 0; i < iterationsPerThread; i++) {
						jpaWithFullText += measure(() -> productService.findProductsWithJpaByFullText(KEYWORD));
						dslWithFullText += measure(() -> productService.findProductsWithDslByFullText(KEYWORD));
						jpaWithLike += measure(() -> productService.findProductsWithJpaByLike(KEYWORD));
						dslWithLike += measure(() -> productService.findProductsWithDslByLike(KEYWORD));
						elasticWithQuery += measure(() -> productSearchService.findProductsWithElasticsearch(KEYWORD));
					}

					return new long[]{jpaWithFullText, dslWithFullText, jpaWithLike, dslWithLike, elasticWithQuery};
				});
			}

			// Virtual Thread로 병렬 실행
			List<Future<long[]>> results = executor.invokeAll(tasks);

			long jpaWithFullText = 0, dslWithFullText = 0, jpaWithLike = 0, dslWithLike = 0, elasticWithQuery = 0;

			for (Future<long[]> f : results) {
				long[] arr = f.get();
				jpaWithFullText += arr[0];
				dslWithFullText += arr[1];
				jpaWithLike += arr[2];
				dslWithLike += arr[3];
				elasticWithQuery += arr[4];
			}

			int totalIterations = virtualThreads * iterationsPerThread;

			log.info("[JPA With FullText Average] : {} ms", jpaWithFullText / totalIterations);
			log.info("[DSL With FullText Average] : {} ms", dslWithFullText / totalIterations);
			log.info("[JPA With LIKE Average] : {} ms", jpaWithLike / totalIterations);
			log.info("[DSL With LIKE Average] : {} ms", dslWithLike / totalIterations);
			log.info("[Elastic With Query Average] : {} ms", elasticWithQuery / totalIterations);

			log.info("Performance test completed.");

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

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
