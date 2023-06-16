package jungle.ovengers.support.converter;

import jungle.ovengers.entity.MemberEntity;
import jungle.ovengers.entity.RankEntity;
import jungle.ovengers.model.response.RankResponse;
import lombok.experimental.UtilityClass;

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
}
