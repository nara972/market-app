# **마켓 플랫폼**

<br>

## **프로젝트 개요**

기존 단순한 기능 구현에서 벗어나, 효율적이고 확장 가능한 플랫폼 개발을 목표로 하였습니다. 이를 위해 다양한 기술을 학습하고 적용하며 성능 개선과 안정성을 높이는 데 중점을 두었습니다.
<br><br>
## **개발 기간**

**2024년 06월 21일 ~ 진행 중**

<br>

## **주요 기능**

### **사용자 관리**

- JWT 기반 회원가입, 로그인, 로그아웃
- 사용자 역할(Role) 기반 권한 관리(일반 사용자/관리자)

### **상품 관리**

- 상품 등록, 수정, 삭제 및 단일/카테고리별 조회
- Elasticsearch 기반 상품 검색 및 조건 필터링

### **주문 처리**

- 주문 생성, 취소

### **쿠폰 관리**

- 쿠폰 생성 및 수정
- 만료 상태 자동 업데이트
- 선착순 쿠폰 발급 시스템 구현
<br>

## **기술 스택**

### **Back-end**

- **Java, SpringBoot, Spring Security**
- **JPA, MySQL**
- **Redis, Elasticsearch**
- **Swagger**
<br>

## **개발 주요 사항**

1. **JWT 인증**
    - Spring Security를 활용한 JWT 기반 인증 및 Refresh Token 관리
    - Refresh Token을 Redis 캐싱으로 관리하여 DB 부하 감소 및 성능 향상
2. **Redis 활용**
    - 세션 관리: Redis를 활용하여 사용자 세션 효율적으로 관리
    - 대기열: 선착순 쿠폰 발급 및 비동기 대기열 구현
    - 캐시: Spring Cache와 Redis를 통해 자주 조회되는 데이터를 메모리에 저장하여 성능 최적화
    - 분산 락: Redisson을 사용하여 동시성 문제 해결
3. **스케줄링**
    - 만료된 쿠폰 자동 비활성화 스케줄러 기능 구현
4. **동시성 문제 해결**
    - 쿠폰 발급 및 주문 처리 과정에서 발생 가능한 동시성 문제를 개선
5. **Elasticsearch 검색**
    - 상품명 및 카테고리 검색: Elasticsearch를 활용하여 효율적이고 빠른 검색 기능 구현
    - 가격 필터링 및 정렬: Elasticsearch를 통해 가격 필터링 및 정렬 기능 추가
    
<br>

## **트러블 슈팅 및 개선**

1. **양방향 참조로 인한 문제**
    - 카테고리-상품 간 양방향 참조로 인해 발생한 `StackOverflowError`를 해결하기 위해 데이터 모델 설계 수정
2. **N+1 문제 해결**
    - JPQL의 JOIN FETCH를 활용하여 조회 성능을 최적화
3. **Redis 대기열 및 중복 방지**
    - 비동기 작업 처리 및 중복 방지를 Redis로 구현
4. **캐시 활용**
    - 자주 조회되는 데이터를 캐싱하여 성능 최적화

## **DB ERD**
<img src="https://github.com/user-attachments/assets/8eebc598-bdab-4ef5-8296-8f525998eb96" alt="마켓 ERD" width="400">

## **API 문서**
[swagger](https://nara972.github.io/market-app-swagger-ui) <br><br>
<img src="https://github.com/user-attachments/assets/19f412c9-2729-45fd-8a67-f5acdefb96d1" 
    width="600">



