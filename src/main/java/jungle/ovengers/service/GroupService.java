package jungle.ovengers.service;

import jungle.ovengers.config.security.AuditorHolder;
import jungle.ovengers.entity.GroupEntity;
import jungle.ovengers.entity.MemberGroupEntity;
import jungle.ovengers.exception.GroupNotFoundException;
import jungle.ovengers.exception.MemberNotFoundException;
import jungle.ovengers.model.request.GroupAddRequest;
import jungle.ovengers.model.request.GroupJoinRequest;
import jungle.ovengers.model.response.GroupResponse;
import jungle.ovengers.repository.GroupRepository;
import jungle.ovengers.repository.MemberGroupRepository;
import jungle.ovengers.repository.MemberRepository;
import jungle.ovengers.support.converter.GroupConverter;
import jungle.ovengers.support.converter.MemberGroupConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final MemberGroupRepository memberGroupRepository;
    private final MemberRepository memberRepository;
    private final AuditorHolder auditorHolder;

    public GroupResponse generateGroup(GroupAddRequest request) {
        Long memberId = auditorHolder.get();
        memberRepository.findById(memberId)
                        .orElseThrow(() -> new MemberNotFoundException(memberId));
        GroupEntity groupEntity = groupRepository.save(GroupConverter.to(request, memberId));
        memberGroupRepository.save(MemberGroupConverter.to(memberId, groupEntity.getId()));

        return new GroupResponse(groupEntity.getId(), groupEntity.getGroupName(), groupEntity.isSecret());
    }

    public List<GroupResponse> getAllGroups() {
        return groupRepository.findAll()
                              .stream()
                              .filter(groupEntity -> !groupEntity.isDeleted())
                              .map(groupEntity -> new GroupResponse(groupEntity.getId(), groupEntity.getGroupName(), groupEntity.isSecret()))
                              .collect(Collectors.toList());
    }

    public List<GroupResponse> getMemberGroups() {
        Long memberId = auditorHolder.get();

        memberRepository.findById(memberId)
                        .orElseThrow(() -> new MemberNotFoundException(memberId));

        List<MemberGroupEntity> memberGroups = memberGroupRepository.findByMemberId(memberId);

        return memberGroups.stream()
                           .map(MemberGroupEntity::getGroupId)
                           .map(groupRepository::findById)
                           .flatMap(Optional::stream)
                           .filter(groupEntity -> !groupEntity.isDeleted())
                           .map(groupEntity -> new GroupResponse(groupEntity.getId(),
                                                                 groupEntity.getGroupName(),
                                                                 groupEntity.isSecret()))
                           .collect(Collectors.toList());
    }

    public GroupResponse joinGroup(Long groupId, GroupJoinRequest request) {
        Long memberId = auditorHolder.get();

        memberRepository.findById(memberId)
                        .orElseThrow(() -> new MemberNotFoundException(memberId));

        GroupEntity groupEntity = groupRepository.findByIdAndDeletedFalse(groupId)
                                                 .orElseThrow(() -> new GroupNotFoundException(groupId));

        if (groupEntity.isSecret() && !groupEntity.isEqualPassword(request.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        if (memberGroupRepository.existsByGroupIdAndMemberId(groupId, memberId)) {
            return null;
        }
        memberGroupRepository.save(MemberGroupConverter.to(memberId, groupId));
        return new GroupResponse(groupEntity.getId(), groupEntity.getGroupName(), groupEntity.isSecret());
    }

    public void deleteGroup(Long groupId) {
        Long memberId = auditorHolder.get();

        memberRepository.findById(memberId)
                        .orElseThrow(() -> new MemberNotFoundException(memberId));

        GroupEntity groupEntity = groupRepository.findById(groupId)
                                                 .orElseThrow(() -> new GroupNotFoundException(groupId));

        if (!groupEntity.isOwner(memberId)) {
            throw new IllegalArgumentException("그룹장만 그룹을 삭제할 수 있습니다.");
        }
        groupEntity.delete();
        memberGroupRepository.findByGroupId(groupId)
                             .forEach(MemberGroupEntity::delete);
    }
}
