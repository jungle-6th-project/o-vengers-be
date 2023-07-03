package jungle.ovengers.service;

import jungle.ovengers.config.security.AuditorHolder;
import jungle.ovengers.data.FakeGroupInitializer;
import jungle.ovengers.data.FakeMemberGroupInitializer;
import jungle.ovengers.data.FakeMemberInitializer;
import jungle.ovengers.entity.GroupEntity;
import jungle.ovengers.entity.MemberEntity;
import jungle.ovengers.entity.MemberGroupEntity;
import jungle.ovengers.model.request.*;
import jungle.ovengers.model.response.GroupResponse;
import jungle.ovengers.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private RoomRepository roomRepository;
    @Mock
    private MemberRoomRepository memberRoomRepository;
    @Mock
    private TodoRepository todoRepository;
    @Mock
    private AuditorHolder auditorHolder;
    @InjectMocks
    private GroupService groupService;

    private GroupEntity groupEntity;
    private MemberEntity memberEntity;
    private MemberGroupEntity memberGroupEntity;

    @BeforeEach
    public void setup() {
        memberEntity = FakeMemberInitializer.of();
        groupEntity = FakeGroupInitializer.of();
        memberGroupEntity = FakeMemberGroupInitializer.of();
    }

    @DisplayName("사용자가 그룹 생성 요청을 했을 경우, 그룹이 잘 생성되는지 테스트")
    @Test
    public void testGenerateGroup() {
        //given
        Long memberId = memberEntity.getId();
        GroupAddRequest request = new GroupAddRequest(groupEntity.getGroupName(), groupEntity.isSecret(), groupEntity.getPassword(), groupEntity.getPath());

        when(auditorHolder.get()).thenReturn(memberId);
        when(memberRepository.findByIdAndDeletedFalse(memberId)).thenReturn(Optional.of(memberEntity));
        when(groupRepository.save(any(GroupEntity.class))).thenReturn(groupEntity);
        //when
        GroupResponse result = groupService.generateGroup(request);
        //then
        assertThat(result.getGroupId())
                .isEqualTo(groupEntity.getId());
        assertThat(result.getGroupName())
                .isEqualTo(groupEntity.getGroupName());
        assertThat(result.isSecret())
                .isEqualTo(groupEntity.isSecret());
    }

    @DisplayName("전체 그룹 목록이 잘 불러와지는지 테스트")
    @Test
    public void testGetAllGroups() {
        //given
        List<GroupEntity> mockGroupEntities = Collections.singletonList(groupEntity);

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
        Long memberId = memberEntity.getId();

        when(auditorHolder.get()).thenReturn(memberId);
        when(memberRepository.findByIdAndDeletedFalse(memberId)).thenReturn(Optional.of(memberEntity));
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
        Long memberId = memberEntity.getId();
        Long groupId = groupEntity.getId();

        when(auditorHolder.get()).thenReturn(memberId);
        when(memberRepository.findByIdAndDeletedFalse(memberId)).thenReturn(Optional.of(memberEntity));
        when(groupRepository.findByIdAndDeletedFalse(groupId)).thenReturn(Optional.of(groupEntity));
        when(memberGroupRepository.findByGroupIdAndMemberIdAndDeletedFalse(groupId, memberId)).thenReturn(Optional.of(memberGroupEntity));
        //when
        groupService.joinGroup(groupId, new GroupJoinRequest(null));
        //then
        verify(memberGroupRepository, never()).save(any(MemberGroupEntity.class));
    }

    @DisplayName("사용자가 비밀번호가 없고, 가입되어 있지 않은 그룹에 참가 요청을 보냈을 때, 그룹 가입 정보를 새롭게 생성하는지 테스트")
    @Test
    public void testJoinGroup() {
        //given
        Long memberId = memberEntity.getId();
        Long groupId = groupEntity.getId();
        Long otherMemberId = memberId + 1L;
        GroupEntity notJoinedGroupEntity = FakeGroupInitializer.of(groupId, otherMemberId);

        when(auditorHolder.get()).thenReturn(memberId);
        when(memberRepository.findByIdAndDeletedFalse(memberId)).thenReturn(Optional.of(memberEntity));
        when(groupRepository.findByIdAndDeletedFalse(groupId)).thenReturn(Optional.of(notJoinedGroupEntity));
        //when
        GroupResponse result = groupService.joinGroup(groupId, null);

        //then
        assertThat(result.getGroupId()).isEqualTo(notJoinedGroupEntity.getId());
        assertThat(result.getGroupName()).isEqualTo(notJoinedGroupEntity.getGroupName());
        assertThat(result.isSecret()).isEqualTo(notJoinedGroupEntity.isSecret());
    }

    @DisplayName("사용자가 비공개 그룹에 잘못된 비밀번호로 참가 요청했을때 예외가 발생하는지 테스트")
    @Test
    public void testJoinGroupWhenKnownPassword() {
        //given
        Long memberId = memberEntity.getId();
        Long groupId = groupEntity.getId();
        Long otherMemberId = memberId + 1L;
        GroupEntity notJoinedGroupEntity = FakeGroupInitializer.of(groupId, otherMemberId)
                                                               .toBuilder()
                                                               .isSecret(true)
                                                               .password("password")
                                                               .build();
        when(auditorHolder.get()).thenReturn(memberId);
        when(memberRepository.findByIdAndDeletedFalse(memberId)).thenReturn(Optional.of(memberEntity));
        when(groupRepository.findByIdAndDeletedFalse(groupId)).thenReturn(Optional.of(notJoinedGroupEntity));
        //when, then
        assertThatThrownBy(() -> groupService.joinGroup(groupId, new GroupJoinRequest("wrong password")))
                .isInstanceOf(IllegalArgumentException.class);
        verify(memberGroupRepository, never()).existsByGroupIdAndMemberIdAndDeletedFalse(groupId, memberId);
        verify(memberGroupRepository, never()).save(memberGroupEntity);
    }

    @DisplayName("그룹의 path와 일치하는 path로 참가 요청할 경우, 참가 정보가 잘 저장 되는지 테스트")
    @Test
    public void testJoinGroupWithValidPath() {
        //given
        Long memberId = memberEntity.getId();

        when(auditorHolder.get()).thenReturn(memberId);
        when(memberRepository.findByIdAndDeletedFalse(memberId)).thenReturn(Optional.of(memberEntity));
        when(groupRepository.findByPathAndDeletedFalse(groupEntity.getPath())).thenReturn(Optional.of(groupEntity));
        //when
        GroupResponse result = groupService.joinGroupWithPath(new GroupPathJoinRequest(groupEntity.getPath()));
        //then
        assertThat(result.getGroupName()).isEqualTo(groupEntity.getGroupName());
        assertThat(result.getGroupId()).isEqualTo(groupEntity.getId());
    }

    @DisplayName("그룹의 path와 일치하지 않는 path로 참가 요청할 경우, 예외가 발생하는지 테스트")
    @Test
    public void testJoinGroupWithInvalidPath() {
        //given
        Long memberId = memberEntity.getId();

        when(auditorHolder.get()).thenReturn(memberId);
        when(memberRepository.findByIdAndDeletedFalse(memberId)).thenReturn(Optional.of(memberEntity));
        when(groupRepository.findByPathAndDeletedFalse("invalidPath")).thenReturn(Optional.empty());
        //when, then
        assertThatThrownBy(() -> groupService.joinGroupWithPath(new GroupPathJoinRequest("invalidPath"))).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("그룹 멤버가 그룹에서 탈퇴할 경우 가입 정보가 잘 삭제되는지 테스트")
    @Test
    public void testWithdrawGroupWhoIsMember() {
        //given
        Long memberId = memberEntity.getId();
        Long groupId = groupEntity.getId();
        Long otherMemberId = memberId + 1L;
        GroupEntity notOwnerGroupEntity = FakeGroupInitializer.of(groupId, otherMemberId);

        when(auditorHolder.get()).thenReturn(memberId);
        when(memberRepository.findByIdAndDeletedFalse(memberId)).thenReturn(Optional.of(memberEntity));
        when(groupRepository.findByIdAndDeletedFalse(groupId)).thenReturn(Optional.of(notOwnerGroupEntity));
        //when
        groupService.withdrawGroup(new GroupWithdrawRequest(groupId));
        //then
        verify(rankRepository, times(1)).findByGroupIdAndMemberIdAndDeletedFalse(groupId, memberId);
        verify(memberGroupRepository, times(1)).findByGroupIdAndMemberIdAndDeletedFalse(groupId, memberId);
        verify(roomRepository, times(1)).findByGroupIdAndDeletedFalse(groupId);
        verify(todoRepository, times(1)).findByGroupIdAndMemberIdAndDeletedFalse(groupId, memberId);
    }

    @DisplayName("그룹의 마지막 사용자가 탈퇴할 경우, 탈퇴가 잘 되는지 테스트")
    @Test
    public void testWithdrawGroupWhoIsOwner() {
        //given
        Long memberId = memberEntity.getId();
        Long groupId = groupEntity.getId();
        GroupWithdrawRequest request = new GroupWithdrawRequest(groupId);

        when(auditorHolder.get()).thenReturn(memberId);
        when(memberRepository.findByIdAndDeletedFalse(memberId)).thenReturn(Optional.of(memberEntity));
        when(groupRepository.findByIdAndDeletedFalse(groupId)).thenReturn(Optional.of(groupEntity));
        //when
        groupService.withdrawGroup(request);
        //then
        verify(rankRepository, times(1)).findByGroupIdAndMemberIdAndDeletedFalse(groupId, memberId);
        verify(memberGroupRepository, times(1)).findByGroupIdAndMemberIdAndDeletedFalse(groupId, memberId);
        verify(roomRepository, times(1)).findByGroupIdAndDeletedFalse(groupId);
        verify(todoRepository, times(1)).findByGroupIdAndMemberIdAndDeletedFalse(groupId, memberId);
    }

    @DisplayName("사용자가 속한 그룹의 색깔을 변경했을때, 잘 변경 되는지 테스트")
    @Test
    public void testChangeMemberGroupColor() {
        //given
        Long memberId = memberEntity.getId();
        Long groupId = groupEntity.getId();

        when(auditorHolder.get()).thenReturn(memberId);
        when(memberRepository.findByIdAndDeletedFalse(memberId)).thenReturn(Optional.of(memberEntity));
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
        Long memberId = memberEntity.getId();
        Long groupId = groupEntity.getId();

        when(auditorHolder.get()).thenReturn(memberId);
        when(memberRepository.findByIdAndDeletedFalse(memberId)).thenReturn(Optional.of(memberEntity));
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
        Long memberId = memberEntity.getId();
        Long groupId = groupEntity.getId();
        Long otherMemberId = 2L;
        GroupEntity notOwnerGroupEntity = FakeGroupInitializer.of(groupId, otherMemberId);

        when(auditorHolder.get()).thenReturn(memberId);
        when(memberRepository.findByIdAndDeletedFalse(memberId)).thenReturn(Optional.of(memberEntity));
        when(groupRepository.findByIdAndDeletedFalse(groupId)).thenReturn(Optional.of(notOwnerGroupEntity));
        //when
        assertThatThrownBy(() -> groupService.changeGroupInfo(new GroupEditRequest(groupId, "changedGroupName", true, "changedPassword")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}