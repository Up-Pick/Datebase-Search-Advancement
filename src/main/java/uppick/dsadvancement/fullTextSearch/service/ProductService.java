package uppick.dsadvancement.fullTextSearch.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uppick.dsadvancement.fullTextSearch.Dto.response.ProductSearchDto;
import uppick.dsadvancement.fullTextSearch.entity.Product;
import uppick.dsadvancement.fullTextSearch.repository.ProductQueryRepository;
import uppick.dsadvancement.fullTextSearch.repository.ProductRepository;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final ProductQueryRepository productQueryRepository;

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
}
