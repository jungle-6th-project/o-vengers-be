package jungle.ovengers.service;

import jungle.ovengers.config.security.AuditorHolder;
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

    @Mock
    private AuditorHolder auditorHolder;

    @InjectMocks
    private RankService rankService;

    private Long firstRankId;
    private Long secondRankId;
    private Long firstMemberId;
    private Long secondMemberId;
    private Long groupId;
    private RankEntity firstRankEntity;
    private RankEntity secondRankEntity;
    private MemberEntity firstMemberEntity;
    private MemberEntity secondMemberEntity;

    @BeforeEach
    public void setup() {
        firstRankId = 1L;
        firstMemberId = 1L;
        secondRankId = 2L;
        secondMemberId = 2L;
        groupId = 1L;
        firstRankEntity = RankEntity.builder()
                                    .rankId(firstRankId)
                                    .deleted(false)
                                    .duration(Duration.ofHours(1))
                                    .groupId(groupId)
                                    .memberId(firstMemberId)
                                    .build();
        secondRankEntity = RankEntity.builder()
                                     .rankId(secondRankId)
                                     .deleted(false)
                                     .duration(Duration.ofHours(4))
                                     .groupId(groupId)
                                     .memberId(secondMemberId)
                                     .build();
        firstMemberEntity = MemberEntity.builder()
                                        .id(firstMemberId)
                                        .email("email1")
                                        .name("name1")
                                        .deleted(false)
                                        .profile("profile1")
                                        .build();
        secondMemberEntity = MemberEntity.builder()
                                         .id(secondMemberId)
                                         .email("email1")
                                         .name("name1")
                                         .deleted(false)
                                         .profile("profile1")
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
        memberIds.add(secondMemberId);
        memberIds.add(firstMemberId);

        List<MemberEntity> memberEntities = new ArrayList<>();
        memberEntities.add(firstMemberEntity);
        memberEntities.add(secondMemberEntity);

        when(rankRepository.findByGroupIdAndDeletedFalse(groupId)).thenReturn(ranks);
        when(memberRepository.findAllById(memberIds)).thenReturn(memberEntities);
        //when
        List<RankResponse> results = rankService.getRanksInGroup(new RankBrowseRequest(groupId));
        //then
        assertThat(results.size()).isEqualTo(2);
        assertThat(results.get(0)
                          .getMemberId()).isEqualTo(secondMemberId);
    }
}