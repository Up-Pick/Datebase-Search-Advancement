package uppick.dsadvancement.dsa.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uppick.dsadvancement.dsa.Dto.response.ProductSearchDto;
import uppick.dsadvancement.dsa.entity.Product;
import uppick.dsadvancement.dsa.repository.ProductQueryRepository;
import uppick.dsadvancement.dsa.repository.ProductRepository;

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
