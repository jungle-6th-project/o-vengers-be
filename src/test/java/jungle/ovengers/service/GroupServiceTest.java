package jungle.ovengers.service;

import jungle.ovengers.config.security.AuditorHolder;
import jungle.ovengers.entity.GroupEntity;
import jungle.ovengers.entity.MemberEntity;
import jungle.ovengers.model.request.GroupAddRequest;
import jungle.ovengers.model.response.GroupResponse;
import jungle.ovengers.repository.GroupRepository;
import jungle.ovengers.repository.MemberGroupRepository;
import jungle.ovengers.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private MemberGroupRepository memberGroupRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private AuditorHolder auditorHolder;
    @InjectMocks
    private GroupService groupService;

    @DisplayName("사용자가 그룹 생성 요청을 했을 경우, 그룹이 잘 생성되는지 테스트")
    @Test
    public void testGenerateGroup() {
        //given
        GroupAddRequest request = new GroupAddRequest("groupName", false, "123", "path");
        Long memberId = 1L;
        GroupEntity savedGroupEntity = GroupEntity.builder()
                                                  .id(1L)
                                                  .ownerId(memberId)
                                                  .path(request.getPath())
                                                  .groupName(request.getGroupName())
                                                  .isSecret(request.isSecret())
                                                  .createdAt(String.valueOf(LocalDateTime.now()))
                                                  .build();

//        verify(auditorHolder).get();
        when(auditorHolder.get()).thenReturn(memberId);
        MemberEntity memberEntity = MemberEntity.builder()
                                                .id(memberId)
                                                .email("email")
                                                .profile("profile")
                                                .name("name")
                                                .build();
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberEntity));
        when(groupRepository.save(any(GroupEntity.class))).thenReturn(savedGroupEntity);

        //when
        GroupResponse result = groupService.generateGroup(request);

        //then
        assertThat(result.getGroupId())
                .isEqualTo(savedGroupEntity.getId());
        assertThat(result.getGroupName())
                .isEqualTo(savedGroupEntity.getGroupName());
        assertThat(result.isSecret())
                .isEqualTo(savedGroupEntity.isSecret());
    }
}