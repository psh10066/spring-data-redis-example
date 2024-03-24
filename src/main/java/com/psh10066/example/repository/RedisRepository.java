package com.psh10066.example.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.psh10066.example.model.CouponIssueInfoDTO;
import com.psh10066.example.repository.type.CouponIssueRequestCode;
import com.psh10066.example.util.CouponRedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RedisRepository {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RedisTemplate<String, String> redisTemplate;
    private final RedisScript<String> issueScript = this.issueRequestScript();
    private final String issueRequestQueueKey = CouponRedisUtils.getIssueRequestQueueKey();

    public Long sAdd(String key, String value) {
        return redisTemplate.opsForSet().add(key, value);
    }

    public Long sCard(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    public Boolean sIsMember(String key, String value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    public Long rPush(String key, String value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    @SneakyThrows
    public void issueRequest(long couponId, long userId, int totalIssueQuantity) {
        String issueRequestKey = CouponRedisUtils.getIssueRequestKey(couponId);
        CouponIssueInfoDTO couponIssueInfoDTO = new CouponIssueInfoDTO(couponId, userId);

        String code = redisTemplate.execute(
            issueScript,
            List.of(issueRequestKey, issueRequestQueueKey),
            String.valueOf(totalIssueQuantity),
            String.valueOf(userId),
            objectMapper.writeValueAsString(couponIssueInfoDTO));

        CouponIssueRequestCode.checkRequestResult(CouponIssueRequestCode.find(code));
    }

    private RedisScript<String> issueRequestScript() {
        String script = """
            if tonumber(ARGV[1]) <= redis.call('SCARD', KEYS[1]) then
                return '2'
            end

            if redis.call('SISMEMBER', KEYS[1], ARGV[2]) == 1 then
                return '3'
            end

            redis.call('SADD', KEYS[1], ARGV[2])
            redis.call('RPUSH', KEYS[2], ARGV[3])
            return '1'
            """;
        return RedisScript.of(script, String.class);
    }
}
