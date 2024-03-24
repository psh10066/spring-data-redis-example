package com.psh10066.example.controller;

import com.psh10066.example.controller.request.CouponIssueRequest;
import com.psh10066.example.service.CouponIssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CouponIssueController {

    private final CouponIssueService couponIssueService;

    /**
     * 동시성 처리 X
     */
    @PostMapping("/issue")
    public boolean issue(@RequestBody CouponIssueRequest request) {
        couponIssueService.issue(request.couponId(), request.userId());
        return true;
    }
}
