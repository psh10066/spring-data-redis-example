package com.psh10066.example.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CouponIssueInfoDTO {

    private final long couponId;
    private final long userId;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private final LocalDateTime issueRequestDate;

    public CouponIssueInfoDTO(long couponId, long userId) {
        this.couponId = couponId;
        this.userId = userId;
        this.issueRequestDate = LocalDateTime.now();
    }
}
