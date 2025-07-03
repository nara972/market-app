package com.group.marketapp.service;

import com.group.marketapp.domain.*;
import com.group.marketapp.dto.requestdto.CreateCouponRequestDto;
import com.group.marketapp.dto.requestdto.UpdateCouponRequestDto;
import com.group.marketapp.dto.responsedto.CouponResponseDto;
import com.group.marketapp.dto.responsedto.ProductResponseDto;
import com.group.marketapp.repository.CouponRepository;
import com.group.marketapp.repository.ReceivedCouponRepository;
import com.group.marketapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final ReceivedCouponRepository receivedCouponRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String,Object> redisTemplate;
    private final RedissonClient redissonClient;
    private static final String COUPON_QUEUE="coupon_queue";

    public void createCoupon(CreateCouponRequestDto request){

        if(request.getCouponType()==CouponType.FIRST_COME_FIRST_SERVE){
            if(request.getQuantity() == null || request.getQuantity()<=0){
                throw new IllegalArgumentException("The quantity must be specified");
            }
        }

        Coupon coupon = Coupon.builder()
                        .name(request.getName())
                        .couponType(request.getCouponType())
                        .quantity(request.getCouponType()==CouponType.FIRST_COME_FIRST_SERVE? request.getQuantity():0)
                        .expiredDate(request.getExpiredDate())
                        .minimumMoney(request.getMinimumMoney())
                        .discountPrice(request.getDiscountPrice()).build();

        couponRepository.save(coupon);

        //선착순 쿠폰일 경우 Redis 대기열에 추가
        if(request.getCouponType()==CouponType.FIRST_COME_FIRST_SERVE){
            for(int i=0;i<request.getQuantity();i++){
                redisTemplate.opsForList()
                        .leftPush(COUPON_QUEUE,String.valueOf(coupon.getId()));
            }
        }
    }

    // 쿠폰 상세 조회
    public CouponResponseDto getCoupon(Long id){
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);

        return CouponResponseDto.of(coupon);

    }

    // 로그인하지 않은 사용자 또는 일반 사용자
    public List<CouponResponseDto> getActiveCoupons() {
        return couponRepository.findByIsActiveTrue().stream()
                .map(CouponResponseDto::of)
                .collect(Collectors.toList());
    }

    // 관리자만 전체 쿠폰 조회 가능
    public List<CouponResponseDto> getAllCoupons(String loginId) {
        Users user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return couponRepository.findAll().stream()
                .map(CouponResponseDto::of)
                .collect(Collectors.toList());
    }


    public void updateCoupon(Long id,UpdateCouponRequestDto request){
        Coupon coupon=couponRepository.findById(id).orElseThrow(IllegalArgumentException::new);

        if(coupon.getCouponType()==CouponType.FIRST_COME_FIRST_SERVE){
            if(request.getQuantity()==null || request.getQuantity()<=0){
                throw new IllegalArgumentException("The quantity must be specified");
            }
        }

        coupon.update(request);
        couponRepository.save(coupon);

        if(coupon.getCouponType()==CouponType.FIRST_COME_FIRST_SERVE){
            redisTemplate.delete(COUPON_QUEUE);
            for(int i=0;i<request.getQuantity();i++){
                redisTemplate.opsForList().leftPush(COUPON_QUEUE,String.valueOf(coupon.getId()));
            }
        }

        System.out.println("서비스에서 받은 isActive: " + coupon.isActive());
    }

    @Scheduled(cron = "0 0 12 * * *")
    @Transactional
    public void deactivateExpiredCoupons(){
        List<Coupon> expiredCoupons = couponRepository.findExpiredCoupons();
        for(Coupon coupon : expiredCoupons){
            coupon.deactivate();
        }
        couponRepository.saveAll(expiredCoupons);
    }

     //선착순 쿠폰 발급 메서드
     @Transactional
     public CouponResponseDto issueCoupon(Long couponId, String loginId) {

         String lockKey = "lock:coupon:" + couponId; // 락 키 생성
         RLock lock = redissonClient.getLock(lockKey);

         try {
             if (lock.tryLock(5, 10, TimeUnit.SECONDS)) { // 5초 대기, 10초 유지
                 try {
                     // Redis 대기열에서 쿠폰 ID를 꺼냄
                     Object queuedCouponId = redisTemplate.opsForList().rightPop(COUPON_QUEUE);
                     if (queuedCouponId == null) {
                         throw new IllegalStateException("No coupons available for issuance.");
                     }

                     // 쿠폰 ID를 사용해 데이터베이스에서 쿠폰을 조회하고 활성화 여부 확인
                     Coupon coupon = couponRepository.findById(couponId)
                             .orElseThrow(() -> new IllegalArgumentException("Coupon not found"));

                     if (!coupon.isActive()) {
                         throw new IllegalStateException("Coupon is not active");
                     }

                     Users user = userRepository.findByLoginId(loginId)
                             .orElseThrow(() -> new IllegalArgumentException("User not found"));

                     // Redis의 Set을 이용해 중복 수령 여부 확인
                     String redisKey = "coupon:issued:" + couponId;
                     Boolean isAlreadyIssued = redisTemplate.opsForSet().isMember(redisKey, loginId);
                     if (Boolean.TRUE.equals(isAlreadyIssued)) {
                         throw new IllegalStateException("Coupon is already issued");
                     }

                     // Redis에 발급 기록 추가
                     redisTemplate.opsForSet().add(redisKey, loginId);

                     // ReceivedCoupon 엔티티에 저장
                     ReceivedCoupon receivedCoupon = new ReceivedCoupon(coupon, user);
                     receivedCouponRepository.save(receivedCoupon);

                     return CouponResponseDto.of(coupon);
                 } finally {
                     lock.unlock();
                 }
             } else {
                 throw new IllegalStateException("Could not acquire lock for coupon issuance");
             }
         } catch (InterruptedException e) {
             throw new IllegalStateException("Thread interrupted while acquiring lock", e);
         }
     }


}
