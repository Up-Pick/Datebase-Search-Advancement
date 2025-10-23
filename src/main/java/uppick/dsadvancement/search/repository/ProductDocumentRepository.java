package uppick.dsadvancement.search.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import uppick.dsadvancement.search.document.ProductDocument;

public interface ProductDocumentRepository extends ElasticsearchRepository<ProductDocument, Long> {
}
