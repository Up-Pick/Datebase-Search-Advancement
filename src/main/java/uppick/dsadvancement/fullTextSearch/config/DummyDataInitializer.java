package uppick.dsadvancement.fullTextSearch.config;

import java.util.Random;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uppick.dsadvancement.fullTextSearch.entity.Product;
import uppick.dsadvancement.fullTextSearch.repository.ProductRepository;

@Slf4j
@Component
@RequiredArgsConstructor
@DependsOn({"fullTextIndexInitializer"})
public class DummyDataInitializer {

	private final ProductRepository productRepository;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@PostConstruct
	public void init() {
		long count = productRepository.count();
		if (count > 0) return;

		Random random = new Random();

		IntStream.range(0, 1_000_000).forEach(i -> {
			Product product = Product.builder()
				.productName("상품 "+(i % 10000)+"번 - "+random.nextInt(100))
				.content("테스트용 내용, 키워드 : " + (i%500))
				.build();
			productRepository.save(product);

			if (i%10000 == 0) {
				logger.info("Dummy Data Inserted: {}", i);
			}
		});
	}
}
