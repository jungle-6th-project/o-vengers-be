package jungle.ovengers.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class TokenStorage {
    private static final String REDIS_KEY_PREFIX = "refreshToken: ";
    @Value("${security.jwt.token.validtime.refresh}")
    private Long EXPIRATION_TIME_SECONDS;

    private final RedisTemplate<String, String> redisTemplate;

    public void storeRefreshToken(String refreshToken, Long memberId) {
        String redisKey = REDIS_KEY_PREFIX + memberId;
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(redisKey, refreshToken, EXPIRATION_TIME_SECONDS, TimeUnit.SECONDS);
    }

    public String getRefreshToken(Long memberId) {
        String redisKey = REDIS_KEY_PREFIX + memberId;
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(redisKey);
    }

    public void deleteRefreshToken(Long memberId) {
        String redisKey = REDIS_KEY_PREFIX + memberId;
        redisTemplate.delete(redisKey);
    }
}
