## Like 검색의 한계

기본적인 검색을 사용할 경우  
```java
public interface ProductRepository extends JpaRepository<Product, Long>

@Query("""
    SELECT p
    FROM Product
    WHERE p.product_name LIKE %:keyword%
    """)
List<Product> findByTitleContaining(@Param("keyword") String keyword);
```
- 속도가 느림
- 검색 결과가 일부 안나오는 문제 발생

--- 

### 문제가 생기는 이유
1. 성능 저하
   `LIKE 'keyword%'`의 경우, k로 시작하는 지점으로 인덱스에서 바로 찾아 빠르게 검색할 수 있지만 `LIKE '%keyword'`는 무엇으로 시작하는지 알 수 없기에 Full Table Scan을 수행하여 성능 저하 발생
2. 정확성 문제  
   ```sql
    INSERT INTO product(product_name, content) VALUES ('상품 이름입니다.', '상품이름 찾아주세요');
    SELECT product_name, content FROM product WHERE product_name LIKE '%상품이름%';   
    ```
   "상품이름"이 완전히 일치하지 않기 때문에 조회 불가

---

## 전문 검색 기능 (Full Text Search)

1. 형태소/어근 분석 : **명사** 또는 **어근**으로 변환하여 분석  
   - 장점 : 검색 정확도가 매우 높음
   - 단점 : 언어별 '분석기'설정 필요
2. N-gram 분석 : **글자 수 단위**로 텍스트를 분해하여 분석  
   - 장점 : 모든 언어에 범용적으로 적용 가능
   - 단점 : 의미 없는 토큰이 생성될 수 있음

한국어 환경에 유연하고 별도 설정 없이 적용 가능한 `N-gram`을 중심으로 진행  

### `N-gram` 분해 예시 (N = 2)

1. 공백을 기준으로 분해
2. N을 기준으로 분해  

- "상품 이름 검색" -> "상품", "이름", "검색" -> 상품, 이름, 검색  
- "상품이름검색" -> "상품이름검색" -> 상품, 품이, 이름, 름검, 검색

### 사용 방법  
- 역 인덱스 (Inverted Index) 적용
  - 텍스트를 토큰 단위로 분해하여, 각 토큰이 어느 문서에 포함되는지 빠르게 검색할 수 있는 데이터 구조

텍스트 테이블 : 

| ID | Title    |
|----|----------|
| 1  | 상품 이름 검색 |
| 2  | 상품이름검색   |

역 인덱스 테이블 :

| Token | Document IDs |
|-------|--------------|
| 전문    | 1, 2         |
| 문검    | 2            |
| 검색    | 1, 2         |
| ...   | ...          |

---

### MySQL 에서 사용하기 위한 전제 조건

- MySQL 버전 : 5.7.6 이상
- 스토리지 엔진 : InnoDB / MyISAM
- 데이터 타입 : 'CHAR', 'VARCHAR', 'TEXT', 등 `문자형`만 가능

---

### Full Text Index 생성 및 사용법

- FULL TEXT INDEX 생성 문법  
  `FULLTEXT INDEX idx_product_name (product_name) WITH PARSER ngram`
- MATCH ... AGAINST 문법  
  `SELECT * FROM TABLE WHERE MATCH (열 이름) AGAINST ('키워드' {검색 모드});`

--- 

### 성능 비교

- 데이터 "상품_(숫자)_(숫자)" 100만개에서 "상품90"을 검색
- relevance score > 0.5 기준
  - Full Index Search 미적용시 결과 값이 존재하지 않음
    - JPA : 245 ms
    - QueryDSL : 58 ms
  - Full Index Search 적용시 4,082개의 데이터 조회 성공
    - JPA : 44 ms
    - DSL : 44 ms

---

### 주의 사항

```java
NumberTemplate<Float> relevanceScore = Expressions.numberTemplate(
    Float.class,
    "MATCH({0}) AGAINST ({1} IN NATURAL LANGUAGE MODE)",
    Expressions.stringPath("product_name"), keyword);
```

- QueryDSL 에서 유사도 점수를 필드에 넣어서 사용하려는 경우, 생성자 문제 발생

score을 포함한 `@AllArgsConstructor`과 score을 제외한 생성자 2개가 존재해야 함  
이유는 모름 뭐지

