package jungle.ovengers.data;

import jungle.ovengers.entity.GroupEntity;

import java.time.LocalDateTime;

public final class FakeGroupInitializer {

    public static GroupEntity of() {
        return GroupEntity.builder()
                          .id(1L)
                          .ownerId(1L)
                          .path("path")
                          .groupName("groupName")
                          .isSecret(false)
                          .createdAt(LocalDateTime.now())
                          .deleted(false)
                          .build();
    }

    public static GroupEntity of(Long groupId, Long ownerId) {
        return GroupEntity.builder()
                          .id(groupId)
                          .ownerId(ownerId)
                          .path("path")
                          .groupName("groupName")
                          .isSecret(false)
                          .createdAt(LocalDateTime.now())
                          .deleted(false)
                          .build();
    }

}
