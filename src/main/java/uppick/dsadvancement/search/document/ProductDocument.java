package uppick.dsadvancement.search.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uppick.dsadvancement.dsa.entity.Product;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setting(settingPath = "/static/elastic/elastic-settings.json")
@Mapping(mappingPath = "/static/elastic/product-mappings.json")
@Document(indexName = "product")
public class ProductDocument {

	@Id
	private Long id;

	@Field(name = "product_name", type = FieldType.Text, analyzer = "nori")
	private String productName;

	@Field(name = "content", type = FieldType.Text, analyzer = "nori")
	private String content;

	public static ProductDocument toDocument(Product product) {
		return new ProductDocument(
			product.getId(),
			product.getProductName(),
			product.getContent()
		);
	}
}
