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

- Java, SpringBoot, Spring Security
- JPA, MySQL
- Redis, Elasticsearch
- Docker
- Swagger
<br>

## **개발 주요 사항**

1. **JWT 인증**
- Spring Security를 활용한 JWT 기반 인증 및 Refresh Token 관리
- Refresh Token을 Redis 캐싱으로 관리하여 DB 부하 감소 및 성능 향상
2. **Redis 활용**
- Redis를 활용하여 사용자 세션 관리

- Redis List를 활용한 대기열 처리  
  <details>
    <summary>상세 내용 보기</summary>

    - Redis List를 활용해 선착순 쿠폰 요청을 대기열(FIFO)로 관리  
    - 요청은 Redis 대기열에 저장되고, 순차적으로 처리  
    - 이를 통해 충돌 가능성을 줄이고 데이터베이스 접근 빈도를 줄여 시스템 성능 최적화
      ```java
      if(request.getCouponType()==CouponType.FIRST_COME_FIRST_SERVE){
            for(int i=0;i<request.getQuantity();i++){
                redisTemplate.opsForList()
                        .leftPush(COUPON_QUEUE,String.valueOf(coupon.getId()));
            }
        }
  </details>

- Redis Set을 활용한 중복 방지  
  <details>
    <summary>상세 내용 보기</summary>

    - Redis Set으로 사용자 발급 이력을 저장하여 중복 요청 차단  
    - 쿠폰 발급이 완료되면 사용자 ID를 Redis Set에 기록  
    - 요청 시 Redis Set에서 사용자 ID의 존재 여부를 확인하여 중복 발급을 방지
      ```java
      String redisKey = "coupon:issued:" + couponId;
                     Boolean isAlreadyIssued = redisTemplate.opsForSet().isMember(redisKey, loginId);
                     if (Boolean.TRUE.equals(isAlreadyIssued)) {
                         throw new IllegalStateException("Coupon is already issued");
                     }
     
  </details>

- Redisson 분산락을 사용하여 동시성 문제 해결

- Spring Cache와 Redis를 통해 자주 조회되는 데이터를 메모리에 저장하여 성능 최적화
3. **스케줄링**
- 만료된 쿠폰 자동 비활성화 스케줄러 기능 구현
4. **Elasticsearch 검색**
- 상품명 및 카테고리 검색: Elasticsearch를 활용하여 효율적이고 빠른 검색 기능 구현
- 가격 필터링 및 정렬: Elasticsearch를 통해 가격 필터링 및 정렬 기능 추가
    
<br>

## **트러블 슈팅 및 개선**
<details>
  <summary>카테고리-상품 간 양방향 참조로 인한 StackOverflowError</summary>

  1. 문제:
      - `ProductResponseDto`에서 `ProductCategory`를 포함한 데이터 직렬화 시, 양방향 참조로 인해 무한 순환이 발생
      - Jackson 라이브러리에서 `StackOverflowError` 발생

  2. 원인:
      - `Product`와 `ProductCategory` 간 양방향 관계를 JSON 직렬화 시 그대로 사용
      - 상위-하위 관계가 반복적으로 참조되며 무한 루프 발생

  3. 해결 방안:
      - DTO 활용: `ProductCategory` 정보를 단순화하여 포함
</details>

<details>
  <summary>N+1 문제</summary>

  1. 문제 상황:  
      - 기존에 `ProductService`의 `getProductCategory` 메서드에서 카테고리에 속한 상품을 조회할 때 N+1 문제가 발생
      - 각 상품의 서브카테고리를 개별적으로 호출하여 추가 쿼리가 불필요하게 실행됨
      - 조회 성능 저하 및 데이터베이스 부하 증가

  2. 해결 방안:  
      - `ProductRepository`의 기존 메서드를 JPQL의 `JOIN FETCH`를 활용하여 개선
      - 상품과 카테고리를 한 번의 쿼리로 조회하도록 최적화

</details>

<details>
  <summary>1차 상품 재고 감소 시 동시성 이슈 발생: 비관적 락 적용</summary>

  1. 문제 상황:  
      - 특정 상품의 재고를 동시에 감소시키는 요청이 처리되는 과정에서 동시성 이슈 발생
        <br><br>
      <img src=https://github.com/user-attachments/assets/29a2a599-5690-4f96-b9ed-babca8031ce9  width="200">

  2. 해결 방안:  
      - 상품 조회 시 비관적 락 적용:
        - ProductRepository에 비관적 락을 사용하는 쿼리를 추가하여 재고 업데이트 중 다른 트랜잭션이 접근하지 못하도록 설정  

        ```java
        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("select p from Product p where p.id =:id and p.isDeleted = false")
        Optional<Product> findByIdWithLock(@Param("id") Long id);
        ```

      - 주문 생성 로직 수정:
        - 주문 처리 시 `findByIdWithLock`을 호출하여 동시에 한 스레드만 접근 가능하도록 보장
</details>

<details>
  <summary>2차 상품 재고 감소 시 동시성 이슈 개선: Redisson의 분산락 적용</summary>

  1. 문제 상황  
     - 비관적 락은 데이터베이스 수준에서 트랜잭션을 지속적으로 유지해야 하므로, DB에 큰 부하를 줄 수 있다고 판단되었습니다.  

  2. 해결 방안  
     - Redisson 분산 락을 도입하여 Redis 기반으로 락 관리를 개선하였습니다.  
     - 락을 Redis에서 관리함으로써 데이터베이스 부하를 줄이고, 락 처리 속도를 높였습니다.  

  3. 성능 테스트 결과
     <br><br>
     <img src="https://github.com/user-attachments/assets/ab453d30-6546-4261-8846-db74f4482923" width="200">

</details>
<br>

## **DB ERD**
<img src="https://github.com/user-attachments/assets/8eebc598-bdab-4ef5-8296-8f525998eb96" alt="마켓 ERD" width="400">

## **API 문서**
[swagger](https://nara972.github.io/market-app-swagger-ui) <br><br>
<img src="https://github.com/user-attachments/assets/19f412c9-2729-45fd-8a67-f5acdefb96d1" 
    width="600">



