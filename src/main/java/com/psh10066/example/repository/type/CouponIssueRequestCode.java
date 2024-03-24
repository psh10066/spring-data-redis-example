package com.psh10066.example.repository.type;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CouponIssueRequestCode {
    SUCCESS(1),
    INVALID_COUPON_ISSUE_QUANTITY(2),
    DUPLICATE_COUPON_ISSUE(3);

    public final int code;

    public static CouponIssueRequestCode find(String code) {
        return switch (code) {
            case "1" -> SUCCESS;
            case "2" -> INVALID_COUPON_ISSUE_QUANTITY;
            case "3" -> DUPLICATE_COUPON_ISSUE;
            default -> throw new IllegalArgumentException();
        };
    }

    public static void checkRequestResult(CouponIssueRequestCode code) {
        if (code == INVALID_COUPON_ISSUE_QUANTITY) {
            throw new RuntimeException("발급 가능한 쿠폰 개수를 초과하였습니다.");
        }
        if (code == DUPLICATE_COUPON_ISSUE) {
            throw new RuntimeException("이미 발급된 쿠폰입니다.");
        }
    }
}
