package uppick.dsadvancement.dsa.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uppick.dsadvancement.dsa.Dto.response.ProductSearchDto;
import uppick.dsadvancement.dsa.entity.Product;
import uppick.dsadvancement.dsa.repository.ProductQueryRepository;
import uppick.dsadvancement.dsa.repository.ProductRepository;
import uppick.dsadvancement.search.document.ProductDocument;
import uppick.dsadvancement.search.repository.ProductDocumentRepository;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final ProductQueryRepository productQueryRepository;

	private final ProductDocumentRepository productDocumentRepository;

	public List<Product> findProductsWithJpaByLike(String keyword) {
		return productRepository.findProductsByLike(keyword);
	}
	public List<ProductSearchDto> findProductsWithDslByLike(String keyword) {
		return productQueryRepository.findProductsByLike(keyword);
	}

	public List<Product> findProductsWithJpaByFullText(String keyword) {
		return productRepository.findProductsByFullText(keyword);
	}
	public List<ProductSearchDto> findProductsWithDslByFullText(String keyword) {
		return productQueryRepository.findProductsByFullText(keyword);
	}

	@Transactional(readOnly = true)
	public void syncAllProductsToElasticsearch() {
		int batchSize = 1000;
		long totalCount = productRepository.count();
		long totalPages = (totalCount + batchSize - 1) / batchSize;

		for (int page = 0; page < totalPages; page++) {
			List<Product> products = productRepository.findAll(
				org.springframework.data.domain.PageRequest.of(page, batchSize)
			).getContent();

			List<ProductDocument> docs = products.stream()
				.map(ProductDocument::toDocument)
				.collect(Collectors.toList());

			productDocumentRepository.saveAll(docs);

			System.out.println("✅ Synced batch " + (page + 1) + "/" + totalPages);
		}

		System.out.println("🎉 All products synced to Elasticsearch!");
	}
}
