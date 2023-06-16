package jungle.ovengers.service;

import jungle.ovengers.config.security.AuditorHolder;
import jungle.ovengers.entity.GroupEntity;
import jungle.ovengers.entity.MemberEntity;
import jungle.ovengers.entity.MemberGroupEntity;
import jungle.ovengers.model.request.*;
import jungle.ovengers.model.response.GroupResponse;
import jungle.ovengers.repository.GroupRepository;
import jungle.ovengers.repository.MemberGroupRepository;
import jungle.ovengers.repository.MemberRepository;
import jungle.ovengers.repository.RankRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private MemberGroupRepository memberGroupRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private RankRepository rankRepository;
    @Mock
    private AuditorHolder auditorHolder;
    @InjectMocks
    private GroupService groupService;

    private Long memberId;
    private Long groupId;
    private GroupEntity groupEntity;
    private MemberEntity memberEntity;
    private MemberGroupEntity memberGroupEntity;

    @BeforeEach
    public void setup() {
        memberId = 1L;
        groupId = 1L;
        groupEntity = GroupEntity.builder()
                                 .id(groupId)
                                 .ownerId(memberId)
                                 .path("path")
                                 .groupName("groupName")
                                 .isSecret(false)
                                 .createdAt(LocalDateTime.now())
                                 .deleted(false)
                                 .build();
        memberEntity = MemberEntity.builder()
                                   .id(memberId)
                                   .email("email")
                                   .profile("profile")
                                   .name("name")
                                   .deleted(false)
                                   .build();
        memberGroupEntity = MemberGroupEntity.builder()
                                             .memberId(memberId)
                                             .groupId(groupEntity.getId())
                                             .id(memberId + 1)
                                             .deleted(false)
                                             .color("color")
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
                                                  .createdAt(LocalDateTime.now())
                                                  .deleted(false)
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
        when(memberGroupRepository.findByMemberIdAndDeletedFalse(memberId)).thenReturn(Collections.singletonList(memberGroupEntity));
        when(groupRepository.findByIdAndDeletedFalse(groupEntity.getId())).thenReturn(Optional.of(groupEntity));

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

    @DisplayName("사용자가 비밀번호가 없고, 이미 가입되어 있는 그룹에 참가 요청 보냈을 때, 그룹 가입 정보를 생성하지 않는지 테스트")
    @Test
    public void testJoinGroupWhenAlreadyJoined() {
        //given
        Long groupId = 1L;
        when(auditorHolder.get()).thenReturn(memberId);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberEntity));
        when(groupRepository.findByIdAndDeletedFalse(groupId)).thenReturn(Optional.of(groupEntity));
        when(memberGroupRepository.existsByGroupIdAndMemberIdAndDeletedFalse(groupId, memberId)).thenReturn(true);
        //when
        GroupResponse result = groupService.joinGroup(groupId, null);
        //then
        assertThat(result).isEqualTo(null);
    }

    @DisplayName("사용자가 비밀번호가 없고, 가입되어 있지 않은 그룹에 참가 요청을 보냈을 때, 그룹 가입 정보를 새롭게 생성하는지 테스트")
    @Test
    public void testJoinGroup() {
        //given
        Long groupId = 1L;
        Long otherMemberId = 2L;

        when(auditorHolder.get()).thenReturn(memberId);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberEntity));

        GroupEntity groupEntity = GroupEntity.builder()
                                             .id(groupId)
                                             .ownerId(otherMemberId)
                                             .path("path")
                                             .groupName("groupName")
                                             .isSecret(false)
                                             .createdAt(LocalDateTime.now())
                                             .deleted(false)
                                             .build();

        when(groupRepository.findByIdAndDeletedFalse(groupId)).thenReturn(Optional.of(groupEntity));
        when(memberGroupRepository.existsByGroupIdAndMemberIdAndDeletedFalse(groupId, memberId)).thenReturn(false);

        //when
        GroupResponse result = groupService.joinGroup(groupId, null);

        //then
        assertThat(result.getGroupId()).isEqualTo(groupEntity.getId());
        assertThat(result.getGroupName()).isEqualTo(groupEntity.getGroupName());
        assertThat(result.isSecret()).isEqualTo(groupEntity.isSecret());
    }

    @DisplayName("사용자가 비공개 그룹에 일치하는 비밀번호와 함께 참가 요청할 경우, 잘 참가 되는지 테스트")
    @Test
    public void testJoinGroupWhenKnownPassword() {
        //given
        Long groupId = 1L;
        Long otherMemberId = 2L;

        when(auditorHolder.get()).thenReturn(memberId);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberEntity));

        GroupEntity groupEntity = GroupEntity.builder()
                                             .id(groupId)
                                             .ownerId(otherMemberId)
                                             .path("path")
                                             .groupName("groupName")
                                             .isSecret(true)
                                             .password("password")
                                             .createdAt(LocalDateTime.now())
                                             .deleted(false)
                                             .build();

        when(groupRepository.findByIdAndDeletedFalse(groupId)).thenReturn(Optional.of(groupEntity));

        //when, then
        assertThatThrownBy(() -> groupService.joinGroup(groupId, new GroupJoinRequest("wrong password")))
                .isInstanceOf(IllegalArgumentException.class);
        verify(memberGroupRepository, never()).existsByGroupIdAndMemberIdAndDeletedFalse(groupId, memberId);
        verify(memberGroupRepository, never()).save(memberGroupEntity);
    }

    @DisplayName("그룹장이 삭제를 요청할 경우 그룹이 잘 삭제 되는지 테스트")
    @Test
    public void testDeleteGroup() {

        //given
        when(auditorHolder.get()).thenReturn(memberId);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberEntity));
        when(groupRepository.findById(groupEntity.getId())).thenReturn(Optional.of(groupEntity));

        //when
        groupService.deleteGroup(groupId);

        //then
        verify(memberGroupRepository).findByGroupId(groupId);
        verify(groupRepository).findById(groupId);
        verify(memberGroupRepository).findByGroupId(groupId);
    }

    @DisplayName("그룹장이 아닌 사용자가 그룹 삭제 요청을 할 경우, 에외가 발생되는지 테스트")
    @Test
    public void testDeleteGroupWhenNotGroupOwner() {
        //given
        Long otherMember = 2L;
        when(auditorHolder.get()).thenReturn(memberId);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberEntity));
        GroupEntity groupEntity = GroupEntity.builder()
                                             .id(groupId)
                                             .ownerId(otherMember)
                                             .path("path")
                                             .groupName("groupName")
                                             .isSecret(false)
                                             .createdAt(LocalDateTime.now())
                                             .deleted(false)
                                             .build();
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(groupEntity));
        //when, then
        assertThatThrownBy(() -> groupService.deleteGroup(groupId))
                .isInstanceOf(IllegalArgumentException.class);

        verify(memberGroupRepository, never()).findByGroupId(groupId);
    }

    @DisplayName("그룹에서 사용자가 잘 탈퇴되는지 테스트")
    @Test
    public void testWithdrawGroup() {
        //given
        GroupWithdrawRequest request = new GroupWithdrawRequest(groupId);
        when(auditorHolder.get()).thenReturn(memberId);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberEntity));
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(groupEntity));
        when(memberGroupRepository.findByGroupIdAndMemberIdAndDeletedFalse(groupId, memberId)).thenReturn(Optional.of(memberGroupEntity));
        //when
        groupService.withdrawGroup(request);
        //then
        verify(memberGroupRepository, times(1)).findByGroupIdAndMemberIdAndDeletedFalse(groupId, memberId);
    }

    @DisplayName("사용자가 속한 그룹의 색깔을 변경했을때, 잘 변경 되는지 테스트")
    @Test
    public void testChangeMemberGroupColor() {
        //given
        when(auditorHolder.get()).thenReturn(memberId);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberEntity));
        when(groupRepository.findByIdAndDeletedFalse(groupId)).thenReturn(Optional.of(groupEntity));
        when(memberGroupRepository.findByGroupIdAndMemberIdAndDeletedFalse(groupId, memberId)).thenReturn(Optional.of(memberGroupEntity));
        //when
        GroupResponse result = groupService.changeGroupColor(new GroupColorEditRequest(groupId, "changedColor"));
        //then
        assertThat(result.getColor()).isEqualTo("changedColor");
    }

    @DisplayName("그룹장이 그룹 색상을 변경했을때 잘 변경되는지 테스트")
    @Test
    public void testChangeGroupInfoByGroupOwner() {
        //given
        when(auditorHolder.get()).thenReturn(memberId);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberEntity));
        when(groupRepository.findByIdAndDeletedFalse(groupId)).thenReturn(Optional.of(groupEntity));
        //when
        GroupResponse result = groupService.changeGroupInfo(new GroupEditRequest(groupId, "changedGroupName", true, "changedPassword"));
        //then
        assertThat(result.getColor()).isEqualTo(null);
        assertThat(result.getGroupName()).isEqualTo("changedGroupName");
        assertThat(result.isSecret()).isTrue();
    }

    @DisplayName("그룹장이 아닌 사용자가 그룹 색상을 변경하려 할 때, 예외가 발생되는지 테스트")
    @Test
    public void testRejectChangeGroupByNotGroupOwner() {
        //given
        when(auditorHolder.get()).thenReturn(memberId);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberEntity));

        Long otherMemberId = 2L;
        GroupEntity groupEntity = GroupEntity.builder()
                                             .id(groupId)
                                             .groupName("groupName")
                                             .isSecret(false)
                                             .ownerId(otherMemberId)
                                             .deleted(false)
                                             .path("path")
                                             .createdAt(LocalDateTime.now())
                                             .build();
        when(groupRepository.findByIdAndDeletedFalse(groupId)).thenReturn(Optional.of(groupEntity));
        //when
        assertThatThrownBy(() -> groupService.changeGroupInfo(new GroupEditRequest(groupId, "changedGroupName", true, "changedPassword")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}