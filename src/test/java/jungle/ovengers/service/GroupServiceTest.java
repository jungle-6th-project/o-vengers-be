package jungle.ovengers.service;

import jungle.ovengers.config.security.AuditorHolder;
import jungle.ovengers.entity.GroupEntity;
import jungle.ovengers.entity.MemberEntity;
import jungle.ovengers.entity.MemberGroupEntity;
import jungle.ovengers.model.request.GroupAddRequest;
import jungle.ovengers.model.response.GroupResponse;
import jungle.ovengers.repository.GroupRepository;
import jungle.ovengers.repository.MemberGroupRepository;
import jungle.ovengers.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

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

    private Long memberId;
    private GroupEntity groupEntity;
    private MemberEntity memberEntity;
    private MemberGroupEntity memberGroupEntity;
    @BeforeEach
    public void setup() {
        memberId = 1L;
        groupEntity = GroupEntity.builder()
                                 .id(1L)
                                 .ownerId(memberId)
                                 .path("path")
                                 .groupName("groupName")
                                 .isSecret(false)
                                 .createdAt(String.valueOf(LocalDateTime.now()))
                                 .build();
        memberEntity = MemberEntity.builder()
                                   .id(memberId)
                                   .email("email")
                                   .profile("profile")
                                   .name("name")
                                   .build();
        memberGroupEntity = MemberGroupEntity.builder()
                                             .memberId(memberId)
                                             .groupId(groupEntity.getId())
                                             .id(memberId + 1)
                                             .build();
    }

    @DisplayName("사용자가 그룹 생성 요청을 했을 경우, 그룹이 잘 생성되는지 테스트")
    @Test
    public void testGenerateGroup() {
        //given
        GroupAddRequest request = new GroupAddRequest("groupName", false, "123", "path");
        GroupEntity savedGroupEntity = GroupEntity.builder()
                                                  .id(1L)
                                                  .ownerId(memberId)
                                                  .path(request.getPath())
                                                  .groupName(request.getGroupName())
                                                  .createdAt(String.valueOf(LocalDateTime.now()))
                                                  .build();

        when(auditorHolder.get()).thenReturn(memberId);
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

    @DisplayName("전체 그룹 목록이 잘 불러와지는지 테스트")
    @Test
    public void testGetAllGroups() {
        //given
        List<GroupEntity> mockGroupEntities = Arrays.asList(groupEntity);

        when(groupRepository.findAll()).thenReturn(mockGroupEntities);

        //when
        List<GroupResponse> result = groupService.getAllGroups();

        //then
        assertThat(result.get(0)
                         .getGroupId()).isEqualTo(groupEntity.getId());
        assertThat(result.get(0)
                         .getGroupName()).isEqualTo(groupEntity.getGroupName());
        assertThat(result.get(0)
                         .isSecret()).isEqualTo(groupEntity.isSecret());
    }

    @DisplayName("사용자가 속한 그룹 목록이 잘 불러와지는지 테스트")
    @Test
    public void testGetMemberGroups() {
        //given
        when(auditorHolder.get()).thenReturn(memberId);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberEntity));
        when(memberGroupRepository.findByMemberId(memberId)).thenReturn(Collections.singletonList(memberGroupEntity));
        when(groupRepository.findById(groupEntity.getId())).thenReturn(Optional.of(groupEntity));

        //when
        List<GroupResponse> results = groupService.getMemberGroups();

        //then
        assertThat(results.get(0)
                          .getGroupName()).isEqualTo(groupEntity.getGroupName());
        assertThat(results.get(0)
                          .getGroupId()).isEqualTo(groupEntity.getId());
        assertThat(results.get(0)
                          .isSecret()).isEqualTo(groupEntity.isSecret());
    }

    @DisplayName("사용자가 이미 가입되어 있는 그룹에 참가 요청 보냈을 때, 그룹 가입 정보를 생성하는지 테스트")
    @Test
    public void testJoinGroupWhenAlreadyJoined() {
        //given
        Long groupId = 1L;
        when(auditorHolder.get()).thenReturn(memberId);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberEntity));
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(groupEntity));
        when(memberGroupRepository.findByGroupId(groupId)).thenReturn(Collections.singletonList(memberGroupEntity));

        //when
        GroupResponse result = groupService.joinGroup(groupId);

        //then
        assertThat(result).isEqualTo(null);
    }

    @DisplayName("사용자가 가입되어 있지 않은 그룹에 참가 요청을 보냈을 때, 그룹 가입 정보를 새롭게 생성하지 않는지 테스트")
    @Test
    public void testJoinGroup() {
        //given
        Long groupId = 1L;
        Long otherMemberId = 2L;
        Long memberGroupId = 1L;

        when(auditorHolder.get()).thenReturn(memberId);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberEntity));

        GroupEntity groupEntity = GroupEntity.builder()
                                 .id(groupId)
                                 .ownerId(otherMemberId)
                                 .path("path")
                                 .groupName("groupName")
                                 .isSecret(false)
                                 .createdAt(String.valueOf(LocalDateTime.now()))
                                 .build();
        MemberGroupEntity memberGroupEntity = MemberGroupEntity.builder()
                                                               .memberId(otherMemberId)
                                                               .groupId(groupEntity.getId())
                                                               .id(memberGroupId)
                                                               .build();
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(groupEntity));
        when(memberGroupRepository.findByGroupId(groupId)).thenReturn(Collections.singletonList(memberGroupEntity));

        //when
        GroupResponse result = groupService.joinGroup(groupId);

        //then
        assertThat(result.getGroupId()).isEqualTo(groupEntity.getId());
        assertThat(result.getGroupName()).isEqualTo(groupEntity.getGroupName());
        assertThat(result.isSecret()).isEqualTo(groupEntity.isSecret());
    }
}