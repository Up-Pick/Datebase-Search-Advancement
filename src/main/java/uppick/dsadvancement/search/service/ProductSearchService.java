package uppick.dsadvancement.search.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Service;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import lombok.RequiredArgsConstructor;
import uppick.dsadvancement.search.document.ProductDocument;

@Service
@RequiredArgsConstructor
public class ProductSearchService {

	private final ElasticsearchOperations elasticsearchOperations;

	public List<ProductDocument> findProductsWithElasticsearch(String keyword) {

		NativeQuery query = NativeQuery.builder()
			.withQuery(
				Query.of(q -> q
					.match(m -> m
						.field("product_name")
						.query(keyword)
						.fuzziness("AUTO"))))
			.withPageable(PageRequest.of(0, 1000))
			.build();

		return elasticsearchOperations.search(query, ProductDocument.class)
			.map(SearchHit::getContent)
			.toList();
	}
}
