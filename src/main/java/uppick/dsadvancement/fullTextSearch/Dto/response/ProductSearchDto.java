package uppick.dsadvancement.fullTextSearch.Dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProductSearchDto {
	private Long id;
	private String productName;
	private String content;
	private Float score;

	public ProductSearchDto(Long id, String productName, String content) {
		this.id = id;
		this.productName = productName;
		this.content = content;
		this.score = null; // 기본 null
	}
}
