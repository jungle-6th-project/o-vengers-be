package jungle.ovengers.data;

import jungle.ovengers.entity.RankEntity;

import java.time.Duration;

public final class FakeRankInitializer {

    public static RankEntity of() {
        return RankEntity.builder()
                         .rankId(1L)
                         .deleted(false)
                         .duration(Duration.ofHours(1))
                         .groupId(1L)
                         .memberId(1L)
                         .build();
    }

    public static RankEntity of(Long rankId, Long groupId, Long memberId) {
        return RankEntity.builder()
                         .rankId(rankId)
                         .deleted(false)
                         .duration(Duration.ofHours(1))
                         .groupId(groupId)
                         .memberId(memberId)
                         .build();
    }
}
