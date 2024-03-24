package com.psh10066.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.psh10066.example.component.DistributeLockExecutor;
import com.psh10066.example.model.Coupon;
import com.psh10066.example.model.CouponIssueInfoDTO;
import com.psh10066.example.repository.RedisRepository;
import com.psh10066.example.util.CouponRedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponIssueService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RedisRepository redisRepository;
    private final CouponCacheService couponCacheService;
    private final CouponIssueRedisService couponIssueRedisService;
    private final DistributeLockExecutor distributeLockExecutor;


    /**
     * 동시성 처리 X
     */
    public void issue(long couponId, long userId) {
        Coupon coupon = couponCacheService.getCoupon(couponId);
        if (!coupon.availableIssueDate()) {
            throw new RuntimeException("발급 가능한 날짜가 아닙니다.");
        }
        if (!couponIssueRedisService.availableTotalIssueQuantity(coupon.getTotalQuantity(), couponId)) {
            throw new RuntimeException("발급 가능한 쿠폰 개수를 초과하였습니다.");
        }
        if (!couponIssueRedisService.availableUserIssueQuantity(couponId, userId)) {
            throw new RuntimeException("이미 발급된 쿠폰입니다.");
        }
        this.issueRequest(couponId, userId);
    }

    /**
     * lock을 이용한 동시성 처리
     */
    public void issueWithLock(long couponId, long userId) {
        Coupon coupon = couponCacheService.getCoupon(couponId);
        if (!coupon.availableIssueDate()) {
            throw new RuntimeException("발급 가능한 날짜가 아닙니다.");
        }
        distributeLockExecutor.execute("lock_%s".formatted(couponId), 3000, 3000, () -> {
            if (!couponIssueRedisService.availableTotalIssueQuantity(coupon.getTotalQuantity(), couponId)) {
                throw new RuntimeException("발급 가능한 쿠폰 개수를 초과하였습니다.");
            }
            if (!couponIssueRedisService.availableUserIssueQuantity(couponId, userId)) {
                throw new RuntimeException("이미 발급된 쿠폰입니다.");
            }
            this.issueRequest(couponId, userId);
        });
    }

    /**
     * lua script를 이용한 동시성 처리
     */
    public void issueWithLuaScript(long couponId, long userId) {
        Coupon coupon = couponCacheService.getCoupon(couponId);
        if (!coupon.availableIssueDate()) {
            throw new RuntimeException("발급 가능한 날짜가 아닙니다.");
        }
        int totalQuantity = coupon.getTotalQuantity() != null ? coupon.getTotalQuantity() : Integer.MAX_VALUE;
        redisRepository.issueRequest(couponId, userId, totalQuantity);
    }

    @SneakyThrows
    private void issueRequest(long couponId, long userId) {
        CouponIssueInfoDTO couponIssueInfoDTO = new CouponIssueInfoDTO(couponId, userId);
        String value = objectMapper.writeValueAsString(couponIssueInfoDTO);
        redisRepository.sAdd(CouponRedisUtils.getIssueRequestKey(couponId), String.valueOf(userId));
        redisRepository.rPush(CouponRedisUtils.getIssueRequestQueueKey(), value);
    }
}
