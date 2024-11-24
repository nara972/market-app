package com.group.marketapp.coupon.service;

import com.group.marketapp.coupon.domain.Coupon;
import com.group.marketapp.coupon.domain.CouponType;
import com.group.marketapp.coupon.domain.ReceivedCoupon;
import com.group.marketapp.coupon.dto.request.CreateCouponRequestDto;
import com.group.marketapp.coupon.dto.request.UpdateCouponRequestDto;
import com.group.marketapp.coupon.dto.response.CouponResponseDto;
import com.group.marketapp.coupon.repository.CouponRepository;
import com.group.marketapp.coupon.repository.ReceivedCouponRepository;
import com.group.marketapp.user.domain.Users;
import com.group.marketapp.user.repository.UserRepository;
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

    public List<CouponResponseDto> getCoupon(){
        return couponRepository.findAll().stream()
                .map(CouponResponseDto::of)
                .collect(Collectors.toList());
    }

    public void updateCoupon(UpdateCouponRequestDto request){
        Coupon coupon=couponRepository.findById(request.getId()).orElseThrow(IllegalArgumentException::new);

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
    }

    @Scheduled(cron = "0 0 12 * * *")
    @Transactional
    public void deactivateExpiredCoupons(){
        List<Coupon> coupons = couponRepository.findAll();
        for(Coupon coupon : coupons){
            coupon.checkAndDeactivateIfExpired();
        }
        couponRepository.saveAll(coupons);
    }

     //선착순 쿠폰 발급 메서드
     @Transactional
     public CouponResponseDto issueCoupon(Long couponId,String loginId){
     //Redis 대기열에서 쿠폰 ID를 꺼냄
     Object queuedCouponId=redisTemplate.opsForList().rightPop(COUPON_QUEUE);
     if(couponId==null){
     throw new IllegalStateException("No coupons available for issuance.");
     }
     //쿠폰ID를 사용해 데이터베이스에서 쿠폰을 조회하고 활성화 여부 확인
     Coupon coupon = couponRepository.findById(couponId)
     .orElseThrow(()->new IllegalArgumentException("Coupon not found"));


     if(!coupon.isActive()){
     throw new IllegalStateException("Coupon is not active");
     }

     Users user = userRepository.findByLoginId(loginId)
     .orElseThrow(()->new IllegalArgumentException("User not found"));

     // Redis의 Set을 이용해 중복 수령 여부 확인
     String redisKey = "coupon:issued:"+couponId;
     Boolean isAlreadyIssued = redisTemplate.opsForSet().isMember(redisKey,loginId);
     if(Boolean.TRUE.equals(isAlreadyIssued)){
         throw new IllegalStateException("Coupon is already issued");
     }

     redisTemplate.opsForSet().add(redisKey,loginId);

     //ReceivedCoupon 엔티티에 저장
     ReceivedCoupon receivedCoupon = new ReceivedCoupon(coupon,user);
     receivedCouponRepository.save(receivedCoupon);

     return CouponResponseDto.of(coupon);
     }

}
