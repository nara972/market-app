package com.group.marketapp;

import com.group.marketapp.coupon.domain.Coupon;
import com.group.marketapp.coupon.domain.CouponType;
import com.group.marketapp.coupon.repository.CouponRepository;
import com.group.marketapp.coupon.repository.ReceivedCouponRepository;
import com.group.marketapp.coupon.service.CouponService;
import com.group.marketapp.user.domain.Users;
import com.group.marketapp.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class CouponServiceConcurrencyTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ReceivedCouponRepository receivedCouponRepository;

    @Value("${coupon.queue.key:coupon_queue}")
    private String COUPON_QUEUE;

    private static final int THREAD_COUNT = 50;

    @BeforeEach
    public void setUp() {
        // Create test coupon
        Coupon coupon = Coupon.builder()
                .name("Test Coupon")
                .couponType(CouponType.FIRST_COME_FIRST_SERVE)
                .quantity(25)
                .expiredDate(LocalDateTime.now().plusDays(1))
                .minimumMoney(1000)
                .discountPrice(500)
                .build();
        couponRepository.save(coupon);

        // Initialize Redis queue
        for (int i = 0; i < 25; i++) {
            redisTemplate.opsForList().leftPush(COUPON_QUEUE, coupon.getId().toString());
        }

        // Create test users
        for (int i = 0; i < THREAD_COUNT; i++) {
            userRepository.save(new Users(null, "user" + i, "password" + i, "User " + i, "Address", "ROLE_USER"));
        }
    }

    @Test
    public void testConcurrentCouponIssuance() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < THREAD_COUNT; i++) {
            int finalI = i;
            executorService.submit(() -> {
                try {
                    couponService.issueCoupon(1L, "user" + finalI);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // Assert the results
        int totalIssuedCoupons = receivedCouponRepository.findAll().size();
        Assertions.assertEquals(25, totalIssuedCoupons, "The number of issued coupons should match the quantity in Redis queue.");
        Assertions.assertEquals(25, successCount.get(), "The success count should equal the available coupons.");
        Assertions.assertEquals(THREAD_COUNT - 25, failureCount.get(), "The failure count should equal the excess requests beyond available coupons.");
    }

    @AfterEach
    public void tearDown() {
        // Clear Redis queue and database
        redisTemplate.delete(COUPON_QUEUE);
        receivedCouponRepository.deleteAll();
        couponRepository.deleteAll();
        userRepository.deleteAll();
    }
}
