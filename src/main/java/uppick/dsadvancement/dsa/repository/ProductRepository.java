package uppick.dsadvancement.dsa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import uppick.dsadvancement.dsa.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	@Query(value = """
		SELECT *, MATCH(product_name) AGAINST(:keyword IN NATURAL LANGUAGE MODE) AS relevance
		FROM product
		WHERE MATCH(product_name) AGAINST(:keyword IN NATURAL LANGUAGE MODE) > 0.0
		ORDER BY relevance DESC
		LIMIT 1000
		""", nativeQuery = true)
	List<Product> findProductsByFullText(@Param("keyword") String keyword);

	@Query(value = """
		SELECT *
		FROM product
		WHERE product_name LIKE CONCAT('%', :keyword, '%')
		LIMIT 1000
    	""", nativeQuery = true)
	List<Product> findProductsByLike(@Param("keyword") String keyword);
}
