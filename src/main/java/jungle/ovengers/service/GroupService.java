package jungle.ovengers.service;

import jungle.ovengers.config.security.AuditorHolder;
import jungle.ovengers.entity.*;
import jungle.ovengers.exception.GroupNotFoundException;
import jungle.ovengers.exception.MemberGroupNotFoundException;
import jungle.ovengers.exception.MemberNotFoundException;
import jungle.ovengers.model.request.*;
import jungle.ovengers.model.response.GroupResponse;
import jungle.ovengers.repository.*;
import jungle.ovengers.support.converter.GroupConverter;
import jungle.ovengers.support.converter.MemberGroupConverter;
import jungle.ovengers.support.converter.RankConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class GroupService {

    private final String NOT_GROUP_OWNER = "그룹장이 아닙니다.";
    private final String INVALID_GROUP_PATH = "유효하지 않은 초대 코드입니다. : ";
    private final String ALREADY_JOINED_GROUP = "이미 가입한 그룹입니다.";
    private final String INVALID_GROUP_PASSWORD = "비밀번호가 일치하지 않습니다.";
    private final GroupRepository groupRepository;
    private final MemberGroupRepository memberGroupRepository;
    private final MemberRepository memberRepository;
    private final RankRepository rankRepository;
    private final RoomRepository roomRepository;
    private final MemberRoomRepository memberRoomRepository;
    private final TodoRepository todoRepository;
    private final AuditorHolder auditorHolder;

    public GroupResponse generateGroup(GroupAddRequest request) {
        Long memberId = auditorHolder.get();
        MemberEntity memberEntity = memberRepository.findById(memberId)
                                                    .orElseThrow(() -> new MemberNotFoundException(memberId));
        GroupEntity groupEntity = groupRepository.save(GroupConverter.to(request, memberId));
        memberGroupRepository.save(MemberGroupConverter.to(memberEntity, groupEntity));
        rankRepository.save(RankConverter.to(memberEntity, groupEntity));

        return GroupConverter.from(groupEntity);
    }

    public List<GroupResponse> getAllGroups() {
        Long memberId = auditorHolder.get();
        Set<Long> excludedGroupIds = memberGroupRepository.findByMemberIdAndDeletedFalse(memberId)
                                                          .stream().map(MemberGroupEntity::getGroupId)
                                                          .collect(Collectors.toSet());

        return groupRepository.findAll()
                              .stream()
                              .filter(groupEntity -> !groupEntity.isDeleted() && !excludedGroupIds.contains(groupEntity.getId()))
                              .map(GroupConverter::from)
                              .collect(Collectors.toList());
    }

    public List<GroupResponse> getMemberGroups() {
        Long memberId = auditorHolder.get();

        memberRepository.findById(memberId)
                        .orElseThrow(() -> new MemberNotFoundException(memberId));

        List<MemberGroupEntity> memberGroups = memberGroupRepository.findByMemberIdAndDeletedFalse(memberId);

        return memberGroups.stream()
                           .map(memberGroupEntity -> {
                               GroupEntity groupEntity = groupRepository.findByIdAndDeletedFalse(memberGroupEntity.getGroupId())
                                                                        .orElseThrow(() -> new GroupNotFoundException(memberGroupEntity.getGroupId()));
                               return GroupConverter.from(groupEntity, memberGroupEntity);
                           })
                           .collect(Collectors.toList());
    }

    public GroupResponse joinGroup(Long groupId, GroupJoinRequest request) {
        Long memberId = auditorHolder.get();

        MemberEntity memberEntity = memberRepository.findById(memberId)
                                                    .orElseThrow(() -> new MemberNotFoundException(memberId));

        GroupEntity groupEntity = groupRepository.findByIdAndDeletedFalse(groupId)
                                                 .orElseThrow(() -> new GroupNotFoundException(groupId));

        if (groupEntity.isSecret() && !groupEntity.isEqualPassword(request.getPassword())) {
            throw new IllegalArgumentException(INVALID_GROUP_PASSWORD);
        }

        MemberGroupEntity memberGroupEntity = memberGroupRepository.findByGroupIdAndMemberIdAndDeletedFalse(groupEntity.getId(), memberId)
                                                                   .orElse(null);

        if (memberGroupEntity == null) {
            memberGroupRepository.save(MemberGroupConverter.to(memberEntity, groupEntity));
            rankRepository.save(RankConverter.to(memberEntity, groupEntity));
        }

        return GroupConverter.from(groupEntity);
    }

    public GroupResponse joinGroupWithPath(GroupPathJoinRequest request) {
        Long memberId = auditorHolder.get();

        MemberEntity memberEntity = memberRepository.findById(memberId)
                                                    .orElseThrow(() -> new MemberNotFoundException(memberId));

        GroupEntity groupEntity = groupRepository.findByPathAndDeletedFalse(request.getPath())
                                                 .orElseThrow(() -> new IllegalArgumentException(INVALID_GROUP_PATH + request.getPath()));
        MemberGroupEntity memberGroupEntity = memberGroupRepository.findByGroupIdAndMemberIdAndDeletedFalse(groupEntity.getId(), memberId)
                                                                   .orElse(null);
        if (memberGroupEntity == null) {
            memberGroupRepository.save(MemberGroupConverter.to(memberEntity, groupEntity));
            rankRepository.save(RankConverter.to(memberEntity, groupEntity));
            return GroupConverter.from(groupEntity);
        }
        throw new IllegalArgumentException(ALREADY_JOINED_GROUP);
    }

    public void withdrawGroup(GroupWithdrawRequest request) {
        Long memberId = auditorHolder.get();

        MemberEntity memberEntity = memberRepository.findByIdAndDeletedFalse(memberId)
                                                    .orElseThrow(() -> new MemberNotFoundException(memberId));

        GroupEntity groupEntity = groupRepository.findByIdAndDeletedFalse(request.getGroupId())
                                                 .orElseThrow(() -> new GroupNotFoundException(request.getGroupId()));

        this.deleteSingleAssociation(groupEntity, memberEntity);
    }

    /**
     * 그룹 구성원 개인이 탈퇴할 경우 -> 트랜잭션 분리해서 개선하기
     */
    private void deleteSingleAssociation(GroupEntity groupEntity, MemberEntity memberEntity) {
        // 랭킹 연관 데이터 삭제
        rankRepository.findByGroupIdAndMemberIdAndDeletedFalse(groupEntity.getId(), memberEntity.getId())
                      .ifPresent(RankEntity::delete);
        // 그룹에 속한 사용자의 데이터 삭제
        memberGroupRepository.findByGroupIdAndMemberIdAndDeletedFalse(groupEntity.getId(), memberEntity.getId())
                             .ifPresent(MemberGroupEntity::delete);
        memberGroupRepository.flush();

        // 해당 그룹에 속한 사용자가 더 이상 없다면 그룹 삭제
        if (!memberGroupRepository.existsByGroupIdAndDeletedFalse(groupEntity.getId())) {
            groupEntity.delete();
        }

        // 사용자가 예약한 방 데이터 삭제
        List<RoomEntity> roomEntities = roomRepository.findByGroupIdAndDeletedFalse(groupEntity.getId());
        roomEntities.forEach(roomEntity -> {
            memberRoomRepository.findByMemberIdAndRoomIdAndDeletedFalse(memberEntity.getId(), roomEntity.getId())
                                .ifPresent(MemberRoomEntity::delete);
            roomEntity.removeProfile(memberEntity.getProfile());
        });
        memberRoomRepository.flush();

        // 사용자가 예약했던 방에 더 이상 예약자가 없다면, 방 자체를 삭제
        roomEntities.forEach(roomEntity -> {
            if (!memberRoomRepository.existsByRoomIdAndDeletedFalse(roomEntity.getId())) {
                roomEntity.delete();
            }
        });

        // 사용자가 해당 그룹에서 작성했던 todo 삭제
        todoRepository.findByGroupIdAndMemberIdAndDeletedFalse(groupEntity.getId(), memberEntity.getId())
                      .forEach(TodoEntity::delete);
    }

    public void deleteAllAssociation(MemberEntity memberEntity) {
        List<MemberGroupEntity> memberGroupEntities = memberGroupRepository.findByMemberIdAndDeletedFalse(memberEntity.getId());

        for (MemberGroupEntity memberGroupEntity : memberGroupEntities) {
            GroupEntity groupEntity = groupRepository.findByIdAndDeletedFalse(memberGroupEntity.getGroupId())
                                                     .orElseThrow(() -> new GroupNotFoundException(memberGroupEntity.getGroupId()));
            this.deleteSingleAssociation(groupEntity, memberEntity);
        }
    }

    public GroupResponse changeGroupInfo(GroupEditRequest request) {
        Long memberId = auditorHolder.get();

        memberRepository.findById(memberId)
                        .orElseThrow(() -> new MemberNotFoundException(memberId));
        GroupEntity groupEntity = groupRepository.findByIdAndDeletedFalse(request.getGroupId())
                                                 .orElseThrow(() -> new GroupNotFoundException(request.getGroupId()));
        if (!groupEntity.isOwner(memberId)) {
            throw new IllegalArgumentException(NOT_GROUP_OWNER);
        }
        groupEntity.changeGroupInfo(request);
        return GroupConverter.from(groupEntity);
    }

    public GroupResponse changeGroupColor(GroupColorEditRequest request) {
        Long memberId = auditorHolder.get();

        memberRepository.findById(memberId)
                        .orElseThrow(() -> new MemberNotFoundException(memberId));
        GroupEntity groupEntity = groupRepository.findByIdAndDeletedFalse(request.getGroupId())
                                                 .orElseThrow(() -> new GroupNotFoundException(request.getGroupId()));
        MemberGroupEntity memberGroupEntity = memberGroupRepository.findByGroupIdAndMemberIdAndDeletedFalse(request.getGroupId(), memberId)
                                                                   .orElseThrow(() -> new MemberGroupNotFoundException(memberId, request.getGroupId()));
        memberGroupEntity.changeColor(request.getColor());

        return GroupConverter.from(groupEntity, memberGroupEntity);
    }

    public GroupResponse getGroupByPath(GroupPathJoinRequest request) {
        Long memberId = auditorHolder.get();
        memberRepository.findById(memberId)
                        .orElseThrow(() -> new MemberNotFoundException(memberId));

        GroupEntity groupEntity = groupRepository.findByPathAndDeletedFalse(request.getPath())
                                                 .orElseThrow(() -> new IllegalArgumentException(INVALID_GROUP_PATH + request.getPath()));

        if (memberGroupRepository.existsByGroupIdAndMemberIdAndDeletedFalse(groupEntity.getId(), memberId)) {
            throw new IllegalArgumentException(ALREADY_JOINED_GROUP);
        }

        return GroupConverter.from(groupEntity);
    }
}
