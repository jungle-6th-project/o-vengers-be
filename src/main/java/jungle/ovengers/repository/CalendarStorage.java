package jungle.ovengers.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CalendarStorage {

    private final RedisTemplate<String, String> redisTemplate;

    public void deleteGroupCacheEntry(Long groupId) {
        String cacheKey = "groupRooms::" + groupId;
        redisTemplate.delete(cacheKey);
    }
}
