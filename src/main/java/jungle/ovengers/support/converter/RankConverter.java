package jungle.ovengers.support.converter;

import jungle.ovengers.entity.GroupEntity;
import jungle.ovengers.entity.MemberEntity;
import jungle.ovengers.entity.RankEntity;
import jungle.ovengers.model.response.RankResponse;
import lombok.experimental.UtilityClass;

import java.time.Duration;

@UtilityClass
public final class RankConverter {
    public static RankResponse from(RankEntity rankEntity, MemberEntity memberEntity) {
        return RankResponse.builder()
                           .memberId(rankEntity.getMemberId())
                           .duration(rankEntity.getDuration())
                           .nickname(memberEntity.getName())
                           .profile(memberEntity.getProfile())
                           .build();
    }

    public static RankEntity to(MemberEntity memberEntity, GroupEntity groupEntity) {
        return RankEntity.builder()
                         .deleted(false)
                         .memberId(memberEntity.getId())
                         .groupId(groupEntity.getId())
                         .duration(Duration.ZERO)
                         .build();
    }
}
