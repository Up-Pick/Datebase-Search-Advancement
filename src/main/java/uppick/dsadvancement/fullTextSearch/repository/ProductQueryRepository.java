package uppick.dsadvancement.fullTextSearch.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.sql.SQLQueryFactory;

import lombok.RequiredArgsConstructor;
import uppick.dsadvancement.fullTextSearch.Dto.response.ProductSearchDto;
import static uppick.dsadvancement.fullTextSearch.entity.QProduct.product;

@Repository
@RequiredArgsConstructor
public class ProductQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;
	private final SQLQueryFactory sqlQueryFactory;

	public List<ProductSearchDto> findProductsByFullText(String keyword) {

		NumberTemplate<Float> relevanceScore = Expressions.numberTemplate(
			Float.class,
			"MATCH({0}) AGAINST ({1} IN NATURAL LANGUAGE MODE)",
			Expressions.stringPath("product_name"), keyword);

		return sqlQueryFactory
			.select(
				Projections.constructor(
					ProductSearchDto.class,
					product.id,
					Expressions.stringPath("product_name"),
					product.content,
					relevanceScore))
			.from(product)
			.where(relevanceScore.gt(0.5f))
			.orderBy(relevanceScore.desc())
			.fetch();
	}

	public List<ProductSearchDto> findProductsByLike(String keyword) {
		return jpaQueryFactory
			.select(
				Projections.constructor(
					ProductSearchDto.class,
					product.id,
					product.productName,
					product.content))
			.from(product)
			.where(product.productName.like("%" + keyword + "%"))
			.fetch();
	}
}
