package jungle.ovengers.service;

import jungle.ovengers.config.security.AuditorHolder;
import jungle.ovengers.data.FakeMemberInitializer;
import jungle.ovengers.data.FakeRankInitializer;
import jungle.ovengers.entity.MemberEntity;
import jungle.ovengers.entity.RankEntity;
import jungle.ovengers.model.request.RankBrowseRequest;
import jungle.ovengers.model.response.RankResponse;
import jungle.ovengers.repository.MemberRepository;
import jungle.ovengers.repository.RankRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RankServiceTest {

    @Mock
    private RankRepository rankRepository;
    @Mock
    private MemberRepository memberRepository;
    @InjectMocks
    private RankService rankService;

    private Long groupId;
    private RankEntity firstRankEntity;
    private RankEntity secondRankEntity;
    private MemberEntity firstMemberEntity;
    private MemberEntity secondMemberEntity;

    @BeforeEach
    public void setup() {
        groupId = 1L;
        firstMemberEntity = FakeMemberInitializer.of(1L);
        secondMemberEntity = FakeMemberInitializer.of(2L);
        firstRankEntity = FakeRankInitializer.of(1L, groupId, firstMemberEntity.getId());
        secondRankEntity = FakeRankInitializer.of(2L, groupId, secondMemberEntity.getId())
                                              .toBuilder()
                                              .duration(Duration.ofHours(2))
                                              .build();
    }

    @DisplayName("그룹내 전체 랭킹 조회 테스트")
    @Test
    public void testBrowseRanksInGroup() {
        //given
        List<RankEntity> ranks = new ArrayList<>();
        ranks.add(firstRankEntity);
        ranks.add(secondRankEntity);

        List<Long> memberIds = new ArrayList<>();
        memberIds.add(secondMemberEntity.getId());
        memberIds.add(firstMemberEntity.getId());

        List<MemberEntity> memberEntities = new ArrayList<>();
        memberEntities.add(secondMemberEntity);
        memberEntities.add(firstMemberEntity);

        when(rankRepository.findByGroupIdAndDeletedFalse(groupId)).thenReturn(ranks);
        when(memberRepository.findAllById(memberIds)).thenReturn(memberEntities);
        //when
        List<RankResponse> results = rankService.getRanksInGroup(new RankBrowseRequest(groupId));
        //then
        assertThat(results.size()).isEqualTo(2);
        assertThat(results.get(0)
                          .getMemberId()).isEqualTo(secondMemberEntity.getId());
    }
}