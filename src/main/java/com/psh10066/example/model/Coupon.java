package com.psh10066.example.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Coupon {

    private long couponId;
    private Integer totalQuantity;
    private LocalDateTime issueStartDate;
    private LocalDateTime issueEndDate;

    public static Coupon getTestInstance(long couponId) {
        LocalDateTime now = LocalDateTime.now();
        return new Coupon(couponId, 100, now.minusDays(1), now.plusDays(1));
    }

    public boolean availableIssueDate() {
        LocalDateTime now = LocalDateTime.now();
        return issueStartDate.isBefore(now) && issueEndDate.isAfter(now);
    }
}
