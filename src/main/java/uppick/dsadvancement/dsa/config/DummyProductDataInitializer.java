package uppick.dsadvancement.dsa.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uppick.dsadvancement.dsa.entity.Product;
import uppick.dsadvancement.dsa.repository.ProductRepository;
import uppick.dsadvancement.search.document.ProductDocument;
import uppick.dsadvancement.search.repository.ProductDocumentRepository;

@Slf4j
@Component
@RequiredArgsConstructor
@DependsOn({"fullTextIndexInitializer"})
public class DummyProductDataInitializer {

	private final ProductRepository productRepository;
	private final ProductDocumentRepository productDocumentRepository;
	private final ElasticsearchOperations elasticsearchOperations;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final int BATCH_SIZE = 1000;
	private static final int TOTAL = 1_000_000;

	@PostConstruct
	public void init() {
		long count = productRepository.count();
		if (count > 0) return;

		String[] adjectives = {"좋은", "멋진", "신형", "한정", "인기"};
		String[] categories = {"가방", "신발", "모자", "셔츠", "휴대폰"};
		Random random = new Random();

		List<ProductDocument> docs = new ArrayList<>();

		IntStream.range(0, TOTAL).forEach(i -> {
			String productName = adjectives[random.nextInt(adjectives.length)] + " " +
				categories[random.nextInt(categories.length)] + " " +
				random.nextInt(1000);

			// 1️⃣ MySQL에 저장 후 반환 entity 사용
			Product saved = productRepository.save(Product.builder()
				.productName(productName)
				.content("테스트용 내용, 키워드 : " + (i % 500))
				.build());

			// 2️⃣ Elasticsearch용 document 생성
			ProductDocument doc = ProductDocument.toDocument(saved);
			docs.add(doc);

			// 3️⃣ 배치 단위로 Elasticsearch 저장
			if (docs.size() >= BATCH_SIZE) {
				productDocumentRepository.saveAll(docs);
				// 저장 후 즉시 refresh
				elasticsearchOperations.indexOps(ProductDocument.class).refresh();
				docs.clear();
			}

			// 4️⃣ 진행 로그
			if (i % 10000 == 0) {
				logger.info("Inserted {} records", i);
			}
		});

		// 남은 문서 처리
		if (!docs.isEmpty()) {
			productDocumentRepository.saveAll(docs);
			elasticsearchOperations.indexOps(ProductDocument.class).refresh();
		}

		logger.info("Dummy Data Insertion Complete! Total documents in ES: {}", productDocumentRepository.count());
	}
}