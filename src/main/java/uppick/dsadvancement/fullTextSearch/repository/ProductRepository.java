package uppick.dsadvancement.fullTextSearch.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import uppick.dsadvancement.fullTextSearch.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	@Query(value = """
		SELECT *, MATCH(product_name) AGAINST(:keyword IN NATURAL LANGUAGE MODE) AS relevance
		FROM product
		WHERE MATCH(product_name) AGAINST(:keyword IN NATURAL LANGUAGE MODE) > 0.5
		ORDER BY relevance DESC
		""", nativeQuery = true)
	List<Product> findProductsByFullText(@Param("keyword") String keyword);

	@Query(value = """
		SELECT *
		FROM product
		WHERE product_name LIKE CONCAT('%', :keyword, '%')
    	""", nativeQuery = true)
	List<Product> findProductsByLike(@Param("keyword") String keyword);
}
