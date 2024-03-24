package com.psh10066.example.service;

import com.psh10066.example.model.Coupon;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponCacheService {

    @SneakyThrows
    @Cacheable("coupon")
    public Coupon getCoupon(long couponId) {
        log.info(couponId + "번 쿠폰 DB 조회중...");
        Thread.sleep(Duration.ofSeconds(3)); // DB 조회 3초 가정
        log.info(couponId + "번 쿠폰 DB 조회 완료!");
        return Coupon.getTestInstance(couponId);
    }
}
