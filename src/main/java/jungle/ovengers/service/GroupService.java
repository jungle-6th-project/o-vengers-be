package jungle.ovengers.service;

import jungle.ovengers.config.security.AuditorHolder;
import jungle.ovengers.entity.GroupEntity;
import jungle.ovengers.entity.MemberGroupEntity;
import jungle.ovengers.exception.GroupNotFoundException;
import jungle.ovengers.exception.MemberNotFoundException;
import jungle.ovengers.model.request.GroupAddRequest;
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
                           .map(groupEntity -> new GroupResponse(groupEntity.getId(),
                                                                 groupEntity.getGroupName(),
                                                                 groupEntity.isSecret()))
                           .collect(Collectors.toList());
    }

    public GroupResponse joinGroup(Long groupId) {
        Long memberId = auditorHolder.get();

        memberRepository.findById(memberId)
                        .orElseThrow(() -> new MemberNotFoundException(memberId));

        GroupEntity groupEntity = groupRepository.findById(groupId)
                                                 .orElseThrow(() -> new GroupNotFoundException(groupId));

        if (memberGroupRepository.findByGroupId(groupId)
                                 .stream()
                                 .anyMatch(memberGroupEntity -> memberGroupEntity.isEqualMemberId(memberId))) {
            return null;
        }
        memberGroupRepository.save(MemberGroupConverter.to(memberId, groupId));
        return new GroupResponse(groupEntity.getId(), groupEntity.getGroupName(), groupEntity.isSecret());
    }
}
