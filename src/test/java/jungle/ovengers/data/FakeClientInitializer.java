package jungle.ovengers.data;

import jungle.ovengers.entity.ClientEntity;

import java.time.LocalDateTime;

public final class FakeClientInitializer {

    public static ClientEntity of() {
        LocalDateTime now = LocalDateTime.now();
        return ClientEntity.builder()
                           .id(1L)
                           .fcmToken("fcmToken")
                           .memberId(1L)
                           .createdAt(now)
                           .updatedAt(now)
                           .build();
    }
}
