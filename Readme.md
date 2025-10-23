### 성능 비교

- "[수식어] [명사] [숫자]" 데이터 100만개에 대해 "123번 신발"을 검색
- relevance score > 0.0
- 10회 조회 시 평균값 계산
    - Full Index Search 미적용시 결과 값이 존재하지 않음
        - JPA : 389 ms
        - QueryDSL : 395 ms
    - Full Index Search 적용시 데이터 조회 성공, N-gram : 1
        - JPA : 229 ms
        - QueryDSL : 213 ms
    - Elasticsearch 적용시 데이터 조회 성공
        - 46 ms

    